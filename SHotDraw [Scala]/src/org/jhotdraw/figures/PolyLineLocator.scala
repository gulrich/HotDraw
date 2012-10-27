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
package org.jhotdraw.figures

import java.awt._
import org.jhotdraw.framework._
import org.jhotdraw.standard._

/**
 * A poly line figure consists of a list of points.
 * It has an optional line decoration at the start and end.
 *
 * @see LineDecoration
 *
 * @version <$CURRENT_VERSION$>
 */
object PolyLineLocator {
  private final val serialVersionUID: Long = -2695322556233654352L
}

class PolyLineLocator(private[figures] fIndex: Int) extends AbstractLocator {

  def locate(owner: Figure): Point = {
    val plf: PolyLineFigure = owner.asInstanceOf[PolyLineFigure]
    if (fIndex < plf.pointCount) (owner.asInstanceOf[PolyLineFigure]).pointAt(fIndex)
    else new Point(0, 0)
  }
}

