package part1

import java.net.URL
import part1.Data._
import zio.IO
import zio.UIO

trait PugService {
  def findPug(name: String): IO[PugNotFound, Pug]
  def randomPugPicture: UIO[String]
  def addPug(pug: Pug): UIO[Unit]
  def editPugPicture(name: String, pictureUrl: URL): IO[PugNotFound, Unit]
}

object PugService {
  val dummy: PugService = new PugService {
    override def findPug(name: String): IO[PugNotFound, Pug] =
      IO.succeed(
        Pug(
          "Patrick",
          List("Pat"),
          Some(new URL("https://m.media-amazon.com/images/I/81tRAIFb9OL._SS500_.jpg")),
          Color.FAWN
        )
      )
    override def randomPugPicture =
      UIO.succeed("https://m.media-amazon.com/images/I/81tRAIFb9OL._SS500_.jpg")
    override def addPug(pug: Pug) = UIO.unit
    override def editPugPicture(name: String, pictureUrl: URL) = IO.fail(PugNotFound(name))
  }
}
