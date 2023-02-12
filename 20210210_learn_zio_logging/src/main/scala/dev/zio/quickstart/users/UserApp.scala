package dev.zio.quickstart.users

import zhttp.http._
import zio._
import zio.json._
import LogAspect._

/**
  * An http app that:
  *   - Accepts a `Request` and returns a `Response`
  *   - May fail with type `Throwable`
  *   - Uses a `UserRepo` as the environment
  */
object UserApp {
  def apply(): Http[UserRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case req @ (Method.POST -> !! / "users") => {
          for {
            body <- req.bodyAsString
            _    <- ZIO.logInfo(s"POST /user -d $body")
            u = body.fromJson[User]
            r <- u match {
              case Left(e) =>
                ZIO
                  .logError(s"Failed to parse the input: $e")
                  .as(
                    Response.text(e).setStatus(Status.BadRequest)
                  )
              case Right(u) =>
                UserRepo
                  .register(u)
                  .foldCauseZIO(
                    cause =>
                      ZIO
                        .logError(s"Failed to register user: $cause")
                        .as(
                          Response.status(Status.InternalServerError)
                        ),
                    id => ZIO.logInfo(s"User registered: $id").as(Response.text(id))
                  )
            }
          } yield r
        } @@ logSpan("register-user") @@ logAnnotateCorrelationId(req)

      case req @ Method.GET -> !! / "users" / id => {
          for {
            _ <- ZIO.logInfo(s"Request: GET /user/A$id")
            r <-
              UserRepo
                .lookup(id)
                .some
                .foldZIO(
                  _ =>
                    ZIO
                      .log(s"Requested user with $id not found")
                      .as(Response.status(Status.NotFound)),
                  user =>
                    ZIO
                      .log(s"Retrieved the user")
                      .as(Response.json(user.toJson))
                )
          } yield r
        } @@ logSpan("get-user") @@ logAnnotateCorrelationId(req)

      case req @ (Method.GET -> !! / "users") => {
          for {
            _     <- ZIO.logInfo(s"Request: GET /users")
            users <- UserRepo.users
            _     <- ZIO.log(s"Returning ${users.size} users")
          } yield Response.json(users.toJson)
        } @@ logSpan("get-users") @@ logAnnotateCorrelationId(req)
    }
}
