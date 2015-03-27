package parallelai.wallet.persistence.mongodb

import java.util.UUID
import parallelai.wallet.entity.KaredoTypes.KaredoPoints
import parallelai.wallet.entity._
import com.novus.salat.annotations._

object MongoUserApplicationInfo {
  def fromClientApplication(clientApp: ClientApplication): MongoUserApplicationInfo =
    MongoUserApplicationInfo(clientApp.id, clientApp.activationCode, clientApp.active)
}

case class SubscribedBrands(id:UUID)

case class MongoUserApplicationInfo(@Key("_id")id: UUID, activationCode: String, active: Boolean = false) {
  def toClientApplication(accountId: UUID) = ClientApplication(id, accountId, activationCode, active)
}
case class MongoUserAccount(
                             @Key("_id") id: UUID,
                             msisdn: Option[String],
                             email: Option[String],
                             userType: String,
                             personalInfo: UserPersonalInfo = defaultUserPersonalInfo,
                             settings: AccountSettings = defaultAccountSettings,
                             password: Option[String] = None,
                             active: Boolean = false,
                             totalPoints: KaredoPoints = 0,
                             subscribedBrands: List[UUID] = List[UUID](),
                             applications: List[MongoUserApplicationInfo] = List[MongoUserApplicationInfo]()
                             ) {
  def toUserAccount : UserAccount = UserAccount(id, msisdn, email, userType, personalInfo, settings, active, totalPoints, subscribedBrands, password)

  def toClientApplicationList : List[ClientApplication] = applications map { _.toClientApplication(id)  }
}
