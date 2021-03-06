package parallelai.wallet.persistence

import java.util.UUID
import parallelai.wallet.entity.KaredoTypes.KaredoPoints
import parallelai.wallet.entity._

import scala.concurrent.Future

trait UserAccountDAO {
  def consume(userId: UUID, points: KaredoPoints) : Unit


  def getById(userId: UUID) : Option[UserAccount]

  def getByMsisdn(msisdn: String, mustBeActive: Boolean = false) : Option[UserAccount]

  def getByEmail(email: String, mustBeActive: Boolean = false) : Option[UserAccount]

  def getByApplicationId(applicationId: UUID, mustBeActive: Boolean = false) : Option[UserAccount]

  def insertNew(userAccount: UserAccount, firstApplication: ClientApplication) : Unit

  def addPoints(userId: UUID, points: KaredoPoints): Option[UserAccountTotalPoints]

  def updateBrandLastAction(userId: UUID, brandId: UUID): Option[SubscribedBrand]

  def update(userAccount: UserAccount): Unit

  def updateSubInfo(id: UUID, userInfo: UserPersonalInfo, personalSettings: AccountSettings): Unit

  def setActive(userId : UUID): Unit

  def setEmail(userId: UUID, email: String) : Unit

  def setMsisdn(userId: UUID, msisdn: String) : Unit

  def setPassword(userId: UUID, password: String) : Unit

  def checkPassword(userId: UUID, password: String): Boolean

  def findByAnyOf(applicationId: Option[UUID], msisdn: Option[String], email: Option[String]) : Option[UserAccount]

  def delete(userId: UUID) : Unit

  def addBrand(userId: UUID, brandId: UUID): Unit

  def getBrand(userId: UUID, brandId: UUID) : Option[SubscribedBrand]

  def deleteBrand(userId: UUID, brandId: UUID): Unit

  def listUserSubscribedBrands(userId: UUID): List[UUID]

}


