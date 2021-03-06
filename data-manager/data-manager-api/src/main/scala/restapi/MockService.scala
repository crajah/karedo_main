package restapi

import java.util.{Date, UUID}

import akka.actor.ActorRef
import akka.event.slf4j.Logger
import akka.util.Timeout
import com.parallelai.wallet.datamanager.data.ApiDataJsonProtocol._
import com.parallelai.wallet.datamanager.data._
import core.OtherActor._
import core.RegistrationActor.RegistrationError
import core.ResponseWithFailure
import shapeless.HNil
import spray.http.StatusCodes
import spray.routing.{Route, Directive, Directives}
import akka.pattern.ask

import restapi.security.AuthorizationSupport
import core.security.UserAuthService


import scala.concurrent.ExecutionContext
import scala.collection.mutable.HashMap

object Session {
  val MAXMILLIS = 2000
  val sessions = new HashMap[String,Date]
  def newId: String = {
    val ret=UUID.randomUUID().toString
    sessions.put(ret,new Date())
    ret
  }
  def validateId(id:String): Boolean = {
    sessions.get(id) match {
      case None => false
      case Some(d) => {
        if( (new Date().getTime - d.getTime) > MAXMILLIS){
          println("Removed expired session")
          sessions.remove(id)
          false
        }
        else {
          sessions.update(id,new Date())
          true
        }
      }
    }

  }
}


object MockService {
  val logger = Logger("OtherService")
}

class MockService(otherActor: ActorRef,
    override protected val userAuthService: UserAuthService)
    (implicit executionContext: ExecutionContext)
  extends Directives 
  with DefaultJsonFormats 
  with ApiErrorsJsonProtocol 
  with AuthorizationSupport {

  import scala.concurrent.duration._

  val q="\""

  implicit val timeout = Timeout(20.seconds)

  val route= route56 ~ /* 57  route59 ~ */
    route63 ~ route79 ~ route80 ~ route81 ~ route82 ~ route92

  lazy val route56: Route =
    // PARALLELAI-56API: User Ads Interaction")
    // r = post("user/"+userId+"/interaction/advert/"+advertId)
    path("user" / JavaUUID / "interaction" / "advert" / JavaUUID) {
      (user, advert) =>
        {
          userAuthorizedFor(canAccessUser(user))(executionContext) { userAuthContext =>
            post {
              handleWith((s: String) =>
                s"{${q}userId${q}: ${q}$user${q},${q}userTotalPoints${q}:${q}500${q}}")
            }
          }
        }
    }

  //title("PARALLELAI-59API: Get Next N Ads For User For Brand")
  //r = get("account/"+userId+"/brand/"+brandId+"/ads?max=5")
//  lazy val route59: Route =
//    path("account" / JavaUUID / "brand" / JavaUUID / "ads") {
//      (user, brand) =>
//        {
//          userAuthorizedFor(canAccessUser(user))(executionContext) { userAuthContext =>
//
//            get {
//              parameters('max) { max =>
//                rejectEmptyResponse {
//                  complete {
//                    """[
//                  |{"id":"f135e16d-da0c-4b12-ab7f-d19ae1c7abe3","name":"name1","iconId":"5678666666"},
//                  |{"id":"f135e16d-da0c-4b12-ab7f-d19ae1c7abe3","name":"name2","iconId":"5678666667"}
//                  |]
//                """.stripMargin
//                  }
//                }
//              }
//            }
//          }
//        }
//    }



  // title("PARALLELAI-63API: User Buy Offer")
  // r = post("user/"+userId+"/interaction/offer/"+offerId, { "interactionType":  "BUY"})
  lazy val route63 : Route =
    path("user" / JavaUUID / "interaction" / "offer" / JavaUUID) {
      (user, offer) =>
        {
          userAuthorizedFor(canAccessUser(user))(executionContext) { userAuthContext =>
            post {
              handleWith(
                (s: String) =>
                  s"{${q}userId${q}: ${q}$user${q},${q}userTotalPoints${q}:${q}500${q}}")

            }
          }
        }
    }



  // title("PARALLELAI-79API: Show Pending Ads Per User Per Brand")
  // r = get("account/"+userId+"/brand/"+brandId+"/pendingAds")
  lazy val route79 : Route =
    path("account" / JavaUUID / "brand" / JavaUUID / "pendingAds") {
      (user, brand) =>
        {
          userAuthorizedFor(canAccessUser(user))(executionContext) { userAuthContext =>
            get {
              complete {
                """[
              |{"id":"f135e16d-da0c-4b12-ab7f-d19ae1c7abe3","text":"name1","imageId":"5678666666","value":"0.5555"},
              |{"id":"f135e16d-da0c-4b12-ab7f-d19ae1c7abe3","text":"name2","imageId":"5678666667","value":"0.7777"}
              |]
            """.stripMargin
              }
            }
          }
        }
    }

  // title("PARALLELAI-80API: List Offers For User")
  // r = get("user/"+userId+"/recommendedOffers?start=0&maxCount=5")
  lazy val route80 : Route =
    path("user" / JavaUUID / "recommendedOffers") {
      (user) =>
        {
          userAuthorizedFor(canAccessUser(user))(executionContext) { userAuthContext =>
            get {

              parameters('start, 'maxCount) { (start, maxCount) =>
                rejectEmptyResponse {
                  complete {
                    """[
                  |{"name":"offername","brandId":"f135e16d-da0c-4b12-ab7f-d19ae1c7abe3","desc":"description","imageId":"imageid1","qrCodeId":"qrCodeId","value":"0.5555"},
                  |{"name":"offername2","brandId":"f135e16d-da0c-4b12-ab7f-d19ae1c7abe3","desc":"description2","imageId":"imageid1","qrCodeId":"qrCodeId","value":"0.5555"}
                  |]
                """.stripMargin

                  }
                }
              }
            }
          }
        }
    }

  //         title("PARALLELAI-81API: User Offer Interaction (like-dislike-share)")
  // r = post("user/"+userId+"/interaction/offer/"+offerId, { "interactionType":  "LIKE"})
  lazy val route81 : Route =
    path("user" / JavaUUID / "interaction" / "offer" / JavaUUID) {
      (user, offer) =>
        {
          userAuthorizedFor(canAccessUser(user))(executionContext) { userAuthContext =>
            post {
              handleWith {
                (s: String) =>
                  s"{${q}userId${q}: ${q}$user${q},${q}userTotalPoints${q}:${q}500${q}}"
              }
            }
          }
        }
    }
  // title("PARALLELAI-82API: Get Offer Details")
  // r = get("offer/"+offerId)
  lazy val route82 : Route =
    path("offer" / JavaUUID) {
      (offer) =>
        {
          userAuthorizedFor(isLoggedInUser)(executionContext) { userAuthContext =>
            get {
              complete {
                """
              | {"name":"offername","brandId":"f135e16d-da0c-4b12-ab7f-d19ae1c7abe3","desc":"description","imageId":"imageid1","qrCodeId":"qrCodeId","value":"0.5555"}
            """.stripMargin
                //"name: %s, brandId: %s, desc: %s, imageId: %s, qrCodeId: %s, value: %s"
              }
            }
          }
        }
    }

  //         title("PARALLELAI-92API: Disable Offer")
  // r = delete("offer/"+offerId)
  lazy val route92 : Route =
    path("offer" / JavaUUID) {
      (offer) =>
        {
          userAuthorizedFor(isLoggedInUser)(executionContext) { userAuthContext =>
            delete {
              complete {
                "{}"
              }
            }
          }
        }
    }



}
