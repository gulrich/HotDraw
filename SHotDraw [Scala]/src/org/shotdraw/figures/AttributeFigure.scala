/*
 * @(#)AttributeFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import org.shotdraw.util._
import org.shotdraw.framework._
import org.shotdraw.standard._
import java.awt._
import java.io._
import java.lang.Object

/**
 * A figure that can keep track of an open ended set of attributes.
 * The attributes are stored in a dictionary implemented by
 * FigureAttributes.
 *
 * @see Figure
 * @see Handle
 * @see FigureAttributes
 *
 * @version <$CURRENT_VERSION$>
 */
object AttributeFigure {
  private final val serialVersionUID: Long = -10857585979273442L
}

abstract class AttributeFigure extends AbstractFigure {
  
  /**
   * Draws the figure in the given graphics. Draw is a template
   * method calling drawBackground followed by drawFrame.
   */
  def draw(g: Graphics) {
    val fill: Color = getFillColor
    if (!ColorMap.isTransparent(fill)) {
      g.setColor(fill)
      drawBackground(g)
    }
    val frame: Color = getFrameColor
    if (!ColorMap.isTransparent(frame)) {
      g.setColor(frame)
      drawFrame(g)
    }
  }

  /**
   * Draws the background of the figure.
   * @see #draw
   */
  protected def drawBackground(g: Graphics) {}

  /**
   * Draws the frame of the figure.
   * @see #draw
   */
  protected def drawFrame(g: Graphics) {}
}

