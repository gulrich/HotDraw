/*
 * @(#)ElbowHandle.java
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
import java.awt.Rectangle
import java.awt.Point
import java.awt.Insets
import java.awt.Graphics
import java.awt.Color


/**
 * A Handle to move an ElbowConnection left/right or up/down.
 *
 * @version <$CURRENT_VERSION$>
 */
class ElbowHandle(owner: LineConnection, fSegment: Int) extends AbstractHandle(owner) {

  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    fLastX = x
    fLastY = y
  }

  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val line = ownerConnection
    val p1 = line.pointAt(fSegment)
    val p2 = line.pointAt(fSegment + 1)
    val ddx = x - fLastX
    val ddy = y - fLastY
    var np1: Point = null
    var np2: Point = null
    if (isVertical(p1, p2)) {
      val cx = constrainX(p1.x + ddx)
      np1 = new Point(cx, p1.y)
      np2 = new Point(cx, p2.y)
    } else {
      val cy = constrainY(p1.y + ddy)
      np1 = new Point(p1.x, cy)
      np2 = new Point(p2.x, cy)
    }
    line.setPointAt(np1, fSegment)
    line.setPointAt(np2, fSegment + 1)
    fLastX = x
    fLastY = y
  }

  private def isVertical(p1: Point, p2: Point): Boolean = p1.x == p2.x

  def locate: Point = {
    val line = ownerConnection
    val segment = math.min(fSegment, line.pointCount - 2)
    val p1 = line.pointAt(segment)
    val p2 = line.pointAt(segment + 1)
    new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
  }

  override def draw(g: Graphics) {
    val r = displayBox
    g.setColor(Color.yellow)
    g.fillOval(r.x, r.y, r.width, r.height)
    g.setColor(Color.black)
    g.drawOval(r.x, r.y, r.width, r.height)
  }

  private def constrainX(x: Int): Int = {
    var _x = x
    val line = ownerConnection
    val startFigure = line.getStartConnector.owner
    val endFigure = line.getEndConnector.owner
    val start = startFigure.displayBox
    val end = endFigure.displayBox
    val i1 = startFigure.connectionInsets
    val i2 = endFigure.connectionInsets
    var r1x = 0
    var r1width = 0
    var r2x = 0
    var r2width = 0
    r1x = start.x + i1.left
    r1width = start.width - i1.left - i1.right - 1
    r2x = end.x + i2.left
    r2width = end.width - i2.left - i2.right - 1
    if (fSegment == 0) {
      _x = Geom.range(r1x, r1x + r1width, _x)
    }
    if (fSegment == line.pointCount - 2) {
      _x = Geom.range(r2x, r2x + r2width, _x)
    }
    _x
  }

  private def constrainY(y: Int): Int = {
    var _y = y
    val line = ownerConnection
    val startFigure = line.getStartConnector.owner
    val endFigure = line.getEndConnector.owner
    val start = startFigure.displayBox
    val end = endFigure.displayBox
    val i1 = startFigure.connectionInsets
    val i2 = endFigure.connectionInsets
    var r1y = 0
    var r1height = 0
    var r2y = 0
    var r2height = 0
    r1y = start.y + i1.top
    r1height = start.height - i1.top - i1.bottom - 1
    r2y = end.y + i2.top
    r2height = end.height - i2.top - i2.bottom - 1
    if (fSegment == 0) {
      _y = Geom.range(r1y, r1y + r1height, _y)
    }
    if (fSegment == line.pointCount - 2) {
      _y = Geom.range(r2y, r2y + r2height, _y)
    }
    return y
  }

  private def ownerConnection: LineConnection =  owner

  private var fLastX = 0
  private var fLastY = 0
}

