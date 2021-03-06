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
import org.shotdraw.standard.AbstractHandle
import org.shotdraw.framework.Cursor
import org.shotdraw.standard.AWTCursor
import org.shotdraw.framework.DrawingView
import org.shotdraw.standard.ResizeHandle
import org.shotdraw.framework.Figure
import org.shotdraw.standard.RelativeLocator
import java.io.PrintWriter
import java.io.FileOutputStream
import java.io.File
import ch.epfl.lamp.cassowary.Constraint

object Printer {
  def println(s: String) {
    val output:PrintWriter = new PrintWriter(new FileOutputStream(new File("output"), true))
    output.println(s)
    output.close()
  }
}

abstract class RectangularFigure(origin: Point, corner: Point, solver: SimplexSolver) extends AbstractFigure {
  var db: CRectangle = new CRectangle(origin, corner, solver)

  private val h = List(
      North(solver, this),
      NorthEast(solver, this),
      East(solver, this),
      SouthEast(solver, this),
      South(solver, this),
      SouthWest(solver, this),
      West(solver, this),
      NorthWest(solver, this))


  
  db.cx.value=origin.x
  db.cy.value=origin.y
  db.cx.stay
  db.cy.stay
  
  db.cwidth.value = corner.x-origin.x
  db.cheight.value = corner.y-origin.y
  db.cwidth.stay
  db.cheight.stay
  
  
  ensure(h(0).cx :== h(7).cx+h(1).cx-h(0).cx)
  ensure(h(4).cx :== h(0).cx)
  
  ensure(h(2).cy :== h(1).cy+h(3).cy-h(2).cy)
  ensure(h(6).cy :== h(2).cy)

  ensure(h(1).cx :== db.cx+db.cwidth)
  ensure(h(1).cx :== h(2).cx)
  
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
     
  def ensure(c: Constraint) {
    solver.addConstraint(c)
  }
  
  def disable(c: Constraint) {
    solver.removeConstraint(c)
  }
  
  def printVar {
    Printer.println("DB:"+db.cx + " " + db.cy + " " + db.cwidth + " " + db.cheight)
    h foreach {f => Printer.println(h.indexOf(f) + "= ("+f.cx + " " +f.cy+")")}
  }
  
  override def displayBox(origin: Point, corner: Point) {
    willChange()
//    solver.addEditVar(db.cx).beginEdit.addEditVar(db.cy).addEditVar(db.cwidth).addEditVar(db.cheight).beginEdit
//    solver.suggestValue(db.cx, origin.x).suggestValue(db.cy, origin.y).suggestValue(db.cwidth, corner.x-origin.x).suggestValue(db.cheight, corner.y-origin.y).resolve
//    solver.endEdit
    changed()
  }
  
  def southEastMove() {
    reset()
    h(7).cx.stay
    h(6).cx.stay
    h(5).cx.stay
    
    h(7).cy.stay
    h(0).cy.stay
    h(1).cy.stay
  }
  
  def southWestMove() {
    reset()
    h(1).cx.stay
    h(2).cx.stay
    h(3).cx.stay
    
    h(7).cy.stay
    h(0).cy.stay
    h(1).cy.stay
  }
  
  def northWestMove() {
    reset()
    h(1).cx.stay
    h(2).cx.stay
    h(3).cx.stay
    
    h(3).cy.stay
    h(4).cy.stay
    h(5).cy.stay
  }
  
  def northEastMove() {
    reset()
    h(7).cx.stay
    h(6).cx.stay
    h(5).cx.stay
    
    h(3).cy.stay
    h(4).cy.stay
    h(5).cy.stay
  }
  
  def northMove() {
    reset()
    verticalMove()
    
    h(3).cy.stay
    h(4).cy.stay
    h(5).cy.stay
  }
  
  def southMove() {
    reset()
    verticalMove()
    
    h(3).cy.stay
    h(4).cy.stay
    h(5).cy.stay
  }
  
  def eastMove() {
    reset()
    horizontalMove()
    
    h(1).cx.stay
    h(2).cx.stay
    h(3).cx.stay
  }
  
  def westMove() {
    reset()
    horizontalMove()
    
    h(5).cx.stay
    h(6).cx.stay
    h(7).cx.stay
  }
    
  private def verticalMove() {
    h(0).cx.stay
    h(1).cx.stay
    h(2).cx.stay
    h(3).cx.stay
    h(4).cx.stay
    h(5).cx.stay
    h(6).cx.stay
    h(7).cx.stay
  }
  
  private def horizontalMove() {
    h(0).cy.stay
    h(1).cy.stay
    h(2).cy.stay
    h(3).cy.stay
    h(4).cy.stay
    h(5).cy.stay
    h(6).cy.stay
    h(7).cy.stay
  }
  
  private def reset() {
    db.cwidth.disable
    db.cheight.disable
    db.cx.disable
    db.cy.disable
    
    h(0).cx.disable
    h(0).cy.disable
    h(1).cx.disable
    h(1).cy.disable
    h(2).cx.disable
    h(2).cy.disable
    h(3).cx.disable
    h(3).cy.disable
    h(4).cx.disable
    h(4).cy.disable
    h(5).cx.disable
    h(5).cy.disable
    h(6).cx.disable
    h(6).cy.disable
    h(7).cx.disable
    h(7).cy.disable
  }
  
  def newFigure(origin: Point, corner: Point, solver: SimplexSolver): RectangularFigure
  
  def basicDisplayBox(origin: Point, corner: Point) {
    db = new CRectangle(origin, corner, solver)
  }

  override def handles = h
  
  def displayBox: Rectangle = {
    new Rectangle(db.x, db.y, db.width, db.height)
  }

  protected def basicMoveBy(x: Int, y: Int) {
    db.cx.disable
    db.cy.disable
    solver.addEditVar(db.cx).addEditVar(db.cy).beginEdit
    solver.suggestValue(db.cx, db.x+x).suggestValue(db.cy, db.y+y).resolve
    solver.endEdit
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
  def apply(solver: SimplexSolver, owner: RectangularFigure) = new DraggableBox(solver, owner, DraggableBox.North)
}
object NorthEast extends DragBox {
  def apply(solver: SimplexSolver, owner: RectangularFigure) = new DraggableBox(solver, owner, DraggableBox.NorthEast)
}
object East extends DragBox {
  def apply(solver: SimplexSolver, owner: RectangularFigure) = new DraggableBox(solver, owner, DraggableBox.East)
}
object SouthEast extends DragBox {
  def apply(solver: SimplexSolver, owner: RectangularFigure) = new DraggableBox(solver, owner, DraggableBox.SouthEast)
}
object South extends DragBox {
  def apply(solver: SimplexSolver, owner: RectangularFigure) = new DraggableBox(solver, owner, DraggableBox.South)
}
object SouthWest extends DragBox {
  def apply(solver: SimplexSolver, owner: RectangularFigure) = new DraggableBox(solver, owner, DraggableBox.SouthWest)
}
object West extends DragBox {
  def apply(solver: SimplexSolver, owner: RectangularFigure) = new DraggableBox(solver, owner, DraggableBox.West)
}
object NorthWest extends DragBox {
  def apply(solver: SimplexSolver, owner: RectangularFigure) = new DraggableBox(solver, owner, DraggableBox.NorthWest)
}

object DraggableBox {
    
  sealed trait Direction
  case object North extends Direction
  case object NorthEast extends Direction
  case object East extends Direction
  case object SouthEast extends Direction
  case object South extends Direction
  case object SouthWest extends Direction
  case object West extends Direction
  case object NorthWest extends Direction
}

class DraggableBox(solver: SimplexSolver, owner: RectangularFigure, direction: DraggableBox.Direction) extends AbstractHandle(owner) {
  private val width, height = 8
  val cx = CVar(direction.toString+" X", 0, solver)
  val cy = CVar(direction.toString+" Y", 0, solver)
  
  def x = cx.value.toInt
  def y = cy.value.toInt

  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    direction match {
      case DraggableBox.North => owner.northMove()
      case DraggableBox.NorthEast => owner.northEastMove() 
      case DraggableBox.East => owner.eastMove()
      case DraggableBox.SouthEast => owner.southEastMove() 
      case DraggableBox.South => owner.southMove()
      case DraggableBox.SouthWest => owner.southWestMove()
      case DraggableBox.West => owner.westMove()
      case DraggableBox.NorthWest => owner.northWestMove()
    }
    solver.addEditVar(cx).beginEdit.addEditVar(cy).beginEdit
    Printer.println("invokeStart")
  }

  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    solver.suggestValue(cx, x).suggestValue(cy, y)
    Printer.println("invokeStep")
  }

  override def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    solver.endEdit()
    Printer.println("invokeEnd")
    owner.printVar
  }
  
  override def locate = new Point(x,y)
  
  override def draw(g: Graphics) {
    g.setColor(Color.BLACK)
    g.drawRect(x - (width / 2), y - (height / 2), width, height)
    g.setColor(Color.WHITE)
    g.fillRect(x - (width / 2) + 1, y - (height / 2) + 1, width - 1, height - 1)
  }

  override def getCursor = {
    direction match {
      case DraggableBox.North => new AWTCursor(java.awt.Cursor.N_RESIZE_CURSOR)
      case DraggableBox.NorthEast => new AWTCursor(java.awt.Cursor.NE_RESIZE_CURSOR)
      case DraggableBox.East => new AWTCursor(java.awt.Cursor.E_RESIZE_CURSOR)
      case DraggableBox.SouthEast => new AWTCursor(java.awt.Cursor.SE_RESIZE_CURSOR)
      case DraggableBox.South => new AWTCursor(java.awt.Cursor.S_RESIZE_CURSOR)
      case DraggableBox.SouthWest => new AWTCursor(java.awt.Cursor.SW_RESIZE_CURSOR)
      case DraggableBox.West =>  new AWTCursor(java.awt.Cursor.W_RESIZE_CURSOR)
      case DraggableBox.NorthWest => new AWTCursor(java.awt.Cursor.NW_RESIZE_CURSOR)
    }
  }
  
  def set(x: Double, y: Double) {
    cx.value = x
    cy.value = y
  }
  
  def set(p: Point) {
    set(p.x,p.y)
  }
  
  def contains(x: Int, y: Int): Boolean =
    (x >= this.x - width / 2) && (x <= this.x + width / 2) && (y >= this.y - height / 2) && (y <= this.y + height / 2)

  override def toString = "("+x+", "+y+")"  
}