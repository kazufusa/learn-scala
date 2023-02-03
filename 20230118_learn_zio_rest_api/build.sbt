ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "learn_zio_rest_api"
  )

libraryDependencies ++= Seq(
  "org.scalatest"  %% "scalatest"      % "3.2.15" % "test",
  "dev.zio"        %% "zio"            % "2.0.0-RC6",
  "dev.zio"        %% "zio-json"       % "0.3.0-RC8",
  "io.d11"         %% "zhttp"          % "2.0.0-RC9",
  "io.d11"         %% "zhttp-test"     % "2.0.0-RC8",
  "io.getquill"    %% "quill-zio"      % "3.17.0-RC2",
  "io.getquill"    %% "quill-jdbc-zio" % "3.17.0-RC2",
  "com.h2database" %  "h2"             % "2.1.212"
)
