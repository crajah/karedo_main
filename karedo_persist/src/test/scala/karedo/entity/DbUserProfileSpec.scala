package karedo.entity

import java.util.UUID

import org.specs2.matcher.TryMatchers
import org.specs2.mutable.Specification
import utils.MongoTestUtils

class DbUserProfileSpec
  extends Specification
      with TryMatchers
      with MongoTestUtils {

    val test = new DbUserProfile {}

    sequential

    "userprofile should insert " in {
      val acctId = UUID.randomUUID()
      val appId = UUID.randomUUID()
      val r = UserProfile(appId, "M", "Claudio", "Pacchiega")
      test.insertNew(appId,r) must beSuccessfulTry
      val updated = r.copy(yob = Some(1962))
      test.update(appId,updated)
      val reread = test.getById(appId)
      reread must beSome
      reread.get.yob  must beEqualTo(Some(1962))

    }
  }