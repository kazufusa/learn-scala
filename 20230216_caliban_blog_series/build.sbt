ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "caliban_blog_series"
  )

libraryDependencies ++= Seq(
  "org.scalatest"         %% "scalatest"      % "3.2.15" % "test",
  "com.github.ghostdogpr" %% "caliban"        % "0.4.2",
  "com.github.ghostdogpr" %% "caliban-http4s" % "0.4.2"
)
