import org.scalatest.flatspec.AnyFlatSpec

class RationalSpec extends AnyFlatSpec {
  "toString" should "return Rational values" in {
    assert(Rational(1, 2).toString === "1/2")
  }

  "add" should "return added Rational value" in {
    val actual = Rational(1, 2) add Rational(1, 4)
    val expected = Rational(36, 48)
    assert(actual === expected)
  }
}
