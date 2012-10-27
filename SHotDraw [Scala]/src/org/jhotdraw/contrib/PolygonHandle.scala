/*
 * @(#)PolygonHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.contrib

import org.jhotdraw.framework._
import org.jhotdraw.standard._
import org.jhotdraw.figures.PolyLineHandle
import org.jhotdraw.util.Undoable
import java.awt.Point

/**
 * A handle for a node on the polygon.
 * Based on PolyLineHandle
 *
 * @author Doug Lea  (dl at gee, Fri Feb 28 07:47:13 1997)
 * @version <$CURRENT_VERSION$>
 */
object PolygonHandle {

  class UndoActivity(newView: DrawingView, newPointIndex: Int) extends PolyLineHandle.UndoActivity(newView, newPointIndex) {

    protected override def movePointToOldLocation: Boolean = {
      val fe: FigureEnumeration = getAffectedFigures
      if (!fe.hasNext) {
        return false
      }
      val figure: PolygonFigure = fe.next.asInstanceOf[PolygonFigure]
      val backupPoint: Point = figure.pointAt(getPointIndex)
      figure.setPointAt(getOldPoint, getPointIndex)
      figure.smoothPoints
      setOldPoint(backupPoint)
      true
    }
  }

}

class PolygonHandle(owner: PolygonFigure, fLocator: Locator, fIndex: Int) extends AbstractHandle(owner) {
  
  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    setUndoActivity(createUndoActivity(view, fIndex))
    getUndoActivity.setAffectedFigures(new SingleFigureEnumerator(owner))
    (getUndoActivity.asInstanceOf[PolygonHandle.UndoActivity]).setOldPoint(new Point(x, y))
  }

  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val index: Int = (getUndoActivity.asInstanceOf[PolyLineHandle.UndoActivity]).getPointIndex
    myOwner.setPointAt(new Point(x, y), index)
  }

  override def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    myOwner.smoothPoints
    if ((x == anchorX) && (y == anchorY)) {
      setUndoActivity(null)
    }
  }

  def locate: Point = fLocator.locate(owner)

  private def myOwner: PolygonFigure = owner

  /**
   * Factory method for undo activity. To be overriden by subclasses.
   */
  protected def createUndoActivity(newView: DrawingView, newPointIndex: Int): Undoable = new PolygonHandle.UndoActivity(newView, newPointIndex)
}

