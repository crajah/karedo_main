package api

import spray.routing.{HttpService, Directives}
import scala.concurrent.ExecutionContext
import akka.actor.ActorRef
import core.{User, RegistrationActor}
import akka.util.Timeout
import RegistrationActor._
import spray.http._
import spray.http.StatusCodes._
import com.parallelai.wallet.datamanager.data._
import ApiDataJsonProtocol._
import com.wordnik.swagger.annotations.{Api => ApiDoc, _}
import core.RegistrationActor.InternalError
import com.parallelai.wallet.datamanager.data._
import akka.pattern.ask
import scala.concurrent.duration._
import com.parallelai.wallet.datamanager.data.ApiDataJsonProtocol._
import apiPaths._

trait RegistrationServiceActorComponent extends Directives with DefaultJsonFormats {
  protected def registration: ActorRef
  protected implicit val executionContext: ExecutionContext

  implicit val timeout = Timeout(2.seconds)

  implicit object EitherErrorSelector extends ErrorSelector[RegistrationError] {
    def apply(error: RegistrationError): StatusCode = error match {
      case InvalidRequest => BadRequest
      case InvalidValidationCode => Unauthorized
      case InternalError(_) => InternalServerError
    }
  }
}

@ApiDoc(value = "/register", description = "User Registration Operations")
trait Register extends RegistrationServiceActorComponent {
  @ApiOperation(httpMethod = "POST", response = classOf[RegistrationResponse], value = "Returns a pet based on ID")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "request", required = true, dataType = "com.parallelai.wallet.datamanager.data.RegistrationResponse",
      paramType = "body", value = "Details of the request")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid Parameters")
  ))
  def register = path(PATH_REGISTER) {
    post {
      handleWith {
        registrationRequest: RegistrationRequest => (registration ? registrationRequest).mapTo[Either[RegistrationError, RegistrationResponse]]
      }
    }
  }
}

@ApiDoc(value = "/register/validate", description = "User Registration Operations")
trait ValidateRegistration extends RegistrationServiceActorComponent {
  @ApiOperation(httpMethod = "POST", response = classOf[RegistrationResponse], value = "Returns a pet based on ID")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "request", required = true, dataType = "com.parallelai.wallet.datamanager.data.RegistrationValidationResponse",
      paramType = "path", value = "Details of the request")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid Parameters"),
    new ApiResponse(code = 401, message = "Wrong validation code")
  ))
  def validateRegistration = path(PATH_REGISTER / PATH_VALIDATE_REGISTRATION) {
    post {
      handleWith {
        registrationValidation: RegistrationValidation =>  (registration ? registrationValidation).mapTo[Either[RegistrationError, RegistrationValidationResponse]]
      }
    }
  }

}

class RegistrationService(protected val registration: ActorRef)(protected implicit val executionContext: ExecutionContext)
  extends Directives with Register with ValidateRegistration {

  val route =
     register ~
     validateRegistration
}
