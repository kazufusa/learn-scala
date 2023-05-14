package repository

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.testing.SttpBackendStub
import zio._
import zio.{ZIO, Runtime, Task}
import zio.test.Assertion._
import zio.test._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ApiRequestRepositorySpec extends AnyFlatSpec with Matchers with ScalaFutures {

  "" should "" in {
    val stubBackend = SttpBackendStub.asynchronousFuture.whenAnyRequest
      .thenRespond("{\"my_name\": \"abc\"}")
    val repository = new ApiRequestRepository {
      override protected[repository] lazy val backend = stubBackend
    }
    val actual: Future[String] = TestUtil.futureFromZIO(repository.resolveBy())
    actual.futureValue shouldBe "abc"
  }
}


object TestUtil {
  def futureFromZIO[R](task: Task[R]): Future[R] = Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.runToFuture(task)
    }
}
