import zio._

object ZIOApp extends ZIOAppDefault {
  val task = ZIO.succeed({
    println("fiber task returns 1")
    1
  })

  def run = task.map(v => println(s"result: $v"))
}
// [info] running ZIOApp
// fiber task returns 1
// result: 1
