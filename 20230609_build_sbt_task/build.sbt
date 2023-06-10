import sys.process._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalafmtOnCompile := true

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % "test"

lazy val downloadFile = taskKey[Unit]("Download a file using curl")

val hello = taskKey[Unit]("say hello")
val compose = taskKey[Unit]("say hello and download")

lazy val nanika = (project in file("nanika"))
  .settings(
    hello := { println("hello") },
    downloadFile := {
      val exitCode = Seq("sh", "-c", "curl -s -o $(echo $USER) https://example.com/file.txt").!
      exitCode match {
        case 0 => println("success to download")
        case _ => throw new Exception("failed to download")
      }
    },
    compose := Def.sequential(hello, downloadFile).value
  )

lazy val root = (project in file("."))
  .settings(
  ).dependsOn(nanika)
