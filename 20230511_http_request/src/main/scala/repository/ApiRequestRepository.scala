package repository

import io.circe
import io.circe.generic.auto._
import sttp.client3._
import sttp.client3.circe._
import zio._
import scala.concurrent.Future

case class ApiResponse(myName: String)

class ApiRequestRepository {
  protected[repository] lazy val backend = HttpClientFutureBackend()

  def resolveBy(): Task[String] = {
    val request = basicRequest
      .contentType("application/json")
      .post(uri"localhost:8081")
      .response(asJson[ApiResponse])

    call(request).map(_.myName)
  }

  def call[R](
      request: RequestT[Identity, Either[ResponseException[String, circe.Error], R], Any]
  ): ZIO[Any, Throwable, R] = {
    for {
      response <- ZIO.fromFuture(_ => request.send(backend))
      body     <- ZIO.fromEither(response.body)
    } yield body
  }
}
