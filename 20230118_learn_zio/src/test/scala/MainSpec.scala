import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.{ZIO, Runtime, Task, Unsafe}
import scala.concurrent.Future

class MainSpec extends AnyFlatSpec with Matchers with ScalaFutures {
  "hello" should "be hello" in {
    val actual = TestUtil.futureFromZIO(ZIO.succeed("hello"))
    actual.futureValue shouldBe "hello"
  }

  "" should "" in {
    val actual: Task[Int] =
      // ZIO.fail(new Exception("1")).flatMap(_ => ZIO.succeed(1))
      for {
        _ <- ZIO.fail(new Exception("1"))
        actual <- ZIO.succeed(1)
      } yield actual
    TestUtil.futureFromZIO(actual).failed.futureValue shouldBe an[Exception]
  }

  "Left(error)" should "be ZIO.fail" in {
    val expected = new Exception("some error")
    val input: Either[Exception, Int] = Left(expected)
    val actual = ZIO.fromEither(input).map(v => v * 2)
    TestUtil.futureFromZIO(actual).failed.futureValue shouldBe expected
  }

  "Right(1)" should "be ZIO.succeed(2)" in {
    val expected = 2
    val input: Either[Exception, Int] = Right(1)
    val actual = ZIO.fromEither(input).map(v => v * 2)
    TestUtil.futureFromZIO(actual).futureValue shouldBe expected
  }

  private def foldTest(flg: Boolean) =
    if (flg) ZIO.fail(new Exception("error")) else ZIO.succeed("hello")

  "error" should "error" in {
    val actual = foldTest(true)
    TestUtil.futureFromZIO(actual).failed.futureValue shouldBe an[Exception]
  }

  "fold" should "fold error" in {
    val actual = ZIO.foreachPar(Seq(true, false)) { flg =>
      foldTest(flg).fold(_ => "folded hello", data => data)
    }
    TestUtil.futureFromZIO(actual).futureValue shouldBe Seq(
      "folded hello",
      "hello"
    )
  }
}

object TestUtil {
  def futureFromZIO[R](task: Task[R]): Future[R] =
    Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.runToFuture(task)
    }
}
