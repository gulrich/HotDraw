/*
 * @(#)RadiusHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import org.shotdraw.framework._
import org.shotdraw.standard._
import org.shotdraw.util.Geom
import org.shotdraw.util.Undoable
import org.shotdraw.util.UndoableAdapter
import java.awt.Rectangle
import java.awt.Point
import java.awt.Graphics
import java.awt.Color

/**
 * A Handle to manipulate the radius of a round corner rectangle.
 *
 * @version <$CURRENT_VERSION$>
 */
object RadiusHandle {
  private final val OFFSET = 4

  class UndoActivity(newView: DrawingView) extends UndoableAdapter(newView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = super.undo && resetRadius

    override def redo: Boolean = isRedoable && resetRadius

    protected def resetRadius: Boolean = {
      val fe = getAffectedFigures.iterator
      if (!fe.hasNext) {
        return false
      }
      val currentFigure = fe.next.asInstanceOf[RoundRectangleFigure]
      val figureRadius = currentFigure.getArc
      currentFigure.setArc(getOldRadius.x, getOldRadius.y)
      setOldRadius(figureRadius)
      true
    }

    private[figures] def setOldRadius(newOldRadius: Point) {
      myOldRadius = newOldRadius
    }

    def getOldRadius: Point = myOldRadius

    private var myOldRadius: Point = null
  }

}

class RadiusHandle(owner: RoundRectangleFigure) extends AbstractHandle(owner) {
  import RadiusHandle._
  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    setUndoActivity(createUndoActivity(view))
    getUndoActivity.setAffectedFigures(List(owner))
    (getUndoActivity.asInstanceOf[RadiusHandle.UndoActivity]).setOldRadius((owner.asInstanceOf[RoundRectangleFigure]).getArc)
  }

  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val dx = x - anchorX
    val dy = y - anchorY
    val _owner = owner.asInstanceOf[RoundRectangleFigure]
    val r = _owner.displayBox
    val originalRadius = (getUndoActivity.asInstanceOf[RadiusHandle.UndoActivity]).getOldRadius
    val rx = Geom.range(0, r.width, 2 * (originalRadius.x / 2 + dx))
    val ry = Geom.range(0, r.height, 2 * (originalRadius.y / 2 + dy))
    _owner.setArc(rx, ry)
  }

  override def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val currentRadius = (owner.asInstanceOf[RoundRectangleFigure]).getArc
    val originalRadius = (getUndoActivity.asInstanceOf[RadiusHandle.UndoActivity]).getOldRadius
    if ((currentRadius.x == originalRadius.x) && (currentRadius.y == originalRadius.y)) {
      setUndoActivity(null)
    }
  }

  def locate: Point = {
    val _owner = owner.asInstanceOf[RoundRectangleFigure]
    val radius = _owner.getArc
    val r = _owner.displayBox
    new Point(r.x + radius.x / 2 + OFFSET, r.y + radius.y / 2 + OFFSET)
  }

  override def draw(g: Graphics) {
    val r = displayBox
    g.setColor(Color.yellow)
    g.fillOval(r.x, r.y, r.width, r.height)
    g.setColor(Color.black)
    g.drawOval(r.x, r.y, r.width, r.height)
  }

  /**
   * Factory method for undo activity. To be overriden by subclasses.
   */
  protected def createUndoActivity(newView: DrawingView): Undoable = new RadiusHandle.UndoActivity(newView)
}