package repository

import io.circe
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration
import sttp.model.MediaType
import sttp.client3._
import sttp.client3.circe._
import zio._
import scala.concurrent.Future

case class ApiResponse(myName: String)

trait ApiRepository {
  implicit val jsonConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
}

class ApiRequestRepository extends ApiRepository {
  protected[repository] lazy val backend = HttpClientFutureBackend()

  def resolveBy(): Task[String] = {
    val request = basicRequest
      .contentType(MediaType.ApplicationJson)
      .post(uri"localhost:8081")
      .response(asJson[ApiResponse])

    call(request).map(_.myName)
  }

  def call[R](
      request: RequestT[Identity, Either[ResponseException[String, circe.Error], R], Any]
  ): ZIO[Any, Throwable, R] =
    ZIO.logAnnotate("request_repository", "true") {
      for {
        _        <- ZIO.log("start call")
        _        <- ZIO.unit.delay(2.second)
        response <- ZIO.fromFuture(_ => request.send(backend))
        body     <- ZIO.fromEither(response.body)
        _        <- ZIO.log("end call")
      } yield body
    }
}
