import sbt._
import Keys._

object Common {

  val settings: Seq[Setting[_]] = Seq (
    version := "1.0",
    scalaVersion := "2.10.2",
    organization := "com.parallelai"
  )

  val akkaVersion = "2.2.3"

  val sl4jVersion = "1.7.5"

  val sprayVersion = "1.2.1"
  val sprayJsonVersion = "1.2.6"

  val sprayResolvers = Seq (
    "spray repo" at "http://repo.spray.io",
    "spray nightlies" at "http://nightlies.spray.io"
  )

  val conjarResolver = "conjars.org" at "http://conjars.org/repo"

  val specs2 = "org.specs2" %% "specs2" % "1.14"
  val sprayJson = "io.spray" %% "spray-json" % sprayJsonVersion
  val subcutExt = "com.pragmasoft" % "subcut_ext" % "2.0"
  val scalaAsync = "org.scala-lang.modules" %% "scala-async" % "0.9.0"
  val sprayClient = "io.spray" % "spray-client" % sprayVersion
  val spraySwagger = "com.gettyimages" % "spray-swagger_2.10" % "0.2.4" //exclude ("com.typesafe.sbt", "sbt-pgp") exclude("com.typesafe.akka", "akka-actor")
}