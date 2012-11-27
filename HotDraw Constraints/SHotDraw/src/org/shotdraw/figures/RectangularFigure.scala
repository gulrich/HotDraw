package org.shotdraw.figures
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import org.shotdraw.framework.CRectangle
import org.shotdraw.standard.AbstractFigure
import org.shotdraw.standard.BoxHandleKit
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput
import ch.epfl.lamp.cassowary.Constraint
import ch.epfl.lamp.cassowary.SimplexSolver
import ch.epfl.lamp.cassowary.CVar
import ch.epfl.lamp.cassowary.Strength

abstract class RectangularFigure(origin: Point, corner: Point) extends AbstractFigure {
  private var solver = new SimplexSolver
  private var db: CRectangle = new CRectangle(origin, corner, solver)
  private val h = List(
      North(solver),
      NorthEast(solver),
      East(solver),
      SouthEast(solver),
      South(solver),
      SouthWest(solver),
      West(solver),
      NorthWest(solver))

  solver.addStay(db.cx, Strength.Required)
  solver.addStay(db.cy, Strength.Required)
  
  ensure(h(0).cx :== (h(5).cx+h(1).cx)/2)
  ensure(h(4).cx :== h(0).cx)
  
  ensure(h(2).cy :== (h(0).cy+h(3).cy)/2)
  ensure(h(6).cy :== h(2).cy)
  
  ensure(h(1).cx :== db.cx+db.cwidth)
  ensure(h(2).cx :== h(1).cx)
  ensure(h(3).cx :== h(1).cx)

  ensure(h(5).cx :== db.cx)
  ensure(h(6).cx :== h(5).cx)
  ensure(h(7).cx :== h(5).cx)
  
  ensure(h(0).cy :== db.cy)
  ensure(h(1).cy :== h(0).cy)
  ensure(h(7).cy :== h(0).cy)
  
  ensure(h(3).cy :== db.cy+db.cheight)
  ensure(h(4).cy :== h(3).cy)
  ensure(h(5).cy :== h(3).cy)
     
  def ensure(c: Constraint): Unit = solver.addConstraint(c)
  
  override def displayBox(origin: Point, corner: Point) {
    solver = new SimplexSolver
    solver.addEditVar(db.cx).beginEdit.addEditVar(db.cy).addEditVar(db.cwidth).addEditVar(db.cheight).beginEdit
    solver.suggestValue(db.cx, origin.x).suggestValue(db.cy, origin.y).suggestValue(db.cwidth, corner.x-origin.x).suggestValue(db.cheight, corner.y-origin.y).resolve
    solver.endEdit
    println("DB:"+db.cx + " " + db.cy + " " + db.cwidth + " " + db.cheight)
    h foreach {f => println(f.id + "= ("+f.cx + " " +f.cy+")")}
    willChange()
//    
//    db.cx.value = origin.x
//    db.cy.value = origin.y
//    db.cwidth.value = corner.x-origin.x
//    db.cheight.value = corner.y-origin.y
//    
//    h(0).cx.value = db.x+db.width/2
//    h(0).cy.value = db.y   
    changed()
  }
  
  def newFigure(origin: Point, corner: Point): RectangularFigure
  
  def basicDisplayBox(origin: Point, corner: Point) {
    db = new CRectangle(origin, corner, solver)
  }

  override def handles = BoxHandleKit.addHandles(this,List())
  
  def getHandles: Seq[DraggableBox] = h
  
  def displayBox: Rectangle = {
    new Rectangle(db.x, db.y, db.width, db.height)
  }

  protected def basicMoveBy(x: Int, y: Int) {
    db = new CRectangle(db.x+x, db.y+y, db.width, db.height, solver)
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
    db = new CRectangle(dr.readInt, dr.readInt, dr.readInt, dr.readInt, solver)
  } 
}

sealed trait DragBox

object North extends DragBox {
  def apply(solver: SimplexSolver) = new DraggableBox(0,solver)
}
object NorthEast extends DragBox {
  def apply(solver: SimplexSolver) = new DraggableBox(1,solver)
}
object East extends DragBox {
  def apply(solver: SimplexSolver) = new DraggableBox(2,solver)
}
object SouthEast extends DragBox {
  def apply(solver: SimplexSolver) = new DraggableBox(3,solver)
}
object South extends DragBox {
  def apply(solver: SimplexSolver) = new DraggableBox(4,solver)
}
object SouthWest extends DragBox {
  def apply(solver: SimplexSolver) = new DraggableBox(5,solver)
}
object West extends DragBox {
  def apply(solver: SimplexSolver) = new DraggableBox(6,solver)
}
object NorthWest extends DragBox {
  def apply(solver: SimplexSolver) = new DraggableBox(7,solver)
}

class DraggableBox(val id: Int, solver: SimplexSolver) extends Serializable {
  private val width, height = 8
  val cx = CVar(0, solver)
  val cy = CVar(0, solver)
  
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