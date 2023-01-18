ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "learn_zio_rest_api"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"
libraryDependencies += "dev.zio" %% "zio" % "2.0.0-RC5"
libraryDependencies += "dev.zio" %% "zio-json" % "0.3.0-RC7"
libraryDependencies += "io.d11" %% "zhttp" % "2.0.0-RC7"
libraryDependencies += "io.d11" %% "zhttp-test" % "2.0.0-RC7" % Test
