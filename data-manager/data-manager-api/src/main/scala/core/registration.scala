package core

import java.net.URI

import core.MessengerActor.SendMessage
import parallelai.wallet.persistence.{ClientApplicationDAO, UserAccountDAO}
import akka.actor.{Props, ActorLogging, ActorRef, Actor}
import com.parallelai.wallet.datamanager.data._
import scala.concurrent.{ExecutionContext, Future}
import scala.async.Async._
import java.util.UUID
import spray.json._
import parallelai.wallet.entity.{ClientApplication, UserAccount}
import com.parallelai.wallet.datamanager.data.RegistrationValidation
import com.parallelai.wallet.datamanager.data.RegistrationResponse
import scala.Some
import parallelai.wallet.entity.UserAccount
import com.parallelai.wallet.datamanager.data.RegistrationValidationResponse
import com.parallelai.wallet.datamanager.data.RegistrationRequest
import org.apache.commons.lang.math.RandomUtils
import org.apache.commons.lang.RandomStringUtils
import javax.management.InvalidApplicationException
import com.parallelai.wallet.datamanager.data.RegistrationValidation
import com.parallelai.wallet.datamanager.data.RegistrationResponse
import parallelai.wallet.entity.ClientApplication
import scala.Some
import parallelai.wallet.entity.UserAccount
import com.parallelai.wallet.datamanager.data.RegistrationValidationResponse
import com.parallelai.wallet.datamanager.data.RegistrationRequest
import com.parallelai.wallet.datamanager.data.RegistrationValidation
import com.parallelai.wallet.datamanager.data.RegistrationResponse
import parallelai.wallet.entity.ClientApplication
import scala.Some
import parallelai.wallet.entity.UserAccount
import com.parallelai.wallet.datamanager.data.RegistrationValidationResponse
import com.parallelai.wallet.datamanager.data.RegistrationRequest
import scala.concurrent.Future.successful

/**
 * We use the companion object to hold all the messages that the ``RegistrationActor``
 * receives.
 */
object RegistrationActor {

  def props( userAccountDAO : UserAccountDAO, clientApplicationDAO : ClientApplicationDAO, messengerActor: ActorRef) : Props =
    Props( classOf[RegistrationActor], userAccountDAO, clientApplicationDAO, messengerActor)

  sealed trait RegistrationError
  case object ApplicationAlreadyRegistered extends RegistrationError
  case object UserAlreadyRegistered extends RegistrationError
  case class InvalidRequest(reason: String) extends RegistrationError
  case object InvalidValidationCode extends RegistrationError
  case class InternalError(throwable: Throwable) extends RegistrationError

  implicit object RegistrationErrorJsonFormat extends RootJsonFormat[RegistrationError] {
    def write(error: RegistrationError) =  error match {
      case ApplicationAlreadyRegistered => JsString("ApplicationAlreadyRegistered")
      case UserAlreadyRegistered => JsString("UserAlreadyRegistered")
      case InvalidValidationCode => JsString("InvalidValidationCode")
      case InvalidRequest(reason) => JsObject(
        "type" -> JsString("InvalidRequest"),
        "data" -> JsObject { "reason" -> JsString(reason)  }
      )
      case InternalError(throwable) => JsObject(
        "type" -> JsString("InternalError"),
        "data" -> JsObject (
          "errorClass" -> JsString(throwable.getClass.getName),
          "errorMessage" -> JsString(throwable.getMessage)
        )
      )
    }
    def read(value: JsValue) = {
      value match {
        case JsString("InvalidValidationCode") => InvalidValidationCode
        case JsObject(attributes) => {
          attributes.get("type") match {
            case Some(JsString("InvalidRequest")) =>
              val data = attributes.get("data") map { _.asInstanceOf[JsObject] }
              data match {
                case None => InvalidRequest("Unknown reason")
                case Some(errorData) =>
                  val reason = errorData.fields.get("reason") map { _.toString } getOrElse ("Unknown")
                  InvalidRequest(reason)
              }
            case Some(JsString("InternalError")) =>
              val data = attributes.get("data") map { _.asInstanceOf[JsObject] }
              data match {
                case None => InternalError (new Exception (s"Exception of class Unknown, message: Unknown") )
                case Some(errorData) =>
                  val errorClass = errorData.fields.get("errorClass") map { _.toString } getOrElse ("Unknown")
                  val errorMessage = errorData.fields.get("errorMessage") map { _.toString } getOrElse ("Unknown")
                  InternalError (new Exception (s"Exception of class $errorClass, message: '$errorMessage'") )
              }
            case _ => InternalError(new Exception(s"Unmapped error '$value'"))
          }
        }
        case JsString(errorName) => InternalError(new Exception(s"Unmapped error '$errorName'"))
        case _ => InternalError(new Exception(s"Unmapped error '$value'"))
      }

    }
  }

  case class AddApplication(applicationId: ApplicationID, accountId: UserID)

  def withValidations[Request, Error, Response](request: Request)(validations: (Request => Future[Option[Error]])*)
                                               (successFlow: Request => Future[Either[Error, Response]])
                                               (implicit executionContext: ExecutionContext)
    : Future[Either[Error, Response]] = {
    val validationResult = validations.foldLeft[Future[Option[Error]]](successful(None)) {
      case (currStatus, currFunction) =>
        currStatus flatMap { _ match {
          case Some(_) => currStatus
          case None => currFunction(request)
        }
        }
    }
    validationResult flatMap {
      _ match {
        case Some(validationError) => successful(Left(validationError))
        case None => successFlow(request)
      }
    }
  }

  def newActivationCode: String = {
    RandomStringUtils.randomAlphanumeric(6)
  }
}

/**
 * Registers the users. Replies with
 */
class RegistrationActor(userAccountDAO : UserAccountDAO, clientApplicationDAO : ClientApplicationDAO,
                        messengerActor: ActorRef) extends Actor with ActorLogging {
  import RegistrationActor._

  import context.dispatcher

  // notice that we don't actually perform any DB operations.
  // that's for another template
  def receive: Receive = {
    case request: RegistrationRequest =>  replyToSender { registerUser(request) }
    case request: AddApplication =>  replyToSender { registerApplication(request) }
    case validation: RegistrationValidation => replyToSender { validateUser(validation) }
  }

  def registerUser(registrationRequest: RegistrationRequest): Future[Either[RegistrationError, RegistrationResponse]] =
    withValidations(registrationRequest) ( validUserIdentification, applicationNotRegistered, noActiveAccountForMsisdnOrEmail ) { request =>

      log.debug("Creating new account for request {}", request)
      val account = UserAccount( id = UUID.randomUUID(), email = request.email, msisdn = request.msisdn )

      val activationCode = newActivationCode

      val firstApplication = ClientApplication(request.applicationId, account.id, activationCode)

      userAccountDAO.insertNew(account, firstApplication)

      (account.id, activationCode)

      successful( activateApplication(request.applicationId, account.id, request, activationCode) )
    }


  def registerApplication(registrationRequest: AddApplication): Future[Either[RegistrationError, RegistrationResponse]] =
    withValidations(registrationRequest) ( applicationNotRegistered ) { request =>

      userAccountDAO.getById(request.accountId) map {
        _ match {
          case None => Left(InvalidRequest(s"User with ID ${registrationRequest.accountId} doesn't exist"))

          case Some(userAccount) =>
            val activationCode = newActivationCode

            clientApplicationDAO.insertNew(ClientApplication(registrationRequest.applicationId, registrationRequest.accountId, activationCode))

            activateApplication(
              registrationRequest.applicationId,
              userAccount.id,
              UserContacts(userAccount.email, userAccount.msisdn),
              activationCode
            )
        }
      }
    }

  def validateUser(validation: RegistrationValidation) : Future[Either[RegistrationError, RegistrationValidationResponse]] = async {
    val clientAppOption = await {
      clientApplicationDAO.getById(validation.applicationId)
    }

    clientAppOption match {
      case None => Left(InvalidRequest(s"Unable to find client application with ID '${validation.applicationId}'"))

      case Some(clientApplication) =>
        // I don't care if the user validates twice. I just check the validation code
        if(clientApplication.activationCode == validation.validationCode) {
          val userOpt = await { userAccountDAO.getByApplicationId(validation.applicationId) }

          userOpt map { user => 
            clientApplicationDAO.update(clientApplication copy (active = true))
            userAccountDAO.setActive(user.id)

            Right(RegistrationValidationResponse(validation.applicationId, user.id))
          } getOrElse {
            Left(InternalError(new IllegalStateException(s"Unable to find user for valid application ID '${validation.applicationId}'")))
          }
        } else {
          Left(InvalidValidationCode)
        }
    }

  }

  private def activateApplication(applicationId: ApplicationID, accountId: UserID, userContacts: WithUserContacts, validationCode: String) : Either[RegistrationError, RegistrationResponse] = {
    log.info("Validation code for registration request of application {} is '{}'", applicationId, validationCode)

    val activationMessage = s"Welcome to Karedo, your activation code is $validationCode"

    if(userContacts.msisdn.isDefined) {
      messengerActor ! SendMessage(URI.create(s"sms:${userContacts.msisdn.get}"), activationMessage)
      Right(RegistrationResponse(applicationId, "msisdn", userContacts.msisdn.get))
    }
    else {
      messengerActor ! SendMessage(URI.create(s"mailto:${userContacts.email.get}"), activationMessage, "Welcome to Karedo")
      Right(RegistrationResponse(applicationId, "email", userContacts.email.get))
    }
  }

  def replyToSender[T <: Any](response: Future[Either[RegistrationError, T]]): Unit = {
    val replyTo = sender

    response recover {
      case t =>
        log.warning("Internal error: {}", t)
        Left(InternalError(t))
    } foreach {
      response : Either[RegistrationError, T] =>
        replyTo ! response
    }
  }

  def applicationNotRegistered(withApplicationId: { def applicationId: ApplicationID }) : Future[Option[RegistrationError]] =
    clientApplicationDAO.getById(withApplicationId.applicationId) map { _  map { _ => ApplicationAlreadyRegistered } }

  def validUserIdentification(userIdentification : WithUserContacts) : Future[Option[RegistrationError]] =
    successful { if(userIdentification.isValid) None else Some(InvalidRequest("Invalid user identification")) }

  // This validation has side effects!!!
  def noActiveAccountForMsisdnOrEmail(userIdentification : WithUserContacts) : Future[Option[RegistrationError]] =
    userAccountDAO.findByAnyOf(None, userIdentification.msisdn, userIdentification.email) map { existingAccountOp =>
      if (existingAccountOp.filter( { _.active } ).isDefined) {
        Some(UserAlreadyRegistered)
      } else {
        if (existingAccountOp.isDefined) {
          log.debug("Removing account of already registered but not active user {}", existingAccountOp.get)
          userAccountDAO.delete(existingAccountOp.get.id)
        }

        None
      }
    }

}
