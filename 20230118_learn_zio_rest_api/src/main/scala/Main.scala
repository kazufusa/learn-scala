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
                  Headers(
                    ("Content-Type", "application/octet-stream"),
                    ("Content-Disposition", s"attachmenet: filename=${file.getName}")
                  ),
                  HttpData.fromFile(file)
                )
              )
            else Http.response(Response.status(Status.NotFound))
        } yield r
      case Method.GET -> !! / "download" / "stream" =>
        val file = new File("bigfile.txt")
        Http
          .fromStream(
            ZStream
              .fromFile(file)
              .schedule(Schedule.spaced(50.millis))
          )
          .setHeaders(
            Headers(
              ("Content-Type", "application/octet-stream"),
              ("Content-Disposition", s"attachmenet: filename=${file.getName}")
            )
          )
    }

  val counterApp: Http[Ref[Int], Nothing, Request, Response] =
    Http.fromZIO(ZIO.service[Ref[Int]]).flatMap { ref =>
      Http.collectZIO[Request] {
        case Method.GET -> !! / "up"   => ref.updateAndGet(_ + 1).map(_.toString).map(Response.text)
        case Method.GET -> !! / "down" => ref.updateAndGet(_ - 1).map(_.toString).map(Response.text)
        case Method.GET -> !! / "get"  => ref.get.map(_.toString).map(Response.text)
      }
    }

  val userApp: Http[UserRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case req @ (Method.POST -> !! / "register") =>
        for {
          u <- req.bodyAsString.map(_.fromJson[User])
          r <- u match {
            case Left(e) =>
              ZIO.debug(s"Failed to parse the input $e").as(Response.text(e))
            case Right(u) =>
              UserRepo.register(u).map(id => Response.text(s"Registered ${u.name} with id $id"))
          }
        } yield r

      case Method.GET -> !! / "user" / id =>
        UserRepo
          .lookup(id)
          .map {
            case Some(user) => Response.json(user.toJson)
            case None       => Response.status(Status.NotFound)
          }
    }

  trait UserRepo {
    def register(user: User): UIO[String]
    def lookup(id: String): UIO[Option[User]]
  }

  object UserRepo {
    def register(user: User): ZIO[UserRepo, Nothing, String] =
      ZIO.serviceWithZIO[UserRepo](_.register(user))

    def lookup(id: String): ZIO[UserRepo, Nothing, Option[User]] =
      ZIO.serviceWithZIO[UserRepo](_.lookup(id))
  }

  case class InmemoryUserRepo(users: Ref[mutable.Map[String, User]]) extends UserRepo {
    def register(user: User): UIO[String] =
      for {
        id <- Random.nextUUID.map(_.toString)
        _  <- users.updateAndGet(_ addOne (id, user))
      } yield id

    def lookup(id: String): UIO[Option[User]] =
      users.get.map(_.get(id))
  }

  object InmemoryUserRepo {
    def layer: ZLayer[Any, Nothing, InmemoryUserRepo] =
      ZLayer.fromZIO(
        Ref.make(mutable.Map.empty[String, User]).map(new InmemoryUserRepo(_))
      )
  }

  def run =
    Server
      .start(
        port = 8090,
        http = greetingApp ++ downloadApp ++ counterApp ++ userApp
      )
      .provide(
        ZLayer.fromZIO(Ref.make(0)),
        InmemoryUserRepo.layer
      )
}
