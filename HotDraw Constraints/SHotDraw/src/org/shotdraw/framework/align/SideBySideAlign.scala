package org.shotdraw.framework.align
import org.shotdraw.framework.DrawingView
import ch.epfl.lamp.cassowary.Constraint
import org.shotdraw.figures.RectangularFigure
import org.shotdraw.framework.Figure


class SideBySideAlign(view: DrawingView) extends Align("Side by side", view) {
   
  override def constraints = {
    var l = List[Constraint]()
    var last: RectangularFigure = null
    view.selection.sortWith((f1,f2) => f1.displayBox.x < f2.displayBox.x).foreach ( f => f match {
      case rf: RectangularFigure =>
        if(last != null) l ::= (rf.db.cx :== last.db.cx+last.db.cwidth)
        last = rf
      case _ =>
    })
    l
  }
}

class StackAlign(view: DrawingView) extends Align("Stack", view) {
   
  override def constraints = {
    var l = List[Constraint]()
    var last: RectangularFigure = null
    view.selection.sortWith((f1,f2) => f1.displayBox.y < f2.displayBox.y).foreach ( f => f match {
      case rf: RectangularFigure =>
        if(last != null) l ::= (rf.db.cy :== last.db.cy+last.db.cheight)
        last = rf
      case _ =>
    })
    l
  }
}
