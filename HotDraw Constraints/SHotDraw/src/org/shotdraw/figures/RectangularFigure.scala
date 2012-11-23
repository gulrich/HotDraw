package org.shotdraw.figures
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import org.shotdraw.framework.CRectangle
import org.shotdraw.framework.Locator
import org.shotdraw.standard.AbstractFigure
import org.shotdraw.standard.LocatorHandle
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput
import ch.epfl.lamp.cassowary.CVar
import ch.epfl.lamp.cassowary.Constraint
import ch.epfl.lamp.cassowary.SimplexSolver
import org.shotdraw.standard.BoxHandleKit

abstract class RectangularFigure(origin: Point, corner: Point) extends AbstractFigure {
  private var db: CRectangle = new CRectangle(origin, corner)
  private val h = List(
      DraggableBox.north,
      DraggableBox.northEast,
      DraggableBox.east,
      DraggableBox.southEast,
      DraggableBox.south,
      DraggableBox.southWest,
      DraggableBox.west,
      DraggableBox.northWest)
  private val solver = new SimplexSolver
    
  
  
//  ensure(h(0).cx :== (db.cx+db.cx+db.cwidth)/2)
//  ensure(h(0).cy :== db.cy)
//  ensure(h(1).cx :== db.cx+db.cwidth)
//  ensure(h(1).cy :== db.cy)
//  ensure(h(2).cx :== db.cx+db.cwidth)
//  ensure(h(2).cy :== (db.cy+db.cheight)/2)
//  ensure(h(3).cx :== db.cx+db.cwidth)
//  ensure(h(3).cy :== db.cy+db.cheight)
//  ensure(h(4).cx :== (db.cx+db.cx+db.cwidth)/2)
//  ensure(h(4).cy :== db.cy+db.cheight)
//  ensure(h(5).cx :== db.cx)
//  ensure(h(5).cy :== db.cy+db.cheight)
//  ensure(h(6).cx :== db.cx)
//  ensure(h(6).cy :== (db.cy+db.cheight)/2)
//  ensure(h(7).cx :== db.cx)
//  ensure(h(7).cy :== db.cy)
  
  def ensure(c: Constraint): Unit = solver.addConstraint(c)
  
  override def displayBox(origin: Point, corner: Point) {
    
//    solver.addEditVar(db.cx).beginEdit.addEditVar(db.cy).addEditVar(db.cwidth).addEditVar(db.cheight).beginEdit
//    solver.suggestValue(db.cx, origin.x).suggestValue(db.cy, origin.y).suggestValue(db.cwidth, corner.x-origin.x).suggestValue(db.cheight, corner.y-origin.y).resolve
//    solver.endEdit
//    println("DB:"+db.cx + " " + db.cy + " " + db.cwidth + " " + db.cheight)
//    h foreach {f => println(f.id + "= ("+f.cx + " " +f.cy+")")}
    willChange()
    
    db.cx.value = origin.x
    db.cy.value = origin.y
    db.cwidth.value = corner.x-origin.x
    db.cheight.value = corner.y-origin.y
    
    h(0).cx.value = db.x+db.width/2
    h(0).cy.value = db.y
    
    changed()
  }
  
  def basicDisplayBox(origin: Point, corner: Point) {
    db = new CRectangle(origin, corner)
  }

  override def handles = BoxHandleKit.addHandles(this,List())
  
  def getHandles: Seq[DraggableBox] = h
  
  def displayBox: Rectangle = {
    new Rectangle(db.x, db.y, db.width, db.height)
  }

  protected def basicMoveBy(x: Int, y: Int) {
    db = new CRectangle(db.x+x, db.y+y, db.width, db.height)
  }
  
  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeInt(db.x)
    dw.writeInt(db.y)
    dw.writeInt(db.width)
    dw.writeInt(db.height)
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    db = new CRectangle(dr.readInt, dr.readInt, dr.readInt, dr.readInt)
  } 
}

object DraggableBox {
  def north = new DraggableBox(0)
  def northEast = new DraggableBox(1)
  def east = new DraggableBox(2)
  def southEast = new DraggableBox(3)
  def south = new DraggableBox(4)
  def southWest = new DraggableBox(5)
  def west = new DraggableBox(6)
  def northWest = new DraggableBox(7)
}

class DraggableBox(val id: Int) extends Serializable {
  private val width, height = 8
  val cx = CVar(0)
  val cy = CVar(0)
  
  def x = cx.value.toInt
  def y = cy.value.toInt

  def draw(g: Graphics) {
    g.setColor(Color.BLACK)
    g.drawRect(x - (width / 2), y - (height / 2), width, height)
    g.setColor(Color.WHITE)
    g.fillRect(x - (width / 2) + 1, y - (height / 2) + 1, width - 1, height - 1)
  }

  def contains(x: Int, y: Int): Boolean =
    (x >= this.x - width / 2) && (x <= this.x + width / 2) && (y >= this.y - height / 2) && (y <= this.y + height / 2)

  override def toString = "("+x+", "+y+")"
}