package repository

import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.extras.semiauto._
import io.circe
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration
import sttp.model.{MediaType, StatusCode}
import sttp.client3._
import sttp.client3.circe._
import zio._
import scala.concurrent.Future

case class ExampleGet200Response(
    myName: String,
    status: Option[ExampleGet200ResponseEnums.Status] = None,
    message: Option[String] = None
)

object ExampleGet200Response {
  implicit val jsonConfig: Configuration               = Configuration.default.withSnakeCaseMemberNames
  implicit val decoder: Decoder[ExampleGet200Response] = deriveConfiguredDecoder[ExampleGet200Response]
}

object ExampleGet200ResponseEnums {

  type Status = Status.Value
  object Status extends Enumeration {
    val Success = Value("success")
    val Failure = Value("failure")
  }

  implicit val statusDecoder: Decoder[Status] = Decoder.decodeEnumeration(Status)
}

case class ApiResponse(myName: String)
case class ApiErrorResponse(errorCode: String)

trait ApiRepository {
  implicit val jsonConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
}

class ApiRequestRepository extends ApiRepository {
  protected[repository] lazy val backend = HttpClientFutureBackend()

  def resolveBy(): Task[String] = {
    val request = basicRequest
      .contentType(MediaType.ApplicationJson)
      .post(uri"localhost:8081")
      .response(asJson[ExampleGet200Response])

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
