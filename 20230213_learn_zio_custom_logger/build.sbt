ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "learn_zio_rest_api"
  )

libraryDependencies ++= Seq(
  "org.scalatest"       %% "scalatest"                % "3.2.15" % "test",
  "dev.zio"             %% "zio"                      % "2.0.0-RC6",
  "dev.zio"             %% "zio-json"                 % "0.3.0-RC8",
  "io.d11"              %% "zhttp"                    % "2.0.0-RC9",
  "io.getquill"         %% "quill-zio"                % "3.17.0-RC3",
  "io.getquill"         %% "quill-jdbc-zio"           % "3.17.0-RC3",
  "com.h2database"       % "h2"                       % "2.1.212",
  "dev.zio"             %% "zio-logging"              % "2.0.0-RC10",
  "dev.zio"             %% "zio-logging-slf4j"        % "2.0.0-RC10",
  "ch.qos.logback"       % "logback-classic"          % "1.2.11",
  "net.logstash.logback" % "logstash-logback-encoder" % "7.0.1"
  // "org.slf4j"      % "slf4j-simple"      % "1.7.36",
  // "org.slf4j"      % "slf4j-reload4j"    % "1.7.36",
)

// vim:ft=scala:
