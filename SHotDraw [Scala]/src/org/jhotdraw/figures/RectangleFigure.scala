/*
 * @(#)RectangleFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.figures

import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.io.IOException
import org.jhotdraw.framework.Handle
import org.jhotdraw.framework.HandleEnumeration
import org.jhotdraw.standard.BoxHandleKit
import org.jhotdraw.standard.HandleEnumerator
import org.jhotdraw.util.StorableInput
import org.jhotdraw.util.StorableOutput

/**
 * A rectangle figure.
 *
 * @version <$CURRENT_VERSION$>
 */
object RectangleFigure {
  private final val serialVersionUID: Long = 184722075881789163L
}

class RectangleFigure(origin: Point, corner: Point) extends AttributeFigure {
  private var fDisplayBox: Rectangle = null
  basicDisplayBox(origin, corner)
  def this() {
    this(new Point(0, 0), new Point(0, 0))
  }

  def basicDisplayBox(origin: Point, corner: Point) {
    fDisplayBox = new Rectangle(origin)
    fDisplayBox.add(corner)
  }

  def handles: HandleEnumeration = new HandleEnumerator(BoxHandleKit.addHandles(this, List()))

  def displayBox: Rectangle = new Rectangle(fDisplayBox.x, fDisplayBox.y, fDisplayBox.width, fDisplayBox.height)

  protected def basicMoveBy(x: Int, y: Int) {
    fDisplayBox.translate(x, y)
  }

  override def drawBackground(g: Graphics) {
    val r: Rectangle = displayBox
    g.fillRect(r.x, r.y, r.width, r.height)
  }

  override def drawFrame(g: Graphics) {
    val r: Rectangle = displayBox
    g.drawRect(r.x, r.y, r.width - 1, r.height - 1)
  }

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

