package util

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import org.specs2.mock.Mockito
import org.specs2.mutable.{After, SpecificationLike}
import org.specs2.specification.{Step, Fragments}
import org.specs2.time.NoTimeConversions
import parallelai.wallet.persistence.{BrandDAO, ClientApplicationDAO, UserAccountDAO}
import spray.testkit.TestUtils

import scala.concurrent.Await._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random


trait ApiHttpClientSpec extends SpecificationLike with NoTimeConversions with Mockito {
  def responseTimeout = 5.seconds

  sequential

  trait WithMockedPersistenceRestService extends After {
    val servicePort : Int = Math.abs( Random.nextInt(3000) ) + 10000
    val serviceUrl = s"http://localhost:$servicePort"

    implicit val system = ActorSystem(s"${getClass.getSimpleName}ClientSystem".replace('$', 'S'))

    def wait[T](future: Future[T]): T = result(future, responseTimeout)

    lazy val mockedBrandDAO = mock[BrandDAO]
    lazy val mockedClientApplicationDAO = mock[ClientApplicationDAO]
    lazy val mockedUserAccountDAO = mock[UserAccountDAO]

    lazy val messagerActor = TestProbe()
    val server = new RestServiceWithMockPersistence(servicePort, mockedBrandDAO, mockedClientApplicationDAO, mockedUserAccountDAO, messagerActor.ref)

    def after = stopServer()

    def stopServer(): Unit = {
      println("Shutting down actor context")
      system.shutdown()
    }
  }
}