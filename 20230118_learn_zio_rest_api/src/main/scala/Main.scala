import zhttp.http._
import zhttp.service.Server
import zio._
import zio.json._
import zio.stream.ZStream
import dev.zio.quickstart.counter.CounterApp
import dev.zio.quickstart.download.DownloadApp

import java.io.File
import scala.collection.mutable

case class User(name: String, age: Int)

object User {
  implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
}

object Main extends ZIOAppDefault {
  val inMemoryUserApp: Http[UserRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case req @ (Method.POST -> !! / "users") =>
        for {
          u <- req.bodyAsString.map(_.fromJson[User])
          r <- u match {
            case Left(e) =>
              ZIO
                .debug(s"Failed to parse the input $e")
                .as(
                  Response.text(e).setStatus(Status.BadRequest)
                )
            case Right(u) =>
              UserRepo.register(u).map(id => Response.text(id))
          }
        } yield r

      case Method.GET -> !! / "users" / id =>
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
        http = GreetingApp() ++ DownloadApp() ++ CounterApp() ++ inMemoryUserApp
      )
      .provide(
        ZLayer.fromZIO(Ref.make(0)),
        InmemoryUserRepo.layer
      )
}
