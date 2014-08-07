package core

import akka.actor.{Props, ActorLogging, Actor}
import akka.actor.Actor.Receive
import parallelai.wallet.persistence.{ClientApplicationDAO, UserAccountDAO}
import com.parallelai.wallet.datamanager.data._
import parallelai.wallet.entity.{UserPersonalInfo, AccountSettings, UserAccount}
import scala.concurrent.Future

object EditAccountActor {
  def props(userAccountDAO : UserAccountDAO, clientApplicationDAO : ClientApplicationDAO): Props =
    Props(classOf[EditAccountActor], userAccountDAO, clientApplicationDAO)

  case class GetAccount(accountId: UserID)

  case class FindAccount(applicationId: Option[UserID], msisdn: Option[String], email: Option[String]) extends WithUserContacts

  case class UpdateAccount(userAccount: UserProfile)

  case class CheckAccountPassword(accountId: UserID, password: String)
}

import EditAccountActor._

class EditAccountActor(userAccountDAO : UserAccountDAO, clientApplicationDAO : ClientApplicationDAO) extends Actor with ActorLogging {
  import context.dispatcher

  def replyToSender[T](responseFuture: Future[T]): Unit = {
    val requester = sender
    responseFuture onSuccess  { case response =>
      log.info("Completed, replying to {} with response {}", requester.path, response)
      requester ! response
    }
  }

  override def receive: Receive = {
    case GetAccount(accountId) =>
      log.info("Trying to get account with account ID {}, sender is {}", accountId, sender)
      replyToSender {
        userAccountDAO.getById(accountId) map { _ map { userAccountToUserProfile } }
      }

    case FindAccount(applicationIdOp, msisdnOp, emailOp) =>
      log.info("Trying to find account with msisdn {} or email {} sender is {}", msisdnOp, emailOp, sender)
      replyToSender {
        userAccountDAO.findByAnyOf(applicationIdOp, msisdnOp, emailOp) map { _ map { userAccountToUserProfile } }
      }

    case UpdateAccount(userProfile) =>
      log.info("Trying to update account with id {}", userProfile.info.userId)
      userAccountDAO.update(userProfileToUserAccount(userProfile))

    case CheckAccountPassword(accountId, password) =>
      log.info("Validationg password for account with id {}", accountId)
      replyToSender {
        userAccountDAO.getById(accountId) map { _ match {
          case Some(account) => account.password == Some(password)
          case None => false
          }
        }
      }
  }

  def userAccountToUserProfile(userAccount: UserAccount): UserProfile =
    UserProfile(
      UserInfo(
        userId = userAccount.id,
        fullName = userAccount.personalInfo.name,
        postCode = userAccount.personalInfo.postCode,
        birthDate = userAccount.personalInfo.birthDate,
        country = None,
        email = userAccount.email,
        msisdn = userAccount.msisdn,
        gender = userAccount.personalInfo.gender
      ),
      UserSettings(
        userAccount.settings.maxMessagesPerWeek,
        userAccount.password.getOrElse("")
      )
    )

  def userProfileToUserAccount(userProfile: UserProfile): UserAccount =
    UserAccount(
      userProfile.info.userId,
      userProfile.info.msisdn,
      userProfile.info.email,
      Some(userProfile.settings.password),
      UserPersonalInfo(
        userProfile.info.fullName,
        userProfile.info.postCode,
        userProfile.info.birthDate,
        userProfile.info.gender
      ),
      AccountSettings(userProfile.settings.maxAdsPerWeek)
    )
}
