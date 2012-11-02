/*
 * @(#)GroupFigure.java
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
import org.shotdraw.framework.FigureAttributeConstant
import org.shotdraw.framework.Handle
import org.shotdraw.standard.CompositeFigure
import org.shotdraw.standard.RelativeLocator
import org.shotdraw.framework.Figure

/**
 * A Figure that groups a collection of figures.
 *
 * @version <$CURRENT_VERSION$>
 */
object GroupFigure {
  private final val serialVersionUID: Long = 8311226373023297933L
}

class GroupFigure extends CompositeFigure {
  /**
   * GroupFigures cannot be connected
   */
  override def canConnect: Boolean = false

  /**
   * Gets the display box. The display box is defined as the union
   * of the contained figures.
   */
  def displayBox: Rectangle = {
    val r: Rectangle = new Rectangle
    figures foreach {f => r.add(f.displayBox)}
    r
  }

  def basicDisplayBox(origin: Point, corner: Point) {}

  override def decompose: Seq[Figure] = fFigures

  /**
   * Gets the handles for the GroupFigure.
   */
  def handles: Seq[Handle] = {
    new GroupHandle(this, RelativeLocator.northWest) ::
        new GroupHandle(this, RelativeLocator.northEast) ::
        new GroupHandle(this, RelativeLocator.southWest) ::
        new GroupHandle(this, RelativeLocator.southEast) :: Nil
  }
}

