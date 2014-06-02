package parallelai.wallet.cassandra

import parallelai.wallet.entity._
import com.newzly.phantom.Implicits._
import com.datastax.driver.core.{ResultSet, Row}
import java.util.UUID

import scala.concurrent.{ Future => ScalaFuture }
import org.joda.time.DateTime
import com.datastax.driver.core.{ ResultSet, Row }
import com.newzly.phantom.iteratee.Iteratee


sealed class CustomerOfferRecord private() extends CassandraTable[CustomerOfferRecord, CustomerOffer] {
  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object title extends StringColumn(this) with Index[String]
  object description extends StringColumn(this)
  object id extends UUIDColumn(this) with PartitionKey[UUID] with PrimaryKey[UUID]
  object customerId extends UUIDColumn(this) with PrimaryKey[UUID]
  object retailOfferId extends UUIDColumn(this) with SecondaryKey[UUID]
  object title extends StringColumn(this)
  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object title extends StringColumn(this) with Index[String]
  object description extends StringColumn(this)
  object value extends DoubleColumn(this)
  object props extends MapColumn[CustomerOfferRecord, CustomerOffer, String, String](this)
  object timestamp extends DateTimeColumn(this) // with ClusteringOrder[_]
  object test extends OptionalIntColumn(this)


  override def fromRow(row: Row): CustomerOffer = {
    CustomerOffer(id(row), customerId(row), retailOfferId(row), title(row), value(row), props(row), timestamp(row), test(row));
  }
}

object CustomerOfferRecord extends CustomerOfferRecord with DBConnector {
  // TODO: Set the table name through configuration
  override lazy val tableName = "customer_offer"

  def insertNewRecord(ro: CustomerOffer): ScalaFuture[ResultSet] = {

    insert.value(_.id, ro.id)
      .value(_.customerId, ro.customerId)
      .value(_.retailOfferId, ro.retailOfferId)
      .value(_.title, ro.title)
      .value(_.value, ro.value)
      .value(_.props, ro.props)
      .value(_.timestamp, ro.timestamp)
      .value(_.test, ro.test)
      // TODO: Could use TTL here but should we?
      .ttl(4500)
      .future()
  }

  def getCustomerOfferById(id: UUID): ScalaFuture[Option[CustomerOffer]] = {
    select.where(_.id eqs id).one()
  }

  def getEntireTable: ScalaFuture[Seq[CustomerOffer]] = {
    select.fetchEnumerator() flatMap {
      res => res run Iteratee.collect()
    }
  }
  def getCustomerOfferPage(start: Int, limit: Int): ScalaFuture[Iterator[CustomerOffer]] = {
    select.fetchEnumerator() flatMap {
      res => res run Iteratee.slice(start, limit)
    }
  }

  def deleteRecipeById(id: UUID): ScalaFuture[ResultSet] = {
    delete.where(_.id eqs id).future()
  }
}
