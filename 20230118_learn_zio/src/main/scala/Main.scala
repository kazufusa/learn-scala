import zio._

import java.io.IOException

object MainApp extends ZIOAppDefault {
  // https://zio.dev/guides/quickstarts/hello-world
  // val myApp: ZIO[Any, IOException, Unit] =
  //   for {
  //     rnd <- Random.nextIntBounded(100)
  //     _   <- Console.printLine(s"Random number: $rnd")
  //     _   <- Clock.sleep(1.second)
  //   } yield ()
  //
  // def run = myApp.forever

  // def run = for {
  //   _ <- Console.print("Please enter your name: ")
  //   name <- Console.readLine
  //   < <- Console.print(s"Hello, $name!")
  // } yield ()

  def run = Console.print("Please enter your name: ")
    .flatMap { _ => 
      Console.readLine
        .flatMap { name =>
          Console.print(s"Hello $name!")
        }
    }

}
