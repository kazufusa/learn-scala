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
  implicit val decoder : JsonDecoder[User] = DeriveJsonDecoder.gen[User]
}

object Main extends ZIOAppDefault {
  val greetingApp: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {

      // GET /greet?name=:name
      case req@(Method.GET -> !! / "greet") if (req.url.queryParams.get("name").nonEmpty) =>
        // Response.text(s"Hello ${req.url.queryParams.get("name").flatMap(_.headOption).get}!")
        Response.text(s"Hello ${req.url.queryParams("name").mkString(" and ")}!")

      // GET /greet
      case Method.GET -> !! / "greet" => Response.text(s"Hello World!")

      // GET /greet/:name
      case Method.GET -> !! / "greet" / name => Response.text(s"Hello $name!")
    }


  def run = Server.start(
      port = 8090,
      http = greetingApp
    ).provide(
      // // An layer that contains a `Ref[Int]` for the `counterApp`
      // ZLayer.fromZIO(Ref.make(0)),
      // // An layer that contains a `Ref[mutable.Map[String, User]]` for the `userApp`
      // ZLayer.fromZIO(Ref.make(mutable.Map.empty[String, User]))
    )
}
