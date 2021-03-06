package parallelai.wallet.persistence

import java.util.UUID

import parallelai.wallet.entity.KaredoSales

trait KaredoSalesDAO {
  def getAcceptedOffers(uid: UUID): List[KaredoSales]


  def insertNew(newData: KaredoSales): Option[UUID]
  def findById(id: UUID): Option[KaredoSales]
  def findByOffer(offerId: UUID): Option[KaredoSales]
  def findByCode(code: String): Option[KaredoSales]
  def consume(code: String): Option[KaredoSales]
  def complete(id : UUID): Option[KaredoSales]

}
