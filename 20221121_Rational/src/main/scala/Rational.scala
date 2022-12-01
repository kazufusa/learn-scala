class Rational(val n: Int, val d: Int) {
  require(d != 0)

  override def toString: String = s"$n/$d"

  def add(that: Rational) = Rational(n = that.d + that.n * d, d * that.d)

  override def equals(other: Any) = other match {
    case that: Rational =>
      (that canEqual this) && (this.n == that.n) && (this.d == that.d)
    case _ => false
  }

  def canEqual(other: Any) = other.isInstanceOf[Rational]
}

object Rational {
  def apply(n: Int, d: Int) = {
    val _lcm = lcm(n, d)
    new Rational(n / _lcm, d / _lcm)
  }

  def lcm(a: Int, b: Int): Int = lcmCore(math.max(a, b), math.min(a, b))

  def lcmCore(larger: Int, smaller: Int): Int = {
    if (smaller == 0) {
      larger
    } else {
      lcmCore(smaller, larger % smaller)
    }
  }
}
