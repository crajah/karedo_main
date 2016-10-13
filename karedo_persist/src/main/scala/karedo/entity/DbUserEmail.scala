package karedo.entity

import karedo.entity.dao.{DbMongoDAO, Keyable}
import org.joda.time.DateTime
import salat.annotations._
import karedo.util.Util.now


case class UserEmail
(
  // it is the full email address
  @Key("_id") id: String
  , account_id: String
  , active: Boolean
  , ts_created: DateTime = now
  , ts_updated: DateTime = now.plusMinutes(20)
) extends Keyable[String]

trait DbUserEmail extends DbMongoDAO[String,UserEmail]





