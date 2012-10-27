/*
 * @(#)ChopEllipseConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.figures

import java.awt._
import org.jhotdraw.framework._
import org.jhotdraw.standard._
import org.jhotdraw.util.Geom

/**
 * A ChopEllipseConnector locates a connection point by
 * chopping the connection at the ellipse defined by the
 * figure's display box.
 *
 * @version <$CURRENT_VERSION$>
 */
object ChopEllipseConnector {
  private final val serialVersionUID: Long = -3165091511154766610L
}

class ChopEllipseConnector(owner: Figure) extends ChopBoxConnector(owner) {
  def this() {
    this(null)
  }

  override protected def chop(target: Figure, from: Point): Point = {
    val r: Rectangle = target.displayBox
    Geom.ovalAngleToPoint(r, Geom.pointToAngle(r, from))
  }
}


