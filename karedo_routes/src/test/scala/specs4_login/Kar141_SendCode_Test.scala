package specs4_login

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import common.AllTests
import karedo.entity.{UserAccount, UserApp}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
  * Created by pakkio on 10/20/16.
  */
@RunWith(classOf[JUnitRunner])
class Kar141_SendCode_Test extends AllTests {

  val acctId = getNewRandomID
  val appId = getNewRandomID

  dbUserApp.insertNew(UserApp(appId,acctId))
  dbUserAccount.insertNew(UserAccount(acctId,password=Some("pippo")))

  var accountId: String = ""
  "Kar141_SendCode_test" should {


    "* POST /account" in {
      val request = Kar141_SendCode_Req(
        application_id = appId,
        first_name = "John",
        last_name = "Doe",
        msisdn = "00393319345235",
        user_type = "",
        email = "pakkio@gmail.com"

      ).toJson.toString
      Post(s"/account",
        HttpEntity(ContentTypes.`application/json`, request)) ~>
        routesWithLogging ~>
        check {
          val st=status
          val res=responseAs[Kar141_SendCode_Res]
          accountId = res.account_id.getOrElse("Unknown")
          status.intValue() shouldEqual (HTTP_OK_200)
        }
      // now we can check if it was created
    }
  }

}