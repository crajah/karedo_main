package karedo.persistence.mongodb

import java.util.UUID

class UserAccountSpecialFunctionsMongoDAOSpec
  extends Specification
  with MongoTestUtils {
  sequential


  val accountDAO = new UserAccountMongoDAO()

  accountDAO.dao.collection.remove(MongoDBObject())

  val userAccount = UserAccount(UUID.randomUUID(), Some("12345678"), Some("user@email.com"))

  val clientApplication = ClientApplication(UUID.randomUUID(), userAccount.id, "ACT_CODE")

  "UserAccountMongoDAO" should {

    "Add points to account" in {
      val initialPoints = 200
      val increment = 5000

      val accountWithPoints = userAccount.copy(totalPoints = initialPoints)
      accountDAO.insertNew(accountWithPoints, clientApplication)
      val updated = accountDAO.addPoints(userAccount.id, increment)
      updated.get.totalPoints === (initialPoints + increment)


    }

  }

}
