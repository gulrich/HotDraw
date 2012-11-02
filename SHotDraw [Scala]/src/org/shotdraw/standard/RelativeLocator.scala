/*
 * @(#)RelativeLocator.java
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
 * A locator that specfies a point that is relative to the bounds
 * of a figure.
 *
 * @see Locator
 *
 * @version <$CURRENT_VERSION$>
 */
object RelativeLocator {
  def east: Locator = new RelativeLocator(1.0, 0.5)

  /**
   * North.
   */
  def north: Locator = new RelativeLocator(0.5, 0.0)

  /**
   * West.
   */
  def west: Locator = new RelativeLocator(0.0, 0.5)

  /**
   * North east.
   */
  def northEast: Locator =  new RelativeLocator(1.0, 0.0)

  /**
   * North west.
   */
  def northWest: Locator = new RelativeLocator(0.0, 0.0)

  /**
   * South.
   */
  def south: Locator = new RelativeLocator(0.5, 1.0)

  /**
   * South east.
   */
  def southEast: Locator = new RelativeLocator(1.0, 1.0)

  /**
   * South west.
   */
  def southWest: Locator = new RelativeLocator(0.0, 1.0)

  /**
   * Center.
   */
  def center: Locator = new RelativeLocator(0.5, 0.5)

}

class RelativeLocator(private[standard] var fRelativeX: Double, private[standard] var fRelativeY: Double) extends AbstractLocator {

  def this() {
    this(0.0, 0.0)
  }
  
  override def equals(o: Any): Boolean = o match {
    case rl: RelativeLocator => (rl.fRelativeX) == fRelativeX && (rl.fRelativeY == fRelativeY)
    case _ => false
  }

  def locate(owner: Figure): Point = {
    val r: Rectangle = owner.displayBox
    new Point(r.x + (r.width * fRelativeX).asInstanceOf[Int], r.y + (r.height * fRelativeY).asInstanceOf[Int])
  }

  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeDouble(fRelativeX)
    dw.writeDouble(fRelativeY)
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    fRelativeX = dr.readDouble
    fRelativeY = dr.readDouble
  }
}

