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
import dev.zio.quickstart.users.PersistentUserRepo
import dev.zio.quickstart.users.InmemoryUserRepo

case class User(name: String, age: Int)

object User {
  implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
}

object Main extends ZIOAppDefault {
  def run =
    Server
      .start(
        port = 8090,
        http = GreetingApp() ++ DownloadApp() ++ CounterApp() ++ UserApp()
      )
      .provide(
        ZLayer.fromZIO(Ref.make(0)),
        PersistentUserRepo.layer
      )
}
