package api

import java.util.UUID

import akka.actor.ActorRef
import akka.util.Timeout
import com.parallelai.wallet.datamanager.data._
import core.EditAccountActor.{FindAccount, GetAccount, GetAccountPoints, UpdateAccount}
import core.RegistrationActor
import core.RegistrationActor.{AddApplication, _}
import spray.http.StatusCodes._
import spray.http._
import spray.routing.Directives

import scala.concurrent.ExecutionContext

class BrandService(brandActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  import akka.pattern.ask
  import com.parallelai.wallet.datamanager.data.ApiDataJsonProtocol._

import scala.concurrent.duration._
  implicit val timeout = Timeout(20.seconds)

  implicit object EitherErrorSelector extends ErrorSelector[RegistrationError] {
    def apply(error: RegistrationError): StatusCode = error match {
      case InvalidRequest(reason) => BadRequest
      case ApplicationAlreadyRegistered => BadRequest
      case UserAlreadyRegistered => BadRequest
      case InvalidValidationCode => Unauthorized
      case InternalError(_) => InternalServerError
    }
  }

  val route =
    path("brand") {
      post {
        handleWith {
          brandData: BrandData =>
            //(brandActor ? brandData).mapTo[Option[UUIDData]]
            UUIDData(UUID.randomUUID())
        }
      }
    }
}
