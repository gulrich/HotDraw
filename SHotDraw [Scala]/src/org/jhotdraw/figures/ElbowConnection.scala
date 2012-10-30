/*
 * @(#)ElbowConnection.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.figures

import java.awt.Point
import java.awt.Rectangle
import org.jhotdraw.framework.Figure
import org.jhotdraw.framework.Handle

import org.jhotdraw.framework.Locator
import org.jhotdraw.standard.AbstractLocator
import org.jhotdraw.standard.ChangeConnectionEndHandle
import org.jhotdraw.standard.ChangeConnectionStartHandle

import org.jhotdraw.standard.NullHandle
import org.jhotdraw.util.Geom

/**
 * A LineConnection that constrains a connection to
 * orthogonal lines.
 *
 * @version <$CURRENT_VERSION$>
 */
object ElbowConnection {
  private final val serialVersionUID: Long = 2193968743082078559L
}

class ElbowConnection extends LineConnection {

  override def updateConnection {
    super.updateConnection
    updatePoints
  }

  override def layoutConnection {}

  /**
   * Gets the handles of the figure.
   */
  override def handles: Seq[Handle] = {
    var handles: List[Handle] = List(new ChangeConnectionStartHandle(this))
    for(i <- 0 to fPoints.size - 2) handles ::= new NullHandle(this, PolyLineFigure.locator(i))
    handles ::= new ChangeConnectionEndHandle(this)
    for(i <- 0 to fPoints.size - 2) handles ::= new ElbowHandle(this, i)
    handles
  }

  override def connectedTextLocator(f: Figure): Locator = new ElbowTextLocator

  protected def updatePoints {
    willChange
    val start: Point = startPoint
    val end: Point = endPoint
    fPoints = List()
    fPoints ::= start
    if (start.x == end.x || start.y == end.y) {
      fPoints ::= end
    } else {
      val r1: Rectangle = getStartConnector.owner.displayBox
      val r2: Rectangle = getEndConnector.owner.displayBox
      val dir: Int = Geom.direction(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2)
      if (dir == Geom.NORTH || dir == Geom.SOUTH) {
        fPoints ::= new Point(start.x, (start.y + end.y) / 2)
        fPoints ::= new Point(end.x, (start.y + end.y) / 2)
      } else {
        fPoints ::= new Point((start.x + end.x) / 2, start.y)
        fPoints ::= new Point((start.x + end.x) / 2, end.y)
      }
      fPoints ::= end
    }
    changed
  }
}

object ElbowTextLocator {
  private final val serialVersionUID: Long = -5220096092980161909L
}

class ElbowTextLocator extends AbstractLocator {
  def locate(owner: Figure): Point = {
    val p: Point = owner.center
    new Point(p.x, p.y - 10)
  }
}


