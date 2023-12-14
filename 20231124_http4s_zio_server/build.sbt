ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalafmtOnCompile := true

lazy val root = (project in file("."))
  .settings(
    name := "http4s_zio_server"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % "test"

libraryDependencies += "dev.zio" %% "zio-http" % "3.0.0-RC3"
