import zhttp.http._

object GreetingApp {
  def apply(): Http[Any, Nothing, Request, Response] = 
    Http.collect[Request] {
      //GET /greet?name=:name
      case req @ (Method.GET -> !! /"greet") if (req.url.queryParams.get("name").nonEmpty) =>
        Response.text(s"Hello ${req.url.queryParams.get("name").mkString(" and ")}!")

      //GET /greet
      case Method.GET -> !! / "greet" => Response.text("Hello World!")

      //GET /greet/:name
      case Method.GET -> !! / "greet" / name => Response.text(s"Hello $name")
    }
}
