/*
 * @(#)ArrowTip.java
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
import org.shotdraw.util._

/**
 * An arrow tip line decoration.
 *
 * @see PolyLineFigure
 *
 * @version <$CURRENT_VERSION$>
 */
class ArrowTip(angle: Double, outerRadius: Double, innerRadius: Double) extends AbstractLineDecoration {
  private var fAngle: Double = .0
  private var fOuterRadius: Double = .0
  private var fInnerRadius: Double = .0
  setAngle(angle)
  setOuterRadius(outerRadius)
  setInnerRadius(innerRadius)
  
  def this() {
    this(0.40, 8, 8)
  }

  /**
   * Calculates the outline of an arrow tip.
   */
  def outline(x1: Int, y1: Int, x2: Int, y2: Int): Polygon = {
    val dir: Double = math.Pi / 2 - math.atan2(x2 - x1, y2 - y1)
    outline(x1, y1, dir)
  }

  private def outline(x: Int, y: Int, direction: Double): Polygon = {
    val shape: Polygon = new Polygon
    shape.addPoint(x, y)
    addPointRelative(shape, x, y, getOuterRadius, direction - getAngle)
    addPointRelative(shape, x, y, getInnerRadius, direction)
    addPointRelative(shape, x, y, getOuterRadius, direction + getAngle)
    shape.addPoint(x, y)
    shape
  }

  private def addPointRelative(shape: Polygon, x: Int, y: Int, radius: Double, angle: Double) {
    shape.addPoint(x + (radius * math.cos(angle)).asInstanceOf[Int], y + (radius * math.sin(angle)).asInstanceOf[Int])
  }

  /**
   * Stores the arrow tip to a StorableOutput.
   */
  override def write(dw: StorableOutput) {
    dw.writeDouble(getAngle)
    dw.writeDouble(getOuterRadius)
    dw.writeDouble(getInnerRadius)
    super.write(dw)
  }

  /**
   * Reads the arrow tip from a StorableInput.
   */
  override def read(dr: StorableInput) {
    setAngle(dr.readDouble)
    setOuterRadius(dr.readDouble)
    setInnerRadius(dr.readDouble)
    super.read(dr)
  }

  /**
   * Sets point angle of arrow. A smaller angle leads to a pointier arrow.
   * The angle is measured between the arrow line and one of the points
   * at the side of the arrow. Thus, the total angle at the arrow tip
   * is the double of the angle specified.
   */
  protected def setAngle(newAngle: Double) {
    fAngle = newAngle
  }

  /**
   * Returns point angle of arrow. A smaller angle leads to a pointier arrow.
   * The angle is measured between the arrow line and one of the points
   * at the side of the arrow. Thus, the total angle at the arrow tip
   * is the double of the angle specified.
   */
  protected def getAngle: Double = fAngle

  /**
   * Sets the inner radius
   */
  protected def setInnerRadius(newInnerRadius: Double) {
    fInnerRadius = newInnerRadius
  }

  /**
   * Returns the inner radius
   */
  protected def getInnerRadius: Double = fInnerRadius

  /**
   * Sets the outer radius
   */
  protected def setOuterRadius(newOuterRadius: Double) {
    fOuterRadius = newOuterRadius
  }

    
  /**
   * Returns the outer radius
   */
  protected def getOuterRadius: Double = fOuterRadius
}

