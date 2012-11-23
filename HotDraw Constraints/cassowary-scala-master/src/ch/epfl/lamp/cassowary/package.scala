/*
 * A linear incremental constraint solver in Scala.
 * Copyright 2012 Ingo Maier <ingo.maier@epfl.ch>
 *
 * Based on the Cassowary Incremental Constraint Solver.
 * Copyright 1998-2000 Greg J. Badros and Alan Borning
 *
 * See the LICENSE file for legal details.
 */
package ch.epfl.lamp

import scala.annotation.elidable

package object cassowary {
  private[cassowary] val debug = false

  def varsCreated = AbstractVar.iVariableNumber

  @elidable(elidable.ALL) def debugprint(s: =>String) {
    Console.err.println(s)
  }

  @elidable(elidable.ALL) def traceprint(s: =>String) {
    Console.err.println(s)
  }

  @elidable(elidable.ALL) def fnenterprint(s: =>String) {
    Console.err.println("* " + s)
  }

  @elidable(elidable.ALL) def fnexitprint(s: =>String) {
    Console.err.println("- " + s)
  }

  sealed abstract class Op

  object Op {
    object GEQ extends Op
    object LEQ extends Op
  }

  def approx(a: Double, b: Double): Boolean = {
    val epsilon = 1.0e-8
    if (a == 0d) math.abs(b) < epsilon
    else if (b == 0d) math.abs(a) < epsilon
    else math.abs(a - b) < math.abs(a) * epsilon
  }

  def removeFromArray[A >: Null <: AnyRef](array: Array[A], elem: A): Array[A] = {
    var i = 0
    while(i < array.length) {
      if(array(i) == elem) array(i) = null
      i += 1
    }
    array
  }
}