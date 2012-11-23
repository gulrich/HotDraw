package org.shotdraw.framework
import ch.epfl.lamp.cassowary.CVar
import java.awt.Rectangle
import java.awt.Point
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput

class CRectangle(x0: Int, y0: Int, w0: Int, h0: Int) extends Serializable {
  val cx = CVar(x0)
  val cy = CVar(y0)
  val cwidth = CVar(w0)
  val cheight = CVar(h0)
  
  def this(origin: Point, corner: Point) = this(origin.x, origin.y, corner.x-origin.x, corner.y-origin.y)
  
  def contains(x: Int, y: Int): Boolean = (x >= this.x && x <= this.x+width) && (y >= this.y && y <= this.y+height)   
  
  def x = cx.value.toInt
  def y = cy.value.toInt
  def width = cwidth.value.toInt
  def height = cheight.value.toInt
}
