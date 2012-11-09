/*
 * @(#)DiamondFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle
import java.awt.geom.GeneralPath
import java.io.IOException
import org.shotdraw.framework.Connector
import org.shotdraw.framework.Handle
import org.shotdraw.standard.BoxHandleKit
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput
import org.shotdraw.standard.AbstractConnector
import org.shotdraw.standard.AbstractFigure

/**
 * An diamond figure.
 *
 * @version <$CURRENT_VERSION$>
 */
class DiamondFigure(origin: Point, corner: Point) extends AbstractFigure {
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
    val r = displayBox
    val g2d = g.asInstanceOf[Graphics2D]
    val path = new GeneralPath
    path.moveTo(r.x + r.width / 2.0, r.y)
    path.lineTo(r.x, r.y + r.height / 2.0)
    path.lineTo(r.x + r.width / 2.0, r.y + r.height)
    path.lineTo(r.x + r.width, r.y + r.height / 2.0)
    path.closePath
    g2d.fill(path)
  }

  override def drawFrame(g: Graphics) {
    val r = displayBox
    val g2d = g.asInstanceOf[Graphics2D]
    val path = new GeneralPath
    path.moveTo(r.x + r.width / 2.0, r.y)
    path.lineTo(r.x, r.y + r.height / 2.0)
    path.lineTo(r.x + r.width / 2.0, r.y + r.height)
    path.lineTo(r.x + r.width, r.y + r.height / 2.0)
    path.closePath
    g2d.draw(path)
  }

  override def connectionInsets: Insets = {
    val r = fDisplayBox
    val cx = r.width / 2
    val cy = r.height / 2
    new Insets(cy, cx, cy, cx)
  }

  override def connectorAt(x: Int, y: Int): Connector =  new ShortestDistanceConnector(this)

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

