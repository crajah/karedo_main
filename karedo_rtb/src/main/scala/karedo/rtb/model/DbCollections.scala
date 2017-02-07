package karedo.rtb.model

import karedo.entity.{DbPrefs, DbUserAccount, DbUserPrefs, DbUserProfile}

/**
  * Created by crajah on 15/10/2016.
  */
trait DbCollections {
  val dbUserAccount = new DbUserAccount {}
  val dbUserProfile = new DbUserProfile {}
  val dbUserPrefs = new DbUserPrefs {}
  val dbPrefs = new DbPrefs {}

}
