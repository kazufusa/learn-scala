class Rational(val n: Int, val d: Int) {
  require(d != 0)

  override def toString: String = s"$n/$d"

  def add(that: Rational) = {
    new Rational(n = that.d + that.n * d, d * that.d)
  }
}

object Rational {
  def apply(n:Int, d:Int) = new Rational(n, d)
}