package karedo.persistence.mongodb

import java.util.UUID


class MongoBrandDAOSpec
  extends Specification
  with MongoTestUtils
 // with Before
{
  val brandDAO=new BrandMongoDAO()
  brandDAO.dao.collection.remove(MongoDBObject())
  val brandInteractionsDAO=new BrandInteractionsMongoDAO()
  brandInteractionsDAO.dao.collection.remove(MongoDBObject())

  val newBrand=Brand(name="aBrand")

  sequential

  def clean = {
    brandDAO.dao.collection.remove(MongoDBObject())
    brandInteractionsDAO.dao.collection.remove(MongoDBObject())
  }

  "BrandMongoDAO" should {
    "create and retrieve a brand with a generated id " in {
      val n1=newBrand
      val id = brandDAO.insertNew(n1).get
      val findAfterInsert = brandDAO.getById(id).get

      brandDAO.delete(id)
      findAfterInsert.name shouldEqual n1.name
      findAfterInsert.iconId shouldEqual n1.iconId
    }

    "can delete one instance" in {
      val id = brandDAO.insertNew(newBrand).get
      brandDAO.delete(id)
      val findAfterDelete = brandDAO.getById(id)
      findAfterDelete should be(None)
    }

    "can delete all instances" in {
      val id = brandDAO.insertNew(newBrand).get
      val list=brandDAO.list
      list should have size 1

      list.map( brand => brandDAO.delete(id))
      val list2=brandDAO.list
      list2 should have size 0
    }

  }
  "BrandInteractionsMongoDAO" should {
    "add an interaction" in {
      val id = brandDAO.insertNew(newBrand).get
      val user = UUID.randomUUID()
      val interaction = Interaction(userId = user, kind = InteractionType.Click, note = "Annotations")
      brandInteractionsDAO.insertNew(id, interaction)
      interaction shouldNotEqual(None)


    }
    "get it back" in {

      clean
      val id = brandDAO.insertNew(newBrand).get
      val user = UUID.randomUUID()
      val interaction = Interaction(userId = user, kind = InteractionType.Click, note = "Annotations")
      brandInteractionsDAO.insertNew(id, interaction)
      val readback=brandInteractionsDAO.getById(interaction.id).get
      readback shouldEqual(interaction)
    }
    "get list of interactions" in {
      clean
      val id = brandDAO.insertNew(newBrand).get
      val user = UUID.randomUUID()
      val interaction = Interaction(userId = user, kind = InteractionType.Click, note = "Annotations")
      brandInteractionsDAO.insertNew(id, interaction)

      val interaction2 = Interaction(userId = user, kind = InteractionType.Like, note = "likes")
      brandInteractionsDAO.insertNew(id, interaction2)

      val list=brandInteractionsDAO.getInteractions(id)
      list should have size(2)

    }
    "delete interaction" in {
      clean
      val id = brandDAO.insertNew(newBrand).get
      val user = UUID.randomUUID()
      val interaction = Interaction(userId = user, kind = InteractionType.Click, note = "Annotations")
      brandInteractionsDAO.insertNew(id, interaction)
      val list=brandInteractionsDAO.getInteractions(id)
      list should have size(1)

      brandInteractionsDAO.delete(interaction.id)
      val list1=brandInteractionsDAO.getInteractions(id)
      list1 should have size(0)

    }
  }

 /* private def addInteraction: Interaction = {
    val id = brandDAO.insertNew(newBrand).get
    val user = UUID.randomUUID()
    val interaction = Interaction(userId = user, kind = InteractionType.Click, note = "Annotations")
    brandInteractionsDAO.insertNew(id, interaction)
    interaction
  }*/
}
