// Cassowary Incremental Constraint Solver
// Original Smalltalk Implementation by Alan Borning
// Java Implementation by Greg J. Badros, <gjb@cs.washington.edu>
// http://www.cs.washington.edu/homes/gjb
// Scala implementation by Ingo Maier <ingo.maier@epfl.ch>
// (c) 1998, 1999, 2012 Alan Borning, Michael Noth, Greg Badros, and Ingo Maier.
//
// Implementation of the QuadDemo
// By Michael Noth, 8 Feb 1998
// Port of QuadDemo made more like the C++ version -- gjb, 13 Feb 1998
package ch.epfl.lamp.cassowary.demos

import java.applet.Applet
import java.awt.{Graphics, Event, Color}
import ch.epfl.lamp.cassowary._

class QuadDemo extends Applet {
  private val db = Array.tabulate(8) { i => new DraggableBox(i) }
  private val mp = Array.tabulate(4) { i => db(i + 4) }
  private var dbDragging = -1
  private val solver: SimplexSolver = new SimplexSolver

  def constrain(cn: Constraint) = solver.addConstraint(cn)


  override def init: Unit = {
    db(0).setCenter(5, 5)
    db(1).setCenter(5, 200)
    db(2).setCenter(200, 200)
    db(3).setCenter(200, 5)
    try {
      // constrain the corners of the outer quad to stay put, with increasing strength
      solver.addPointStays(db(0).centerPt, db(1).centerPt, db(2).centerPt, db(3).centerPt)
      //solver.addPointStay(db(0).centerPt)
      //solver.addPointStay(db(1).centerPt)
      //solver.addPointStay(db(2).centerPt)
      //solver.addPointStay(db(3).centerPt)


      // constrain the corners of inner quad to the midpoints of the outer quad's edges
      constrain(mp(0).x :== (db(0).x + db(1).x) / 2)
      constrain(mp(0).y :== (db(0).y + db(1).y) / 2)
      constrain(mp(1).x :== (db(1).x + db(2).x) / 2)
      constrain(mp(1).y :== (db(1).y + db(2).y) / 2)
      constrain(mp(2).x :== (db(2).x + db(3).x) / 2)
      constrain(mp(2).y :== (db(2).y + db(3).y) / 2)
      constrain(mp(3).x :== (db(3).x + db(0).x) / 2)
      constrain(mp(3).y :== (db(3).y + db(0).y) / 2)

      // constrain the outer quad to remain simple, i.e., no edges cross.
      constrain(db(0).x + 10 :<= db(2).x)
      constrain(db(0).x + 10 :<= db(3).x)
      constrain(db(1).x + 10 :<= db(2).x)
      constrain(db(1).x + 10 :<= db(3).x)
      constrain(db(0).y + 10 :<= db(1).y)
      constrain(db(0).y + 10 :<= db(2).y)
      constrain(db(3).y + 10 :<= db(1).y)
      constrain(db(3).y + 10 :<= db(2).y)

      //constrain(db(0).y :== db(3).y)

      // constrain the outer quad to stay inside component
      val width = getWidth
      val height = getHeight
      constrain(db(0).x :>= 0.0)
      constrain(db(0).y :>= 0.0)
      constrain(db(1).x :>= 0.0)
      constrain(db(1).y :>= 0.0)
      constrain(db(2).x :>= 0.0)
      constrain(db(2).y :>= 0.0)
      constrain(db(3).x :>= 0.0)
      constrain(db(3).y :>= 0.0)
      constrain(db(0).x :<= width)
      constrain(db(0).y :<= height)
      constrain(db(1).x :<= width)
      constrain(db(1).y :<= height)
      constrain(db(2).x :<= width)
      constrain(db(2).y :<= height)
      constrain(db(3).x :<= width)
      constrain(db(3).y :<= height)


      //constrain(new LinearEquation(db(0).x - db(1).x - (db(2).x - db(3).x), Strength.Medium))
    } catch {
      case e: CLException => e.printStackTrace
    }
  }

  override def mouseDown(e: Event, x: Int, y: Int): Boolean = {
    dbDragging = db indexWhere { _ contains (x, y) }
    if (dbDragging != -1) {
      try {
        solver.addEditVar(db(dbDragging).x).addEditVar(db(dbDragging).y).beginEdit
      } catch {
        case ex: CLException => ex.printStackTrace
      }
      repaint()
    }
    true
  }

  override def mouseUp(e: Event, x: Int, y: Int): Boolean = {
    if (dbDragging != -1) {
      try {
        dbDragging = -1
        solver.endEdit
      } catch {
        case ex: LinearEquation => ex.printStackTrace
      }
      repaint()
    }
    true
  }

  override def mouseDrag(e: Event, x: Int, y: Int): Boolean = {
    if (dbDragging != -1) {
      try {
        solver.suggestValue(db(dbDragging).x, x).suggestValue(db(dbDragging).y, y).resolve
      } catch {
        case ex: LinearEquation => ex.printStackTrace
      }
      repaint()
    }
    true
  }

  override def paint(g: Graphics): Unit = {
    g.drawLine(db(0).centerX.toInt, db(0).centerY.toInt, db(1).centerX.toInt, db(1).centerY.toInt)
    g.drawLine(db(1).centerX.toInt, db(1).centerY.toInt, db(2).centerX.toInt, db(2).centerY.toInt)
    g.drawLine(db(2).centerX.toInt, db(2).centerY.toInt, db(3).centerX.toInt, db(3).centerY.toInt)
    g.drawLine(db(3).centerX.toInt, db(3).centerY.toInt, db(0).centerX.toInt, db(0).centerY.toInt)
    g.drawLine(mp(0).centerX.toInt, mp(0).centerY.toInt, mp(1).centerX.toInt, mp(1).centerY.toInt)
    g.drawLine(mp(1).centerX.toInt, mp(1).centerY.toInt, mp(2).centerX.toInt, mp(2).centerY.toInt)
    g.drawLine(mp(2).centerX.toInt, mp(2).centerY.toInt, mp(3).centerX.toInt, mp(3).centerY.toInt)
    g.drawLine(mp(3).centerX.toInt, mp(3).centerY.toInt, mp(0).centerX.toInt, mp(0).centerY.toInt)
    db foreach { _ draw g }

    if (dbDragging != -1) {
      db(dbDragging).fill(g)
    }
    g.setColor(Color.black)
  }

}