package ch.epfl.lamp.cassowary

class EditConstraint(v: CVar, s: Strength, w: Double) extends EditOrStayConstraint(v, s, w) {

  def this(v: CVar, strength: Strength) = this(v, strength, 1d)
  def this(v: CVar) = this(v, Strength.Required, 1.0)

  override def toString: String = "EditConstraint: " + super.toString
}