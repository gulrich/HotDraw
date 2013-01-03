/*
 * @(#)RoundRectangleFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	��� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.io.IOException
import org.shotdraw.framework._
import org.shotdraw.standard._
import org.shotdraw.util._
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Point
import java.awt.Graphics
import RoundRectangleFigure._
import ch.epfl.lamp.cassowary.SimplexSolver

/**
 * A round rectangle figure.
 *
 * @see RadiusHandle
 *
 * @version <$CURRENT_VERSION$>
 */
object RoundRectangleFigure {
  private final val DEFAULT_ARC = 8
}

class RoundRectangleFigure(origin: Point, corner: Point, solver: SimplexSolver) extends RectangularFigure(origin, corner, solver) {
  private var fArcWidth = DEFAULT_ARC
  private var fArcHeight = DEFAULT_ARC
  
  def this(solver: SimplexSolver) {
    this(new Point(0, 0), new Point(0, 0), solver)
  }
  
  /**
   * Sets the arc's witdh and height.
   */
  def setArc(width: Int, height: Int) {
    willChange()
    fArcWidth = width
    fArcHeight = height
    changed()
  }

  /**
   * Gets the arc's width and height.
   */
  def getArc: Point = new Point(fArcWidth, fArcHeight)

  override def drawBackground(g: Graphics) {
    val r = displayBox
    g.fillRoundRect(r.x, r.y, r.width, r.height, fArcWidth, fArcHeight)
  }

  override def drawFrame(g: Graphics) {
    val r = displayBox
    g.drawRoundRect(r.x, r.y, r.width - 1, r.height - 1, fArcWidth, fArcHeight)
  }

  override def connectionInsets: Insets = new Insets(fArcHeight / 2, fArcWidth / 2, fArcHeight / 2, fArcWidth / 2)

  override def connectorAt(x: Int, y: Int): Connector = new ShortestDistanceConnector(this)

  override def handles = new RadiusHandle(this) :: super.handles
  
  override def newFigure(origin: Point, corner: Point, solver: SimplexSolver) = new RoundRectangleFigure(origin, corner, solver)
  
  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeInt(fArcWidth)
    dw.writeInt(fArcHeight)
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    fArcWidth = dr.readInt
    fArcHeight = dr.readInt
  }

}

