package karedo.util

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import karedo.entity
import karedo.entity._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by pakkio on 10/9/16.
  */
import karedo.util.DateTimeJsonHelper._

trait KaredoJsonHelpers
  extends DefaultJsonProtocol
  with SprayJsonSupport {

  implicit val jsonChannel = jsonFormat2(Channel)
  implicit val jsonBacon = jsonFormat1(entity.Beacon)
  implicit val jsonImage = jsonFormat5(ImageAd)
  implicit val jsonVideo = jsonFormat6(VideoAd)
  implicit val jsonAd = jsonFormat13(Ad)
  implicit val jsonAds = jsonFormat2(Ads)

  implicit val jsonAction = jsonFormat2(Action)
  implicit val jsonMessage = jsonFormat6(UserMessages)


  case class Kar166Request(entries: List[UserAd] = List())
  implicit val jsonUserAd = jsonFormat10(UserAd)
  implicit val jsonKar166Request = jsonFormat1(Kar166Request)

  implicit val jsonUserProfile = jsonFormat12(UserProfile)

  /*
  {
    "application_id": "{{application_id}}",
    "session_id": "{{session_id}}",
    "profile": {
        "gender":"",
        "first_name":"",
        "last_name": "",
        "yob": "",
        "kids": "",
        "income": "",
        "location": "",
        "opt-in": "",
        "third-party": ""
    }
}
   */
  case class Kar189ReqProfile(gender:Option[String], first_name:Option[String], last_name: Option[String],
                              yob: Option[Int], kids: Option[Int], income: Option[Int], location: Option[Boolean],
                              opt_in: Option[Boolean], third_party: Option[Boolean])
  case class Kar189Req(application_id: String, session_id: String, profile: Kar189ReqProfile )
  implicit val jsonKar189ReqProfile = jsonFormat9(Kar189ReqProfile)
  implicit val jsonKar189Req:RootJsonFormat[Kar189Req] = jsonFormat3(Kar189Req)
}
