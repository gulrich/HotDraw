// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// Scala implementation by Ingo Maier <ingo.maier@epfl.ch>
// (c) 1998, 1999, 2012 Alan Borning, Michael Noth, Greg Badros, and Ingo Maier.
//
// Implementation of the DraggableBox class
// By Michael Noth, 9 Feb 1998
package ch.epfl.lamp.cassowary.demos

import java.awt.Graphics
import ch.epfl.lamp.cassowary._

class DraggableBox(idx: Int, solver: SimplexSolver) {
  private val center = new CPoint(0, 0, idx, solver)
  private val width, height = 6

  def sx = center.x.value.toInt
  def sy = center.y.value.toInt

  def draw(g: Graphics) {
    g.drawRect(sx - (width / 2), sy - (height / 2), width, height)
  }

  def fill(g: Graphics) {
    g.fillRect(sx - (width / 2), sy - (height / 2), width, height)
  }

  def setCenter(x: Double, y: Double) {
    center.set(x, y)
  }

  def centerX: Double = center.x.value
  def centerY: Double = center.y.value
  def x: CVar = center.x
  def y: CVar = center.y
  def centerPt: CPoint = center

  def contains(x: Int, y: Int): Boolean =
    (x >= sx - width / 2) && (x <= sx + width / 2) && (y >= sy - height / 2) && (y <= sy + height / 2)

}