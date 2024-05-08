ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalafmtOnCompile := true

lazy val root = (project in file("."))
  .settings(
    name := "zio_zenbu"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"
libraryDependencies += "dev.zio" %% "zio" % "2.0.22"
