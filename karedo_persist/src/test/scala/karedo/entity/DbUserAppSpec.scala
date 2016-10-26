package karedo.entity

import java.util.UUID

import karedo.util.{KO, OK}
import org.specs2.matcher.EitherMatchers
import org.specs2.mutable.Specification
import utils.MongoTestUtils
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DbUserAppSpec
  extends Specification
      with EitherMatchers
      with MongoTestUtils {

    val test = new DbUserApp {}
    test.deleteAll()

    sequential

    "userapp should insert " in {
      val acctId = UUID.randomUUID()
      val appId = "app1"
      val r = UserApp(appId, acctId, mobile_linked = false, email_linked = true)
      test.insertNew(r) must beOK
      val updated = r.copy(mobile_linked = true)
      test.update(updated)
      test.find(appId) match {
        case OK(x) => x.mobile_linked must beTrue
        case KO(error) => ko("update didn't work")
      }

    }
  }