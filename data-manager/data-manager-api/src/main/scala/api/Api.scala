package api

import core.{CoreActors, Core}
import akka.actor.Props
import spray.routing.RouteConcatenation
import com.gettyimages.spray.swagger.SwaggerHttpService
import scala.reflect.runtime.universe._
import com.parallelai.wallet.datamanager.data._

/**
 * The REST API layer. It exposes the REST services, but does not provide any
 * web server interface.<br/>
 * Notice that it requires to be mixed in with ``core.CoreActors``, which provides access
 * to the top-level actors that make up the system.
 */
trait Api extends RouteConcatenation {
  this: CoreActors with Core =>

  private implicit val _ = system.dispatcher

  val routes =
    new RegistrationService(registration).route ~
    new MessengerService(messenger).route ~
    new SwaggerService(system).route ~
      new SwaggerHttpService {
        def actorRefFactory = system
        def apiTypes = Seq( typeOf[RegistrationService] )
        def modelTypes =
          Seq(
            typeOf[RegistrationRequest],
            typeOf[RegistrationResponse],
            typeOf[RegistrationValidation]//,
//            typeOf[RegistrationValidationResponse]
          )
        def apiVersion = "1.0"
        def swaggerVersion = "1.2"
        def baseUrl = "http://localhost:8080"
        def specPath = "api"
        def resourcePath = "api-docs"
      }.routes

  val rootService = system.actorOf(Props(new RoutedHttpService(routes)))

}
