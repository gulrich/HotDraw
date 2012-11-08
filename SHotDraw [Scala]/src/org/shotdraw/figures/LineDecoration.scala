/*
 * @(#)LineDecoration.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import org.shotdraw.util.Storable
import java.awt._
import java.io.Serializable

/**
 * Decorate the start or end point of a line or poly line figure.
 * LineDecoration is the base class for the different line decorations.
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
trait LineDecoration extends Storable with Cloneable with Serializable {
  /**
   * Draws the decoration in the direction specified by the two points.
   */
  def draw(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int)

  /**
   * @return the display box of a LineDecoration.
   */
  def displayBox: Rectangle
}

