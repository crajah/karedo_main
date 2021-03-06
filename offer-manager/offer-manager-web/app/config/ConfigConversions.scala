package config

import com.escalatesoft.subcut.inject.config.ConfigProperty
import scala.concurrent.duration._

/**
 * Created by crajah on 29/05/2014.
 */
object ConfigConversions {

  implicit def toDuration(prop: ConfigProperty): Duration = {
    if(prop.value == "infinite") {
      Duration.Inf
    } else {
      val amount: Long = prop.value.toLong
      val timeQualifier: String = prop.name.split('.').last

      timeQualifier match {
        case "seconds" | "secs" | "second" | "sec" => amount.seconds
        case "minutes" | "minute" | "min" | "mins" => amount.minutes
        case "hours"  | "hour" => amount.hours
        case _ => amount.milliseconds
      }
    }
  }

  implicit def toBoolean(prop: ConfigProperty): Boolean = prop.value.equalsIgnoreCase("true")
}
