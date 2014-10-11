package parallelai.wallet.util

trait SprayJsonSupport {

  import spray.httpx.marshalling.Marshaller
  import spray.httpx.unmarshalling.Unmarshaller
  import spray.json._
  import spray.http._

  implicit def sprayJsonUnmarshallerConverter[T](reader: RootJsonReader[T]) = sprayJsonUnmarshaller(reader)

  implicit def sprayJsonUnmarshaller[T: RootJsonReader] = Unmarshaller[T](MediaTypes.`application/json`) {
      case x: HttpEntity.NonEmpty ⇒
        val json = JsonParser(x.asString(defaultCharset = HttpCharsets.`UTF-8`))
        jsonReader[T].read(json)
    }

  implicit def sprayJsonMarshallerConverter[T](writer: RootJsonWriter[T])(implicit printer: JsonPrinter = PrettyPrinter) =
    sprayJsonMarshaller[T](writer, printer)

  implicit def sprayJsonMarshaller[T](implicit writer: RootJsonWriter[T], printer: JsonPrinter = PrettyPrinter) =
    Marshaller.delegate[T, String](ContentTypes.`application/json`) { value ⇒
      val json = writer.write(value)
      printer(json)
    }
}

object SprayJsonSupport extends SprayJsonSupport