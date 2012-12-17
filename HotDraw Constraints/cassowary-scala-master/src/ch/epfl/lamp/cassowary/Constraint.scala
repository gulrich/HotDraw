package ch.epfl.lamp.cassowary

import scala.collection.mutable.FastSet

abstract class Constraint(val strength: Strength, val weight: Double) extends Serializable {

  private[cassowary] var markerVar: AbstractVar = _
  private[cassowary] var errorVars: FastSet[AbstractVar] = null

  def this(strength: Strength) = this(strength, 1d)

  def this() = this(Strength.Required, 1d)

  def expression: LinearExpression
  def isRequired: Boolean = strength eq Strength.Required

  override def toString: String =
    "strength=" + strength.toString + ", weight=" + weight + ", " + expression
}