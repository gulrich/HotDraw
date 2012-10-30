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
package org.jhotdraw.figures

import java.awt.Point
import java.awt.Rectangle
import org.jhotdraw.framework.FigureAttributeConstant
import org.jhotdraw.framework.Handle
import org.jhotdraw.standard.CompositeFigure
import org.jhotdraw.standard.RelativeLocator
import org.jhotdraw.framework.Figure

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

  /**
   * Sets the attribute of all the contained figures.
   * @deprecated see setAttribute(FigureAttributeConstant,Object)
   */
  override def setAttribute(name: String, value: Any) {
    super.setAttribute(name, value)
    figures foreach {_.setAttribute(name, value)}
  }

  /**
   * Sets the attribute of the GroupFigure as well as all contained Figures.
   */
  override def setAttribute(fac: FigureAttributeConstant, obj: Any) {
    super.setAttribute(fac, obj)
    figures foreach {_.setAttribute(fac, obj)}
  }
}

