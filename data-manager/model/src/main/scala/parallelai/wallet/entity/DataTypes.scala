package parallelai.wallet.entity

/**
 * These are the types we are finding on the DAO level
 */

import java.io.InputStream
import java.util.UUID
import com.novus.salat.annotations.Key
import org.bson.types.ObjectId
import org.joda.time.DateTime

case class EmailUserLookup(email: String, userId: UUID)

case class MsisdnUserLookup(msisdn: String, userId: UUID)

case class ClientApplication(id: UUID, accountId: UUID, activationCode: String, active: Boolean = false)

case class UserPersonalInfo(name: String, postCode: Option[String] = None, birthDate: Option[DateTime] = None, gender: Option[String] = None)

case class AccountSettings(maxMessagesPerWeek: Int)

case class UserAccount(id: UUID, msisdn: Option[String], email: Option[String],
                       personalInfo: UserPersonalInfo = defaultUserPersonalInfo,
                       settings: AccountSettings = defaultAccountSettings,
                       active: Boolean = false,
                       totalPoints: Long = 0,
                       subscribedBrands: List[UUID] = List(),
                       password: Option[String] = None)

case class UserAds(userId: UUID, readAds: Set[UUID])

case class UserOffers(userId: UUID, rewards: Set[UUID])

case class Offer(@Key("_id") id: UUID = UUID.randomUUID(), name: String = "", brandId: UUID, description: Option[String],
                 imagePath: Option[String], qrCodeId: Option[UUID], value: Option[Long])

case class AdvertisementDetail(@Key("_id") id: UUID = UUID.randomUUID(), publishedDate: DateTime = new DateTime(), text: String = "", imageIds: List[String] = List(), value: Int = 0)

case class Brand(@Key("_id") id: UUID = UUID.randomUUID(), name: String = "", iconId: String, ads: List[AdvertisementDetail] = List())

case class KaredoLog(@Key("_id") id: UUID = UUID.randomUUID, user: UUID, brand: UUID, offer: UUID, text: String )

case class Hint(@Key("_id") id: UUID = UUID.randomUUID(), userId: UUID, brandId: UUID, ad: UUID, score: Double)

//case class UserBrandInteraction(@Key("_id") id: UUID = UUID.randomUUID(), userId: UUID, )

case class MediaContentDescriptor(name: String, contentType: String, id: String = "")

case class MediaContent(descriptor: MediaContentDescriptor, inputStream: InputStream)

case class SuggestedAdForUsersAndBrandModel(id: UUID, name: String, iconId: String)

case class UserSession(sessionId: UUID, userId: UUID, applicationId: UUID)

case class UserAuthContext(userId: UUID, activeApps: Seq[UUID]) {
  def isValidAppForUser(appId: UUID): Boolean = activeApps.contains(appId)
}