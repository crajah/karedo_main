package restapi

import core.{SuccessResponse, FailureResponse, ResponseWithFailure}
import spray.json._
import java.util.UUID
import scala.reflect.ClassTag
import spray.httpx.marshalling.{MarshallingContext, MetaMarshallers, Marshaller, CollectingMarshallingContext}
import spray.http.StatusCode
import spray.httpx.SprayJsonSupport

//import parallelai.wallet.util.SprayJsonSupport

/**
 * Contains useful JSON formats: ``j.u.Date``, ``j.u.UUID`` and others; it is useful
 * when creating traits that contain the ``JsonReader`` and ``JsonWriter`` instances
 * for types that contain ``Date``s, ``UUID``s and such like.
 */
trait DefaultJsonFormats extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers  {

  /**
   * Computes ``RootJsonFormat`` for type ``A`` if ``A`` is object
   */
  def jsonObjectFormat[A : ClassTag]: RootJsonFormat[A] = new RootJsonFormat[A] {
    val ct = implicitly[ClassTag[A]]
    def write(obj: A): JsValue = JsObject("value" -> JsString(ct.runtimeClass.getSimpleName))
    def read(json: JsValue): A = ct.runtimeClass.newInstance().asInstanceOf[A]
  }


  /**
   * Type alias for function that converts ``A`` to some ``StatusCode``
   * @tparam A the type of the input values
   */
  type ErrorSelector[A] = A => StatusCode

  /**
   * Marshals instances of ``Either[A, B]`` into appropriate HTTP responses by marshalling the values
   * in the left or right projections; and by selecting the appropriate HTTP status code for the
   * values in the left projection.
   *
   * @param ma marshaller for the left projection
   * @param mb marshaller for the right projection
   * @param esa the selector converting the left projection to HTTP status code
   * @tparam A the left projection
   * @tparam B the right projection
   * @return marshaller
   */
  implicit def errorSelectingEitherMarshaller[A, B](implicit ma: Marshaller[A], mb: Marshaller[B], esa: ErrorSelector[A]): Marshaller[ResponseWithFailure[A, B]] =
    Marshaller[ResponseWithFailure[A, B]] { (value, ctx) =>
      value match {
        case FailureResponse(a) =>
          val mc = new CollectingMarshallingContext()
          ma(a, mc)
          ctx.handleError(ErrorResponseException(esa(a), mc.entity))
        case SuccessResponse(b) =>
          mb(b, ctx)
      }
    }

}
