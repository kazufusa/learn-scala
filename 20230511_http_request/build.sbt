ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalafmtOnCompile := true

lazy val root = (project in file("."))
  .settings(
    name := "http_request"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"

libraryDependencies ++= List(
  "com.softwaremill.sttp.client3" %% "core"                 % "3.8.15",
  "com.softwaremill.sttp.client3" %% "circe"                % "3.8.15",
  "io.circe"                      %% "circe-core"           % "0.14.5",
  "io.circe"                      %% "circe-generic"        % "0.14.5",
  "io.circe"                      %% "circe-generic-extras" % "0.14.3",
  "io.circe"                      %% "circe-parser"         % "0.14.5",
  "dev.zio"                       %% "zio"                  % "2.0.13",
  "dev.zio"                       %% "zio-test"             % "2.0.13"
)
