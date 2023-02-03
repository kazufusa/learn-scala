package dev.zio.quickstart.users

import zhttp.http._
import zio._
import zio.json._

object UserApp {
  def apply(): Http[UserRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case req @ (Method.POST -> !! / "users") =>
        for {
          u <- req.bodyAsString.map(_.fromJson[User])
          r <- u match {
            case Left(e) =>
              ZIO
                .debug(s"Failed to parse the input: $e")
                .as(
                  Response.text(e).setStatus(Status.BadRequest)
                )
            case Right(u) => UserRepo.register(u).map(id => Response.text(id))
          }
        } yield r

      case Method.GET -> !! / "users" / id =>
        UserRepo.lookup(id).map {
          case Some(user) => Response.text(user.toJson)
          case None       => Response.status(Status.NotFound)
        }

      case Method.GET -> !! / "users" => UserRepo.users().map(response => Response.text(response.toJson))
    }
}
