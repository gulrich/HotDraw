package ch.epfl.lamp.cassowary

abstract class EditOrStayConstraint(val variable: CVar, strength: Strength, weight: Double)
  extends Constraint(strength, weight) {

  val expression = new LinearExpression(variable, -1.0, variable.value);

  def this(v: CVar, strength: Strength) = this(v, strength, 1.0)
  def this(v: CVar) = this(v, Strength.Required, 1.0)
}