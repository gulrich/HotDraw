/*
 * @(#)PolyLineLocator.java
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

/**
 * A poly line figure consists of a list of points.
 * It has an optional line decoration at the start and end.
 *
 * @see LineDecoration
 *
 * @version <$CURRENT_VERSION$>
 */
class PolyLineLocator(private[figures] fIndex: Int) extends AbstractLocator {

  def locate(owner: Figure): Point = {
    val plf: PolyLineFigure = owner.asInstanceOf[PolyLineFigure]
    if (fIndex < plf.pointCount) (owner.asInstanceOf[PolyLineFigure]).pointAt(fIndex)
    else new Point(0, 0)
  }
}

