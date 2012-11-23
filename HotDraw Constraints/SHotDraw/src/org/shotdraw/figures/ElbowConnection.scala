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
package org.shotdraw.figures

import java.awt.Point
import java.awt.Rectangle
import org.shotdraw.framework.Figure
import org.shotdraw.framework.Handle
import org.shotdraw.framework.Locator
import org.shotdraw.standard.AbstractLocator
import org.shotdraw.standard.ChangeConnectionEndHandle
import org.shotdraw.standard.ChangeConnectionStartHandle
import org.shotdraw.standard.NullHandle
import org.shotdraw.util.Geom
import scala.collection.mutable.ArrayBuffer

/**
 * A LineConnection that constrains a connection to
 * orthogonal lines.
 *
 * @version <$CURRENT_VERSION$>
 */
class ElbowConnection extends LineConnection {

  override def updateConnection() {
    super.updateConnection()
    updatePoints()
  }

  override def layoutConnection() {}

  /**
   * Gets the handles of the figure.
   */
  override def handles: Seq[Handle] = {
    var handles = ArrayBuffer[Handle](new ChangeConnectionStartHandle(this))
    for(i <- 0 to fPoints.size - 2) handles += new NullHandle(this, PolyLineFigure.locator(i))
    handles += new ChangeConnectionEndHandle(this)
    for(i <- 0 to fPoints.size - 2) handles += new ElbowHandle(this, i)
    handles
  }

  override def connectedTextLocator(f: Figure): Locator = new ElbowTextLocator

  protected def updatePoints() {
    willChange()
    val start = startPoint
    val end = endPoint
    fPoints = ArrayBuffer()
    fPoints += start
    if (start.x == end.x || start.y == end.y) {
      fPoints += end
    } else {
      val r1 = getStartConnector.owner.displayBox
      val r2 = getEndConnector.owner.displayBox
      val dir = Geom.direction(r1.x + r1.width / 2, r1.y + r1.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2)
      if (dir == Geom.NORTH || dir == Geom.SOUTH) {
        fPoints += new Point(start.x, (start.y + end.y) / 2)
        fPoints += new Point(end.x, (start.y + end.y) / 2)
      } else {
        fPoints += new Point((start.x + end.x) / 2, start.y)
        fPoints += new Point((start.x + end.x) / 2, end.y)
      }
      fPoints += end
    }
    changed()
  }
}

class ElbowTextLocator extends AbstractLocator {
  def locate(owner: Figure): Point = {
    val p = owner.center
    new Point(p.x, p.y - 10)
  }
}


