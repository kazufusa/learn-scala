import zhttp.http._
import zhttp.service.Server
import zio._
import zio.json._
import zio.stream.ZStream
import dev.zio.quickstart.counter.CounterApp
import dev.zio.quickstart.download.DownloadApp

import java.io.File
import scala.collection.mutable
import dev.zio.quickstart.users.UserApp
import dev.zio.quickstart.users.InmemoryUserRepo
import zio.logging.backend.SLF4J
import zio.logging.LogFormat

case class User(name: String, age: Int)

object User {
  implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
}

object Main extends ZIOAppDefault {
  override val bootstrap = SLF4J.slf4j(LogLevel.All, LogFormat.default)

  def run =
    Server
      .start(
        port = 8090,
        http = GreetingApp() ++ DownloadApp() ++ CounterApp() ++ UserApp()
      )
      .provide(
        ZLayer.fromZIO(Ref.make(0)),
        // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
        InmemoryUserRepo.layer
      )
}
