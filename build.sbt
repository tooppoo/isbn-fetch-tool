name := "isbn-fetch-tool"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.dispatchhttp" %% "dispatch-core" % "0.14.0"
)

val circeVersion = "0.9.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)