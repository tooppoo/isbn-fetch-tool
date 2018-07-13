name := "isbn-fetch-tool"

version := "0.1"

scalaVersion := "2.12.6"

// https://mvnrepository.com/artifact/net.databinder.dispatch/dispatch-core
libraryDependencies += "net.databinder.dispatch" % "dispatch-core_2.12" % "0.13.4"

val circeVersion = "0.9.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)