package karedo.entity

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import karedo.util.json.JodaImplicits
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by charaj on 20/01/2017.
  */
trait JsonDbImplicits extends DefaultJsonProtocol
  with SprayJsonSupport with JodaImplicits {

  implicit val json_Beacon = jsonFormat1(Beacon)
  implicit val json_ImageAd = jsonFormat5(ImageAd)
  implicit val json_VideoAd = jsonFormat6(VideoAd)
  implicit val json_Ad = jsonFormat13(Ad)
  implicit val json_Ads:RootJsonFormat[Ads] = jsonFormat2(Ads)

  implicit val json_EmailVerify:RootJsonFormat[EmailVerify] = jsonFormat3(EmailVerify)

  implicit val json_KaredoChange:RootJsonFormat[KaredoChange] = jsonFormat7(KaredoChange)

  implicit val json_MobileSale:RootJsonFormat[MobileSale] = jsonFormat2(MobileSale)

  implicit val json_Pref:RootJsonFormat[Pref] = jsonFormat6(Pref)

  implicit val json_Sale:RootJsonFormat[Sale] = jsonFormat13(Sale)

  implicit val json_Mobile = jsonFormat5(Mobile)
  implicit val json_Email = jsonFormat5(Email)
  implicit val json_UserAccount:RootJsonFormat[UserAccount] = jsonFormat9(UserAccount)

  implicit val json_Channel = jsonFormat2(Channel)
  implicit val json_UserAd:RootJsonFormat[UserAd] = jsonFormat10(UserAd)

  implicit val json_UserApp:RootJsonFormat[UserApp] = jsonFormat5(UserApp)

  implicit val json_UserEmail:RootJsonFormat[UserEmail] = jsonFormat5(UserEmail)

  implicit val json_UserIntent = jsonFormat2(UserIntent)
  implicit val json_IntentUnit:RootJsonFormat[IntentUnit] = jsonFormat6(IntentUnit)

  implicit val json_ChannelUnit = jsonFormat4(ChannelUnit)
  implicit val json_InteractUnit = jsonFormat10(InteractUnit)
  implicit val json_UserInteraction:RootJsonFormat[UserInteraction] = jsonFormat4(UserInteraction)

  implicit val json_FavouriteUnit = jsonFormat6(FavouriteUnit)
  implicit val json_UserFavourite:RootJsonFormat[UserFavourite] = jsonFormat2(UserFavourite)

  implicit val json_UrlMagic:RootJsonFormat[UrlMagic] = jsonFormat4(UrlMagic)

  implicit val json_UrlAccess:RootJsonFormat[UrlAccess] = jsonFormat4(UrlAccess)

  implicit val json_HashedAccount:RootJsonFormat[HashedAccount] = jsonFormat2(HashedAccount)

  implicit val json_UserKaredos:RootJsonFormat[UserKaredos] = jsonFormat4(UserKaredos)

  implicit val json_Action = jsonFormat2(Action)
  implicit val json_UserMessages:RootJsonFormat[UserMessages] = jsonFormat6(UserMessages)

  implicit val json_UserMobile:RootJsonFormat[UserMobile] = jsonFormat5(UserMobile)

  implicit val json_UserPrefData = jsonFormat4(UserPrefData)
  implicit val json_UserPrefs:RootJsonFormat[UserPrefs] = jsonFormat4(UserPrefs)

  implicit val json_UserProfile:RootJsonFormat[UserProfile] = jsonFormat13(UserProfile)

  implicit val json_UserSession:RootJsonFormat[UserSession] = jsonFormat5(UserSession)

}


