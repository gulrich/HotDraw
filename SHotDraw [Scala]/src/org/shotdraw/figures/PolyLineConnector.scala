/*
 * @(#)PolyLineConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.awt._
import org.shotdraw.framework._
import org.shotdraw.standard._
import org.shotdraw.util._


/**
 * PolyLineConnector finds connection points on a
 * PolyLineFigure.
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
class PolyLineConnector(fOwner: PolyLineFigure) extends ChopBoxConnector(fOwner) {
  def this() {
    this(null)
  }
  
  
  override protected def chop(target: Figure, from: Point): Point = {
    val p = owner match {
      case plf: PolyLineFigure => plf
      case x => sys.error(x.getClass + " found, PolyLineFigure expected")
    }
    val ctr = p.center
    var cx: Int = -1
    var cy: Int = -1
    var len: Long = Long.MaxValue
    for(i <- 0 to p.pointCount - 2) {
      val p1 = p.pointAt(i)
      val p2 = p.pointAt(i + 1)
      val chop = Geom.intersect(p1.x, p1.y, p2.x, p2.y, from.x, from.y, ctr.x, ctr.y)
      if (chop != null) {
        val cl = Geom.length2(chop.x, chop.y, from.x, from.y)
        if (cl < len) {
          len = cl
          cx = chop.x
          cy = chop.y
        }
      }
    }
    for(i <- 0 to p.pointCount-1) {
      val pp = p.pointAt(i)
      val l = Geom.length2(pp.x, pp.y, from.x, from.y)
      if (l < len) {
        len = l
        cx = pp.x
        cy = pp.y
      }
    }
    new Point(cx, cy)
  }
}


