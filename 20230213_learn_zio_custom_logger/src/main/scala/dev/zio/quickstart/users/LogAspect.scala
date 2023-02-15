package dev.zio.quickstart.users

import zio.{ZIO, UIO, Trace, ZIOAspect, Random}
import zhttp.http.Request

object LogAspect {

  def logSpan(label: String): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
    new ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] {
      override def apply[R, F, A](zio: ZIO[R, F, A])(implicit trace: Trace): ZIO[R, F, A] =
        ZIO.logSpan(label)(zio)
    }

  def logAnnotateCorrelationId(req: Request) =
    new ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] {
      override def apply[R, F, A](zio: ZIO[R, F, A])(implicit trace: Trace): ZIO[R, F, A] =
        correlationId(req).flatMap(id => ZIO.logAnnotate("correlation-id", id)(zio))
    }

  def correlationId(req: Request): UIO[String] =
    ZIO
      .succeed(req.header("X-Correlation-ID").map(_._2.toString))
      .flatMap(x => Random.nextUUID.map(uuid => x.getOrElse(uuid.toString)))
}
