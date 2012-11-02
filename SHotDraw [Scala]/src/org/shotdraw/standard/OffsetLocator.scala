/*
 * @(#)OffsetLocator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt._
import java.io.IOException
import org.shotdraw.framework._
import org.shotdraw.util._

/**
 * A locator to offset another Locator.
 * @see Locator
 *
 * @version <$CURRENT_VERSION$>
 */
object OffsetLocator {
  private final val serialVersionUID: Long = 2679950024611847621L
}

class OffsetLocator(var fBase: Locator, var fOffsetX: Int, var fOffsetY: Int) extends AbstractLocator {
  def this() {
    this(null, 0, 0)
  }

  def this(base: Locator) {
    this(base, 0, 0)
  }

  def locate(owner: Figure): Point = {
    val p: Point = fBase.locate(owner)
    p.x += fOffsetX
    p.y += fOffsetY
    return p
  }

  def moveBy(dx: Int, dy: Int) {
    fOffsetX += dx
    fOffsetY += dy
  }

  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeInt(fOffsetX)
    dw.writeInt(fOffsetY)
    dw.writeStorable(fBase)
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    fOffsetX = dr.readInt
    fOffsetY = dr.readInt
    fBase = dr.readStorable.asInstanceOf[Locator]
  }
}


