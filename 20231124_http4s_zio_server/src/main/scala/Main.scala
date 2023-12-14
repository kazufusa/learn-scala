import zio._
import zio.http._

object Main extends ZIOAppDefault {
  val app: HttpApp[Any] = Routes(
      Method.GET / "graphql" -> handler(Response.text("Hello GET")),
      Method.POST / "graphql" -> handler(Response.text("Hello POST")),
  ).toHttpApp

  override val run = Server.serve(app).provide(Server.default)
}
