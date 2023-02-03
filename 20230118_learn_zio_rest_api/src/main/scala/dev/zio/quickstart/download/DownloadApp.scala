package dev.zio.quickstart.download

import zhttp.http._
import zio.stream.ZStream
import zio._

import java.io.File

object DownloadApp {
  def apply(): Http[Any, Throwable, Request, Response] =
    Http.collectHttp[Request] {
      case Method.GET -> !! / "download" =>
        val fileName = "file.txt"
        Http
          .fromStream(ZStream.fromResource(fileName))
          .setHeaders(
            Headers(
              ("Content-Type", "application/octet-stream"),
              ("Content-Disposition", s"attachmenet: filename=${fileName}")
            )
          )
      case Method.GET -> !! / "download" / "stream" =>
        val fileName = "bigfile.txt"
        Http
          .fromStream(
            ZStream
              .fromResource(fileName)
              .schedule(Schedule.spaced(50.millis))
          )
          .setHeaders(
            Headers(
              ("Content-Type", "application/octet-stream"),
              ("Content-Disposition", s"attachmenet: filename=${fileName}")
            )
          )
    }
}
