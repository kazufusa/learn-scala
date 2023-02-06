import zhttp.http._
import zhttp.service.Server
import zio._
import zio.json._
import zio.stream.ZStream
import dev.zio.quickstart.counter.CounterApp
import dev.zio.quickstart.download.DownloadApp

import java.io.IOException
import java.io.File
import scala.collection.mutable
import dev.zio.quickstart.users.{UserApp, InmemoryUserRepo}
import dev.zio.quickstart.config.HttpServerConfig

case class User(name: String, age: Int)

object User {
  implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
}

object Main extends ZIOAppDefault {
  def run =
    ZIO
      .service[HttpServerConfig]
      .flatMap { config =>
        for {
          _ <- Console.printLine(
            "Application started with following configuration:\n" +
              s"\thost: ${config.host}\n" +
              s"\tport: ${config.port}"
          )
          server <- Server.start(
            port = config.port,
            http = GreetingApp() ++ DownloadApp() ++ CounterApp() ++ UserApp()
          )
        } yield server
      }
      .provide(
        ZLayer.fromZIO(Ref.make(0)),
        InmemoryUserRepo.layer,
        HttpServerConfig.layer
      )
}
