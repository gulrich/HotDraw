package ch.epfl.lamp.cassowary

/**
 * Linear inequality of the form 'e >= 0'.
 */
class LinearInequality(e: LinearExpression, s: Strength, w: Double) extends LinearConstraint(e, s, w) {
  def this(e: LinearExpression, s: Strength) = this(e, s, 1d)
  def this(e: LinearExpression) = this(e, Strength.Required, 1.0)

  override def toString: String = super.toString + " >= 0"
}