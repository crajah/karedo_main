package karedo.entity.dao

import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import reactivemongo.api.{Collection, DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, Macros, document}

/**
  * Created by charaj on 23/01/2017.
  */
object MongoConnection3Object extends MongoConnectionConfig {
  val db = getMongo

  private def getMongo = {
    import ExecutionContext.Implicits.global // use any appropriate context

    // Connect to the database: Must be done only once per application
    val driver = MongoDriver()
    val parsedUri = MongoConnection.parseURI(mongoURL)
    val connection = parsedUri.map(driver.connection(_))

    // Database and collections: Get references
    val futureConnection = Future.fromTry(connection)
    def db: Future[DefaultDB] = futureConnection.flatMap(_.database(mongoDbName))

    db
  }

}

trait MongoConnection3 extends MongoConnectionConfig {
  val db:Future[DefaultDB] = MongoConnection3Object.db

  def getCollection(name: String):Future[BSONCollection] = {

    db.map(_.collection(name))
  }
}