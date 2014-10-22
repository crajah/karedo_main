package api


import java.io.ByteArrayInputStream
import java.util.UUID

import akka.actor.ActorRef
import akka.util.Timeout
import com.parallelai.wallet.datamanager.data.ApiDataJsonProtocol._
import com.parallelai.wallet.datamanager.data.{BrandData, BrandResponse, ListBrandsAdverts, _}
import core.BrandActor.BrandError
import core.{SuccessResponse, ResponseWithFailure}
import spray.http.MultipartFormData

import spray.routing.Directives

import scala.concurrent.ExecutionContext


class BrandService(brandActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats with ApiErrorsJsonProtocol {


  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout = Timeout(20.seconds)


  val routebrand =
    path("brand") {


      post {
        handleWith {
          brandData: BrandData =>
            (brandActor ? brandData).mapTo[ResponseWithFailure[BrandError, BrandResponse]]
        }
      } ~
        get {
          rejectEmptyResponse {

            complete {


              (brandActor ? ListBrands).mapTo[List[BrandRecord]]
            }
          }
        }

    }

  val routebrandWithId =

    path("brand" / JavaUUID) {

      brandId: UUID =>
      rejectEmptyResponse {
        get {
          complete {

            (brandActor ? BrandIDRequest(brandId)).mapTo[ResponseWithFailure[BrandError, BrandRecord]]
          }
        }

      } ~ delete {
        complete {
          (brandActor ? DeleteBrandRequest(brandId)).mapTo[ResponseWithFailure[BrandError, String]]
        }
      }
    }

  val routebrandWithIdAdvert =

    path("brand" / JavaUUID / "advert") {

      brandId: UUID =>
      rejectEmptyResponse {
        get {
          complete {

            (brandActor ? ListBrandsAdverts(brandId)).mapTo[ResponseWithFailure[BrandError, List[AdvertDetailResponse]]]
          }
        }
      } ~ post {
        handleWith {
          request: AdvertDetail => {
            (brandActor ? AddAdvertCommand(brandId, request.text, request.imageIds, request.value)).mapTo[ResponseWithFailure[BrandError, AdvertDetailResponse]]
          }
        }
      }
    }

  val routebrandWithIdAdvertWithId =

    path("brand" / JavaUUID / "advert" / JavaUUID) {

      (brandId: UUID, advId: UUID) =>
        delete {
          complete {
          (brandActor ? DeleteAdvRequest (brandId, advId) ).mapTo[ResponseWithFailure[BrandError, String]]


          }
        }
    }

  val routeMedia =
    (path("media") & post) {
      entity(as[MultipartFormData]) { formData: MultipartFormData =>

        complete {
          val details = formData.fields
          "{ \"mediaId\":\"25\" }"

        }
      }
    }


  val route = routebrand ~ routebrandWithId ~ routebrandWithIdAdvert ~ routebrandWithIdAdvertWithId ~ routeMedia
}
