package karedo.entity.dao

import karedo.util.{KO, OK, Result}
import org.slf4j.LoggerFactory
import reactivemongo.api.Collection
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.{DefaultWriteResult, LastError}
import reactivemongo.bson.{BSONDocument, _}

import scala.concurrent._
import scala.util._
import scala.concurrent.duration._

/**
  * Created by charaj on 23/01/2017.
  */
abstract class DbMongoDAO3[K, T <: Keyable[K]] (implicit val manifestT: Manifest[T], val manifestK: Manifest[K])
  extends DbDAO[K, T]
    with DbDAOExtensions[K, T]
    with MongoConnection3
{
  val thisClass = manifestT.runtimeClass
  val simpleName = thisClass.getSimpleName
  val logger = LoggerFactory.getLogger(thisClass)
  //logger.debug(s"setting up $thisClass")

  lazy val collection:Future[BSONCollection] = getCollection(s"${DbDAO.tablePrefix}$simpleName")

  override def insertNew(r:T): Result[String,T] = {

    val r:Future[Result[String,T]] = collection.flatMap(_.insert(r).map(_ match {
      case s: DefaultWriteResult => OK(s.asInstanceOf[T])
      case f @ _ => KO(s"ERROR: Insert to ${collection.toString} failed with error message: " + f.writeErrors.map(_.errmsg).reduce(_ + " " + _))
    }))

    futureToResult(r)
  }

  override def find(id:K): Result[String,T] = {
    val query = BSONDocument("_id" -> id.asInstanceOf[Keyable].id.toString)

    val item:Future[Option[T]] = collection.flatMap(_.find(query).one[T])

    val r:Future[Result[String, T]] = item.map(x => x match {
      case Some(t) => OK(t)
      case None => KO(s"ERROR: Find on ${collection.toString} for ID ${id} failed")
    })

    futureToResult(r)
  }

  override def ids: Result[String, List[K]] = {

    val query = BSONDocument("_id" -> "$exists")
    val projection = BSONDocument("_id" -> 1)

    val q:Future[List[K]] = collection.flatMap(_.find(query).cursor[K]().collect())

    val r:Future[Result[String, List[K]]] = q.map(OK(_))

    futureToResult(r)
  }

  override def update(r:T): Result[String,T] = {KO("Not Implemented")}

  override def delete(r:T): Result[String,T] = {KO("Not Implemented")}

  override def deleteAll(): Result[String,Unit] = {KO("Not Implemented")}

  override def lock(id: K, transId: String, transField: String = "lockedBy", tsField: String = "ts", max: Int):  Result[String,T] = {KO("Not Implemented")}

  override def unlock(id: K, transId: String, transField: String = "lockedBy", tsField: String = "ts"): Result[String,T] = {KO("Not Implemented")}

  override def findByAccount(account_id: String): Result[String, List[T]] = {KO("Not Implemented")}

//  private def futureToResult(f: Future[Result[String,T]]): Result[String,T] = {
//    Await.result(f, db_timeoout)
//  }

  private def futureToResult[F](f: Future[F]): F = {
    Await.result(f, db_timeoout)
  }
}
