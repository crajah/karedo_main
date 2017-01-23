package karedo.entity.dao

import karedo.util._
import org.slf4j.LoggerFactory
import org.mongodb.scala._
import spray.json._
import DefaultJsonProtocol._
import karedo.entity._

/**
  * Created by charaj on 12/01/2017.
  */
object DbMongoDAO2 {
  val tablePrefix = "K2_"
}

abstract class DbMongoDAO2[K, T <: Keyable[K]] (implicit val manifestT: Manifest[T], val manifestK: Manifest[K])
  extends DbDAO[K, T]
    with DbDAOExtensions[K, T]
    with MongoConnection2
    with JsonDbImplicits
{

  val thisClass = manifestT.runtimeClass
  val simpleName = thisClass.getSimpleName
  val logger = LoggerFactory.getLogger(thisClass)
  //logger.debug(s"setting up $thisClass")

  lazy val collection = db.getCollection(s"${DbMongoDAO2.tablePrefix}$simpleName")

  override def insertNew(r:T) : Result[String,T] = {

//    r match {
//      case j: RootJsonFormat[_] => {
//        val json = j.toJson.compactPrint
//        collection.insertOne(Document(json))
//      }
//      case _ => {
//        KO("Only RootJsonFormat is supported. " + manifestT.toString)
//      }
//    }

    KO("Not Implemented")
  }

  override def find(id:K): Result[String,T] = {KO("Not Implemented")}

  override def ids: Result[String, List[K]] = {KO("Not Implemented")}

  override def update(r:T): Result[String,T] = {KO("Not Implemented")}

  override def delete(r:T): Result[String,T] = {KO("Not Implemented")}

  override def deleteAll(): Result[String,Unit] = {KO("Not Implemented")}

  override def lock(id: K, transId: String, transField: String = "lockedBy", tsField: String = "ts", max: Int):  Result[String,T] = {KO("Not Implemented")}

  override def unlock(id: K, transId: String, transField: String = "lockedBy", tsField: String = "ts"): Result[String,T] = {KO("Not Implemented")}

  override def findByAccount(account_id: String): Result[String, List[T]] = {KO("Not Implemented")}
}
