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
package org.jhotdraw.figures

import org.jhotdraw.framework._
import org.jhotdraw.standard._
import org.jhotdraw.util.Geom
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
    val line: LineConnection = ownerConnection
    val p1: Point = line.pointAt(fSegment)
    val p2: Point = line.pointAt(fSegment + 1)
    val ddx: Int = x - fLastX
    val ddy: Int = y - fLastY
    var np1: Point = null
    var np2: Point = null
    if (isVertical(p1, p2)) {
      val cx: Int = constrainX(p1.x + ddx)
      np1 = new Point(cx, p1.y)
      np2 = new Point(cx, p2.y)
    } else {
      val cy: Int = constrainY(p1.y + ddy)
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
    val line: LineConnection = ownerConnection
    val segment: Int = Math.min(fSegment, line.pointCount - 2)
    val p1: Point = line.pointAt(segment)
    val p2: Point = line.pointAt(segment + 1)
    new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
  }

  override def draw(g: Graphics) {
    val r: Rectangle = displayBox
    g.setColor(Color.yellow)
    g.fillOval(r.x, r.y, r.width, r.height)
    g.setColor(Color.black)
    g.drawOval(r.x, r.y, r.width, r.height)
  }

  private def constrainX(x: Int): Int = {
    var _x = x
    val line: LineConnection = ownerConnection
    val startFigure: Figure = line.getStartConnector.owner
    val endFigure: Figure = line.getEndConnector.owner
    val start: Rectangle = startFigure.displayBox
    val end: Rectangle = endFigure.displayBox
    val i1: Insets = startFigure.connectionInsets
    val i2: Insets = endFigure.connectionInsets
    var r1x: Int = 0
    var r1width: Int = 0
    var r2x: Int = 0
    var r2width: Int = 0
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
    val line: LineConnection = ownerConnection
    val startFigure: Figure = line.getStartConnector.owner
    val endFigure: Figure = line.getEndConnector.owner
    val start: Rectangle = startFigure.displayBox
    val end: Rectangle = endFigure.displayBox
    val i1: Insets = startFigure.connectionInsets
    val i2: Insets = endFigure.connectionInsets
    var r1y: Int = 0
    var r1height: Int = 0
    var r2y: Int = 0
    var r2height: Int = 0
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

  private var fLastX: Int = 0
  private var fLastY: Int = 0
}

