/*
 * @(#)EllipseFigure.java
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
import org.shotdraw.util._
import org.shotdraw.framework._
import org.shotdraw.standard._
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Point
import java.awt.Graphics

/**
 * An ellipse figure.
 *
 * @version <$CURRENT_VERSION$>
 */
class EllipseFigure(origin: Point, corner: Point) extends AttributeFigure {
  private var fDisplayBox: Rectangle = null
  basicDisplayBox(origin, corner)
  
  def this() {
    this(new Point(0, 0), new Point(0, 0))
  }

  def handles: Seq[Handle] = BoxHandleKit.addHandles(this, List())  

  def basicDisplayBox(origin: Point, corner: Point) {
    fDisplayBox = new Rectangle(origin)
    fDisplayBox.add(corner)
  }

  def displayBox: Rectangle = new Rectangle(fDisplayBox.x, fDisplayBox.y, fDisplayBox.width, fDisplayBox.height)

  protected def basicMoveBy(x: Int, y: Int) {
    fDisplayBox.translate(x, y)
  }

  override def drawBackground(g: Graphics) {
    val r: Rectangle = displayBox
    g.fillOval(r.x, r.y, r.width, r.height)
  }

  override def drawFrame(g: Graphics) {
    val r: Rectangle = displayBox
    g.drawOval(r.x, r.y, r.width - 1, r.height - 1)
  }

  override def connectionInsets: Insets = {
    val r: Rectangle = fDisplayBox
    val cx: Int = r.width / 2
    val cy: Int = r.height / 2
    new Insets(cy, cx, cy, cx)
  }

  override def connectorAt(x: Int, y: Int): Connector = new ChopEllipseConnector(this)

  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeInt(fDisplayBox.x)
    dw.writeInt(fDisplayBox.y)
    dw.writeInt(fDisplayBox.width)
    dw.writeInt(fDisplayBox.height)
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    fDisplayBox = new Rectangle(dr.readInt, dr.readInt, dr.readInt, dr.readInt)
  }
}

