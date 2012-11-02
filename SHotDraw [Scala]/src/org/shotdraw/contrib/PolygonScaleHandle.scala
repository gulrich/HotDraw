/*
 * @(#)PolygonScaleHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.contrib

import org.shotdraw.framework._
import org.shotdraw.standard._
import org.shotdraw.util.Geom
import org.shotdraw.util.Undoable
import org.shotdraw.util.UndoableAdapter
import java.awt.Polygon
import java.awt.Rectangle
import java.awt.Point
import java.awt.Graphics
import java.awt.Color

/**
 * A Handle to scale and rotate a PolygonFigure
 * Based on RadiusHandle
 *
 * @author Doug Lea  (dl at gee, Sat Mar 1 09:06:09 1997)
 * @version <$CURRENT_VERSION$>
 */
object PolygonScaleHandle {

  class UndoActivity(newView: DrawingView) extends UndoableAdapter(newView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = super.undo && resetPolygon

    override def redo: Boolean = isRedoable && resetPolygon

    protected def resetPolygon: Boolean = {
      val fe: Iterator[Figure] = getAffectedFigures.iterator
      if (!fe.hasNext) {
        return false
      }
      val figure: PolygonFigure = fe.next.asInstanceOf[PolygonFigure]
      val backupPolygon: Polygon = figure.getPolygon
      figure.willChange
      figure.setInternalPolygon(getPolygon)
      figure.changed
      setPolygon(backupPolygon)
      true
    }

    private[contrib] def setPolygon(newPolygon: Polygon) {
      myPolygon = newPolygon
    }

    def getPolygon: Polygon = myPolygon

    private var myPolygon: Polygon = new Polygon
  }

}

class PolygonScaleHandle(owner: PolygonFigure) extends AbstractHandle(owner) {
  import AbstractHandle._
  /**
   * @param x the x position where the interaction started
   * @param y the y position where the interaction started
   * @param view the handles container
   */
  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    fCurrent = new Point(x, y)
    val activity: PolygonScaleHandle.UndoActivity = createUndoActivity(view).asInstanceOf[PolygonScaleHandle.UndoActivity]
    setUndoActivity(activity)
    activity.setAffectedFigures(List(owner))
    activity.setPolygon(owner.getPolygon)
  }

  /**
   * Tracks a step of the interaction.
   * @param x the current x position
   * @param y the current y position
   * @param anchorX the x position where the interaction started
   * @param anchorY the y position where the interaction started
   */
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    fCurrent = new Point(x, y)
    val polygon: Polygon = (getUndoActivity.asInstanceOf[PolygonScaleHandle.UndoActivity]).getPolygon
    owner.scaleRotate(new Point(anchorX, anchorY), polygon, fCurrent)
  }

  /**
   * Tracks the end of the interaction.
   * @param x the current x position
   * @param y the current y position
   * @param anchorX the x position where the interaction started
   * @param anchorY the y position where the interaction started
   */
  override def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    ((owner).asInstanceOf[PolygonFigure]).smoothPoints
    if ((fCurrent.x == anchorX) && (fCurrent.y == anchorY)) {
      setUndoActivity(null)
    }
    fCurrent = null
  }

  def locate: Point = {
    if (fCurrent == null) getOrigin
    else fCurrent
  }

  private[contrib] def getOrigin: Point = {
    val outer: Point = ((owner).asInstanceOf[PolygonFigure]).outermostPoint
    val ctr: Point = ((owner).asInstanceOf[PolygonFigure]).center
    val len: Double = Geom.length(outer.x, outer.y, ctr.x, ctr.y)
    if (len == 0) {
      return new Point(outer.x - HANDLESIZE / 2, outer.y + HANDLESIZE / 2)
    }
    val u: Double = HANDLESIZE / len
    if (u > 1.0) new Point((outer.x * 3 + ctr.x) / 4, (outer.y * 3 + ctr.y) / 4)
    else new Point((outer.x * (1.0 - u) + ctr.x * u).asInstanceOf[Int], (outer.y * (1.0 - u) + ctr.y * u).asInstanceOf[Int])
  }

  override def draw(g: Graphics) {
    val r: Rectangle = displayBox
    g.setColor(Color.yellow)
    g.fillOval(r.x, r.y, r.width, r.height)
    g.setColor(Color.black)
    g.drawOval(r.x, r.y, r.width, r.height)
  }

  /**
   * Factory method for undo activity. To be overriden by subclasses.
   */
  protected def createUndoActivity(newView: DrawingView): Undoable = new PolygonScaleHandle.UndoActivity(newView)
  
  private var fCurrent: Point = null
}

