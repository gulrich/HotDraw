/*
 * @(#)AbstractLineDecoration.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.io._
import java.awt._
import org.shotdraw.framework._
import org.shotdraw.util._

/**
 * An standard implementation of a line decoration.
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
object AbstractLineDecoration {
  private[figures] final val serialVersionUID: Long = 1577970039258356627L
  private val FILL_COLOR: String = "fill_color"
  private val FRAME_COLOR: String = "frame_color"
}

abstract class AbstractLineDecoration extends LineDecoration {
  import AbstractLineDecoration._
  /**
   * Draws the arrow tip in the direction specified by the given two
   * points.. (template method)
   */
  def draw(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int) {
    val p: Polygon = outline(x1, y1, x2, y2)
    myBounds = p.getBounds
    if (getFillColor == null) {
      g.fillPolygon(p.xpoints, p.ypoints, p.npoints)
    }
    else {
      val drawColor: Color = g.getColor
      g.setColor(getFillColor)
      g.fillPolygon(p.xpoints, p.ypoints, p.npoints)
      g.setColor(drawColor)
    }
    if (getBorderColor ne getFillColor) {
      val drawColor: Color = g.getColor
      g.setColor(getBorderColor)
      g.drawPolygon(p.xpoints, p.ypoints, p.npoints)
      g.setColor(drawColor)
    }
  }

  /**
   * The LineDecoration has only a displayBox after it has been drawn
   * at least once. If it has not yet been drawn then a rectangle of size 0
   * is returned.
   * @return the display box of a LineDecoration.
   */
  def displayBox: Rectangle = {
    if (myBounds != null) {
      myBounds
    } else {
      new Rectangle(0, 0)
    }
  }

  /**
   * Hook method to calculates the outline of an arrow tip.
   */
  def outline(x1: Int, y1: Int, x2: Int, y2: Int): Polygon

  /**
   * Stores the arrow tip to a StorableOutput.
   */
  def write(dw: StorableOutput) {
    if (getFillColor != null) {
      dw.writeColor(getFillColor)
    } else {
      dw.writeString("no" + FILL_COLOR)
    }
    if (getBorderColor != null) {
      dw.writeColor(getBorderColor)
    } else {
      dw.writeString("no" + FRAME_COLOR)
    }
  }

  /**
   * Reads the arrow tip from a StorableInput.
   */
  def read(dr: StorableInput) {
    val fillColorId: String = dr.readString
    if (fillColorId == FRAME_COLOR) {
      setFillColor(dr.readColor)
    }
    val borderColorId: String = dr.readString
    if ((borderColorId == "BorderColor") || (borderColorId == FRAME_COLOR)) {
      setBorderColor(dr.readColor)
    }
  }

  /**
   * Sets color with which arrow is filled
   */
  def setFillColor(fillColor: Color) {
    fFillColor = fillColor
  }

  /**
   * Returns color with which arrow is filled
   */
  def getFillColor: Color = fFillColor

  /**
   * Sets color of arrow's border
   */
  def setBorderColor(borderColor: Color) {
    fBorderColor = borderColor
  }

  /**
   * Returns color of arrow's border
   */
  def getBorderColor: Color = fBorderColor

  private var fFillColor: Color = null
  private var fBorderColor: Color = null
  @transient
  private var myBounds: Rectangle = null
}

