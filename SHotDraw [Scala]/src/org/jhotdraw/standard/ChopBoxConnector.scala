/*
 * @(#)ChopBoxConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import java.awt._
import org.jhotdraw.framework._
import org.jhotdraw.util.Geom

/**
 * A ChopBoxConnector locates connection points by
 * choping the connection between the centers of the
 * two figures at the display box.
 *
 * @see Connector
 *
 * @version <$CURRENT_VERSION$>
 */
object ChopBoxConnector {
  private final val serialVersionUID: Long = -1461450322712345462L
}

class ChopBoxConnector(owner: Figure) extends AbstractConnector(owner) {

  def this() {
    this(null)
  }
  
  override def findStart(connection: ConnectionFigure): Point = {
    val startFigure: Figure = connection.getStartConnector.owner
    val r2: Rectangle = connection.getEndConnector.displayBox
    var r2c: Point = null
    if (connection.pointCount == 2) {
      r2c = new Point(r2.x + r2.width / 2, r2.y + r2.height / 2)
    }
    else {
      r2c = connection.pointAt(1)
    }
    chop(startFigure, r2c)
  }

  override def findEnd(connection: ConnectionFigure): Point = {
    val endFigure: Figure = connection.getEndConnector.owner
    val r1: Rectangle = connection.getStartConnector.displayBox
    var r1c: Point = null
    if (connection.pointCount == 2) {
      r1c = new Point(r1.x + r1.width / 2, r1.y + r1.height / 2)
    }
    else {
      r1c = connection.pointAt(connection.pointCount - 2)
    }
    chop(endFigure, r1c)
  }

  protected def chop(target: Figure, from: Point): Point = {
    val r: Rectangle = target.displayBox
    Geom.angleToPoint(r, (Geom.pointToAngle(r, from)))
  }
}


