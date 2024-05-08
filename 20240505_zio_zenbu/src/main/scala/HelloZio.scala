import zio._

object HelloZio extends App {
  val task = ZIO.succeed({
    println("fiber task returns 1")
    1
  })

  println("Start")
  val result = Unsafe
    .unsafe { implicit unsafe =>
      zio.Runtime.default.unsafe.run(task)
    }
  println("result: " + result.getOrElse(cause => s"failed: $cause"))
}
// Start
// fiber task returns 1
// result: 1
