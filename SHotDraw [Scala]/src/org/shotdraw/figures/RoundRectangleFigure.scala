/*
 * @(#)RoundRectangleFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
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

/**
 * A round rectangle figure.
 *
 * @see RadiusHandle
 *
 * @version <$CURRENT_VERSION$>
 */
object RoundRectangleFigure {
  private final val DEFAULT_ARC: Int = 8
}

class RoundRectangleFigure(origin: Point, corner: Point) extends AbstractFigure {
  import RoundRectangleFigure._
  
  private var fDisplayBox: Rectangle = null
  private var fArcWidth: Int = DEFAULT_ARC
  private var fArcHeight: Int = DEFAULT_ARC
  basicDisplayBox(origin, corner)
  
  def this() {
    this(new Point(0, 0), new Point(0, 0))
  }

  def basicDisplayBox(origin: Point, corner: Point) {
    fDisplayBox = new Rectangle(origin)
    fDisplayBox.add(corner)
  }

  /**
   * Sets the arc's witdh and height.
   */
  def setArc(width: Int, height: Int) {
    willChange
    fArcWidth = width
    fArcHeight = height
    changed
  }

  /**
   * Gets the arc's width and height.
   */
  def getArc: Point = new Point(fArcWidth, fArcHeight)

  def handles: Seq[Handle] = BoxHandleKit.addHandles(this, List()) ::: List(new RadiusHandle(this))

  def displayBox: Rectangle = new Rectangle(fDisplayBox.x, fDisplayBox.y, fDisplayBox.width, fDisplayBox.height)

  protected def basicMoveBy(x: Int, y: Int) {
    fDisplayBox.translate(x, y)
  }

  override def drawBackground(g: Graphics) {
    val r: Rectangle = displayBox
    g.fillRoundRect(r.x, r.y, r.width, r.height, fArcWidth, fArcHeight)
  }

  override def drawFrame(g: Graphics) {
    val r: Rectangle = displayBox
    g.drawRoundRect(r.x, r.y, r.width - 1, r.height - 1, fArcWidth, fArcHeight)
  }

  override def connectionInsets: Insets = new Insets(fArcHeight / 2, fArcWidth / 2, fArcHeight / 2, fArcWidth / 2)

  override def connectorAt(x: Int, y: Int): Connector = new ShortestDistanceConnector(this)

  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeInt(fDisplayBox.x)
    dw.writeInt(fDisplayBox.y)
    dw.writeInt(fDisplayBox.width)
    dw.writeInt(fDisplayBox.height)
    dw.writeInt(fArcWidth)
    dw.writeInt(fArcHeight)
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    fDisplayBox = new Rectangle(dr.readInt, dr.readInt, dr.readInt, dr.readInt)
    fArcWidth = dr.readInt
    fArcHeight = dr.readInt
  }

}

