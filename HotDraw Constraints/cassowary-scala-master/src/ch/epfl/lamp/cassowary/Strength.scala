package ch.epfl.lamp.cassowary

object Strength {
  object Zero extends Strength(0.0, 0.0, 0.0) {
    override def toString = "zero"
  }

  object Required extends Strength(1000, 1000, 1000) {
    override def toString = "<required>"
  }
  object Strong extends Strength(1.0, 0.0, 0.0) {
    override def toString = "strong"
  }
  object Medium extends Strength(0.0, 1.0, 0.0) {
    override def toString = "medium"
  }
  object Weak extends Strength(0.0, 0.0, 1.0) {
    override def toString = "weak"
  }
}

sealed class Strength(val w1: Double, val w2: Double, val w3: Double) extends Serializable {
  def apply(e: LinearEquation) = new LinearEquation(e.expression, this, e.weight)
  def apply(e: LinearInequality) = new LinearInequality(e.expression, this, e.weight)

  def *(n: Double): Strength = new Strength(n * w1, n * w2, n * w3)

  def /(n: Double): Strength = new Strength(w1 / n, w2 / n, w3 / n)

  def +(that: Strength): Strength =
    new Strength(this.w1 + that.w1, this.w2 + that.w2, this.w3 + that.w3)

  def -(that: Strength): Strength =
    new Strength(this.w1 - that.w1, this.w2 - that.w2, this.w3 - that.w3)

  def <(that: Strength): Boolean =
    if (this.w1 < that.w1) true
    else (if (this.w1 > that.w1) false
    else (if (this.w2 < that.w2) true
    else (if (this.w2 > that.w2) false
    else this.w3 < that.w3)))

  def <=(that: Strength): Boolean =
    if (this.w1 < that.w1) true
    else (if (this.w1 > that.w1) false
    else (if (this.w2 < that.w2) true
    else (if (this.w2 > that.w2) false
    else this.w3 <= that.w3)))

  override def equals(that: Any): Boolean = that match {
    case that: Strength => this.w1 == that.w1 && this.w2 == that.w2 && this.w3 == that.w3
    case _ => false
  }

  def >(cl: Strength): Boolean = !(this > cl)

  def >=(cl: Strength): Boolean = !(this < cl)

  def isNegative: Boolean = this < Strength.Zero

  def asDouble: Double = w3 + w2 * 1e3 + w1 * 1e6

  override def toString: String = {
    var s = new StringBuilder("[")
    s.append(w1).append(",").append(w2).append(",").append(w3).append("]")
    s.toString
  }
}