/*
 * @(#)ShortestDistanceConnector.java
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
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Point

/**
 * A ShortestDistance locates connection points by
 * finding the shortest distance between the start and
 * end of the connection.
 * It doesn't connect to the areas defined by Figure.connectionInsets()
 *
 * @see Figure#connectionInsets
 * @see Connector
 *
 * @version <$CURRENT_VERSION$>
 */
class ShortestDistanceConnector(owner: Figure) extends AbstractConnector(owner) {

  override def findStart(connection: ConnectionFigure): Point = findPoint(connection, true)

  override def findEnd(connection: ConnectionFigure): Point = findPoint(connection, false)

  protected def findPoint(connection: ConnectionFigure, getStart: Boolean): Point = {
    val startFigure: Figure = connection.getStartConnector.owner
    val endFigure: Figure = connection.getEndConnector.owner
    val r1: Rectangle = startFigure.displayBox
    val r2: Rectangle = endFigure.displayBox
    val i1: Insets = startFigure.connectionInsets
    val i2: Insets = endFigure.connectionInsets
    var p1: Point = null
    var p2: Point = null
    var start: Point = null
    var end: Point = null
    var s: Point = null
    var e: Point = null
    var len2: Long = Long.MaxValue
    var l2: Long = 0L
    var x1: Int = 0
    var x2: Int = 0
    var y1: Int = 0
    var y2: Int = 0
    var xmin: Int = 0
    var xmax: Int = 0
    var ymin: Int = 0
    var ymax: Int = 0
    var r1x: Int = 0
    var r1width: Int = 0
    var r2x: Int = 0
    var r2width: Int = 0
    var r1y: Int = 0
    var r1height: Int = 0
    var r2y: Int = 0
    var r2height: Int = 0
    r1x = r1.x + i1.left
    r1width = r1.width - i1.left - i1.right - 1
    r2x = r2.x + i2.left
    r2width = r2.width - i2.left - i2.right - 1
    if (r1x + r1width < r2x) {
      x1 = r1x + r1width
      x2 = r2x
    } else if (r1x > r2x + r2width) {
      x1 = r1x
      x2 = r2x + r2width
    } else {
      xmax = math.max(r1x, r2x)
      xmin = math.min(r1x + r1width, r2x + r2width)
      x1 = (xmax + xmin) / 2
      x2 = x1      
    }
    r1y = r1.y + i1.top
    r1height = r1.height - i1.top - i1.bottom - 1
    r2y = r2.y + i2.top
    r2height = r2.height - i2.top - i2.bottom - 1
    if (r1y + r1height < r2y) {
      y1 = r1y + r1height
      y2 = r2y
    } else if (r1y > r2y + r2height) {
      y1 = r1y
      y2 = r2y + r2height
    } else {
      ymax = math.max(r1y, r2y)
      ymin = math.min(r1y + r1height, r2y + r2height)
      y1 = (ymax + ymin) / 2
      y2 = y1
    }
    for(i <- 0 to 3) i match {
      case 0 =>
        p1 = Geom.east(r1)
        p2 = Geom.west(r2)
        s = new Point(p1.x, y1)
        e = new Point(p2.x, y2)
      case 1 =>
        p1 = Geom.west(r1)
        p2 = Geom.east(r2)
        s = new Point(p1.x, y1)
        e = new Point(p2.x, y2)
      case 2 =>
        p1 = Geom.north(r1)
        p2 = Geom.south(r2)
        s = new Point(x1, p1.y)
        e = new Point(x2, p2.y)
      case 3 =>
        p1 = Geom.south(r1)
        p2 = Geom.north(r2)
        s = new Point(x1, p1.y)
        e = new Point(x2, p2.y)
    }
    l2 = Geom.length2(s.x, s.y, e.x, e.y)
    if (l2 < len2) {
      start = s
      end = e
      len2 = l2
    }
    if (getStart) start
    else end
  }
}