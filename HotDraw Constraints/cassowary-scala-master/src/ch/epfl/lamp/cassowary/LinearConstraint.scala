package ch.epfl.lamp.cassowary

class LinearConstraint(val expression: LinearExpression, s: Strength, w: Double) extends Constraint(s, w) {
  def this(e: LinearExpression, s: Strength) = this(e, s, 1.0)
  def this(e: LinearExpression) = this(e, Strength.Required, 1.0)
}