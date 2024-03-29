ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "learn_zio_graphql"
  )

libraryDependencies ++= Seq(
  "org.scalatest"         %% "scalatest"        % "3.2.15" % "test",
  "dev.zio"               %% "zio"              % "2.0.0-RC2",
  "com.github.ghostdogpr" %% "caliban"          % "2.0.0-RC2",
  "com.github.ghostdogpr" %% "caliban-zio-http" % "2.0.0-RC2",
  "io.d11"                %% "zhttp"            % "2.0.0-RC3",
)

// vim: set ft=scala:
