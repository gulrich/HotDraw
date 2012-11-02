/*
 * @(#)LineFigure.java
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

/**
 * A line figure.
 *
 * @version <$CURRENT_VERSION$>
 */
object LineFigure {
  private final val serialVersionUID: Long = 511503575249212371L
}

class LineFigure extends PolyLineFigure {
  addPoint(0, 0)
  addPoint(0, 0)  

  /**
   * Gets a copy of the start point.
   */
  def startPoint: Point = pointAt(0)

  /**
   * Gets a copy of the end point.
   */
  def endPoint: Point = pointAt(1)

  /**
   * Sets the start point.
   */
  def startPoint(x: Int, y: Int) {
    setPointAt(new Point(x, y), 0)
  }

  /**
   * Sets the end point.
   */
  def endPoint(x: Int, y: Int) {
    setPointAt(new Point(x, y), 1)
  }

  /**
   * Sets the start and end point.
   */
  def setPoints(start: Point, end: Point) {
    setPointAt(start, 0)
    setPointAt(end, 1)
  }

  override def basicDisplayBox(origin: Point, corner: Point) {
    setPoints(origin, corner)
  }
}

