import zio._

trait Repo1 {
  def findByIdOpt(id: Option[Int]): Task[Option[Int]]
  def findOptById(id: Int): Task[Option[Int]]
  def findById(id: Int): Task[Int]
  def findAsStrById(id: Int): Task[String]
}

trait Repo2 {
  def findByIdOpt(id: Option[Int]): Task[Option[Int]]
  def findOptById(id: Int): Task[Option[Int]]
  def findById(id: Int): Task[Int]
  def findAsStrById(id: Int): Task[String]
}

class App {
  def find(id: Int, repo1: Repo1, repo2: Repo2): Task[Int] =
    repo1.findById(id).flatMap(v => repo2.findById(v))

  def find2(id: Int, repo1: Repo1, repo2: Repo2): Task[Int] =
    for {
      id1 <- repo1.findById(id)
      id2 <- repo2.findById(id1)
    } yield id2

  def findOpt(mayBeId: Int, repo1: Repo1, repo2: Repo2): Task[Option[Int]] =
    repo1.findByIdOpt(mayBeId).flatMap(v => repo2.findByIdOpt(v))

  def findOpt(mayBeId: Option[Int], repo1: Repo1, repo2: Repo2): Task[Option[Int]] =
    repo1.findByIdOpt(mayBeId).flatMap(v => repo2.findByIdOpt(v))

  def findOpt2(mayBeId: Option[Int], repo1: Repo1, repo2: Repo2): Task[Option[Int]] =
    for {
      id1 <- repo1.findByIdOpt(mayBeId)
      id2 <- repo2.findByIdOpt(id1)
    } yield id2

  def findOptAndFind(mayBeId: Option[Int], repo1: Repo1, repo2: Repo2): Task[Option[Int]] =
    repo1.findByIdOpt(mayBeId).flatMap {
      case Some(v) => repo2.findById(v).map(Some(_))
      case _       => ZIO.none
    }

  def findOptAndFind2(mayBeId: Option[Int], repo1: Repo1, repo2: Repo2): Task[Option[Int]] =
    for {
      id1 <- repo1.findByIdOpt(mayBeId)
      id2 <- id1 match {
        case Some(v) => repo2.findById(v).map(Some(_))
        case _       => ZIO.none
      }
    } yield id2

  def findIds(ids: Set[Int], repo1: Repo1): Task[Set[Int]] =
    ZIO.foreachPar(ids)(repo1.findById)

  def findIds2(ids: Set[Int], repo1: Repo1, repo2: Repo2): Task[Set[Int]] =
    ZIO.foreachPar(ids)(id => repo1.findById(id).flatMap(repo2.findById))

  def findIds3(ids: Set[Int], repo1: Repo1, repo2: Repo2): Task[Set[Option[Int]]] =
    ZIO.foreachPar(ids)(id => repo1.findOptById(id))

  def findIds4(ids: Set[Int], repo1: Repo1, repo2: Repo2): Task[Set[Int]] =
    ZIO.foreachPar(ids)(id => repo1.findOptById(id)).map(_.flatten)

  def zip(id: Int, repo1: Repo1, repo2: Repo2): Task[(Int, Int)] = for {
    both <- repo1.findById(id).zip(repo2.findById(id))
  } yield both

  def zipPar(id: Int, repo1: Repo1, repo2: Repo2): Task[(Int, Int)] = for {
    both <- repo1.findById(id).zipPar(repo2.findById(id))
  } yield both

  def race(id: Int, repo1: Repo1, repo2: Repo2): Task[Int] = for {
    id1or2 <- repo1.findById(id).race(repo2.findById(id))
  } yield id1or2

  def raceEither(id: Int, repo1: Repo1, repo2: Repo2): Task[Either[Int, String]] = for {
    id1or2 <- repo1.findById(id).raceEither(repo2.findAsStrById(id))
  } yield id1or2

  def timeoutAndCancel(id: Int, repo1: Repo1): Task[Int] =
    repo1.findById(id).raceEither(ZIO.unit.delay(1.second)).flatMap {
      case Left(v) => ZIO.succeed(v)
      case _       => ZIO.fail(new Exception("timeout"))
    }
}

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

  def run = Console
    .print("Please enter your name: ")
    .flatMap { _ =>
      Console.readLine
        .flatMap { name =>
          Console.print(s"Hello $name!")
        }
    }

}
