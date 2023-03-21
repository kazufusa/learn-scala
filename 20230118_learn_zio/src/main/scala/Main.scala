import zio._

trait Repo1 {
  def findById(id: Int): Task[Int]
  def findByIdOpt(id: Option[Int]): Task[Option[Int]]
  def findByIds(ids: Set[Int]): Task[Set[Int]]
  def findOptById(id: Int): Task[Option[Int]]
  def findAsStrById(id: Int): Task[String]
}

class App {
  def conditionalChain(id: Int, repo1: Repo1, repo2: Repo1, condRepo: Repo1): Task[(Int, Option[Int])] =
    for {
      repo1IdAndCond <- repo1.findById(id).zipPar(condRepo.findById(id))
      repo2Id <- ZIO.when(repo1IdAndCond._2 > 10)(repo2.findById(repo1IdAndCond._1))
      result = (repo1IdAndCond._1, repo2Id)
    } yield result

  def findByIdAndMakeOpton(id: Int, repo1: Repo1, repo2: Repo1): Task[Option[Int]] =
    repo1.findById(id).map(Some(_)).flatMap(ZIO.foreach(_)(id => repo2.findById(id)))

  def findByIdAndMakeOptonFor(id: Int, repo1: Repo1, repo2: Repo1): Task[Option[Int]] =
    for {
      mayBeId <- repo1.findById(id).asSome
      // mayBeId <- repo1.findById(id).map(Some(_))
      id <- ZIO.foreach(mayBeId)(id => repo2.findById(id))
    } yield id

  def findByIdOp(id: Int, repo1: Repo1, repo2: Repo1): Task[Int] =
    repo1.findById(id) &> repo2.findById(id)

  def findById(id: Int, repo1: Repo1, repo2: Repo1): Task[Int] =
    repo1.findById(id).flatMap(v => repo2.findById(v))

  def findByIdFor(id: Int, repo1: Repo1, repo2: Repo1): Task[Int] =
    for {
      id1 <- repo1.findById(id)
      id2 <- repo2.findById(id1)
    } yield id2

  def findByIdOpt(mayBeId: Option[Int], repo1: Repo1, repo2: Repo1): Task[Option[Int]] =
    repo1.findByIdOpt(mayBeId).flatMap(v => repo2.findByIdOpt(v))

  def findByIdOptFor(mayBeId: Option[Int], repo1: Repo1, repo2: Repo1): Task[Option[Int]] =
    for {
      id1 <- repo1.findByIdOpt(mayBeId)
      id2 <- repo2.findByIdOpt(id1)
    } yield id2

  def findOptAndFind(mayBeId: Option[Int], repo1: Repo1, repo2: Repo1): Task[Option[Int]] =
    repo1.findByIdOpt(mayBeId).flatMap {
      case Some(v) => repo2.findById(v).map(Some(_))
      case _       => ZIO.none
    }

  def findOptAndFindFor(mayBeId: Option[Int], repo1: Repo1, repo2: Repo1): Task[Option[Int]] =
    for {
      id1 <- repo1.findByIdOpt(mayBeId)
      id2 <- id1 match {
        case Some(v) => repo2.findById(v).map(Some(_))
        case _       => ZIO.none
      }
    } yield id2

  def findIds(ids: Set[Int], repo1: Repo1): Task[Set[Int]] =
    ZIO.foreachPar(ids)(repo1.findById)

  def findIds2(ids: Set[Int], repo1: Repo1, repo2: Repo1): Task[Set[Int]] =
    ZIO.foreachPar(ids)(id => repo1.findById(id).flatMap(repo2.findById))

  def findIds3(ids: Set[Int], repo1: Repo1, repo2: Repo1): Task[Set[Option[Int]]] =
    ZIO.foreachPar(ids)(id => repo1.findOptById(id))

  def findIds4(ids: Set[Int], repo1: Repo1, repo2: Repo1): Task[Set[Int]] =
    ZIO.foreachPar(ids)(id => repo1.findOptById(id)).map(_.flatten)

  def findIds5(ids: Set[Int], repo1: Repo1, repo2: Repo1): Task[Set[Int]] =
    repo1.findByIds(ids).flatMap(repo2.findByIds(_))

  def findIds6(ids: Set[Int], repo1: Repo1, repo2: Repo1, repo3: Repo1): Task[Set[Int]] =
    for {
      repo1Ids <- repo1.findByIds(ids)
      repo2And3Ids <- repo2.findByIds(repo1Ids).zipPar(repo3.findByIds(repo1Ids))
    } yield repo2And3Ids._1 ++ repo2And3Ids._2

  def findIds7(ids: Set[Int], repo1: Repo1, repo2: Repo1, repo3: Repo1): Task[Set[Int]] =
    repo1
      .findByIds(ids)
      .flatMap(repo1Ids => repo2.findByIds(repo1Ids).zipPar(repo3.findByIds(repo1Ids)))
      .map(v => v._1 ++ v._2)

  def zip(id: Int, repo1: Repo1, repo2: Repo1): Task[(Int, Int)] = for {
    both <- repo1.findById(id).zip(repo2.findById(id))
  } yield both

  def zipPar(id: Int, repo1: Repo1, repo2: Repo1): Task[(Int, Int)] = for {
    both <- repo1.findById(id).zipPar(repo2.findById(id))
  } yield both

  def race(id: Int, repo1: Repo1, repo2: Repo1): Task[Int] = for {
    id1or2 <- repo1.findById(id).race(repo2.findById(id))
  } yield id1or2

  def raceEither(id: Int, repo1: Repo1, repo2: Repo1): Task[Either[Int, String]] = for {
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

  // def run = Console
  //   .print("Please enter your name: ")
  //   .flatMap { _ =>
  //     Console.readLine
  //       .flatMap { name =>
  //         Console.print(s"Hello $name!")
  //       }
  //   }

  def findById(id: Int): Task[Int] = ZIO.succeed(id)

  def run = for {
    ids <- findById(1).debug.zipPar(findById(1)).debug
    ret <- ZIO.when(ids._2 == 0)(findById(3)).debug
  } yield ret
}
