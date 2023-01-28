import io.netty.handler.codec.http.DefaultHttpHeaders
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.json._
import zio.stream.ZStream

import java.io.File
import scala.collection.mutable

case class User(name: String, age: Int)

object User {
  implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
}

object Main extends ZIOAppDefault {
  val greetingApp: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {

      // GET /greet?name=:name
      case req @ (Method.GET -> !! / "greet") if (req.url.queryParams.get("name").nonEmpty) =>
        // Response.text(s"Hello ${req.url.queryParams.get("name").flatMap(_.headOption).get}!")
        Response.text(s"Hello ${req.url.queryParams("name").mkString(" and ")}!")

      // GET /greet
      case Method.GET -> !! / "greet" => Response.text(s"Hello World!")

      // GET /greet/:name
      case Method.GET -> !! / "greet" / name => Response.text(s"Hello $name!")
    }

  val downloadApp: Http[Any, Throwable, Request, Response] =
    Http.collectHttp[Request] {
      case Method.GET -> !! / "download" =>
        for {
          file <- Http.fromZIO(ZIO.attemptBlocking(new File("file.txt")))
          r <-
            if (file.exists())
              Http.response(
                Response(
                  Status.Ok,
                  Headers.make(
                    new DefaultHttpHeaders().add("Content-type", "text/plain")
                  ),
                  HttpData.fromFile(file)
                )
              )
            else Http.response(Response.status(Status.NotFound))
        } yield r
      case Method.GET -> !! / "download" / "stream" =>
        Http.fromStream(ZStream.fromFile(new File("file.txt")))
    }

  val counterApp: Http[Ref[Int], Nothing, Request, Response] =
    Http.fromZIO(ZIO.service[Ref[Int]]).flatMap { ref =>
      Http.collectZIO[Request] {
        case Method.GET -> !! / "up"   => ref.updateAndGet(_ + 1).map(_.toString).map(Response.text)
        case Method.GET -> !! / "down" => ref.updateAndGet(_ - 1).map(_.toString).map(Response.text)
        case Method.GET -> !! / "get"  => ref.get.map(_.toString).map(Response.text)
      }
    }

  def run =
    Server
      .start(
        port = 8090,
        http = greetingApp ++ downloadApp ++ counterApp
      )
      .provide(
        ZLayer.fromZIO(Ref.make(0))
        // // An layer that contains a `Ref[mutable.Map[String, User]]` for the `userApp`
        // ZLayer.fromZIO(Ref.make(mutable.Map.empty[String, User]))
      )
}
