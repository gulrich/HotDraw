/*
 * @(#)ChopPolygonConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.contrib

import java.awt._
import org.shotdraw.framework._
import org.shotdraw.standard._

/**
 * A ChopPolygonConnector locates a connection point by
 * chopping the connection at the polygon boundary.
 *
 * @author Erich Gamma
 * @version <$CURRENT_VERSION$>
 */
class ChopPolygonConnector(owner: Figure) extends ChopBoxConnector(owner) {
  def this() {
    this(null)
  }

  override protected def chop(target: Figure, from: Point): Point = target match {
    case pf: PolygonFigure => pf.chop(from)
    case _ => null
  }
}


