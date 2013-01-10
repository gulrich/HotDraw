package org.shotdraw.framework.align
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.framework.DrawingEditor
import org.shotdraw.figures.RectangularFigure
import ch.epfl.lamp.cassowary.Constraint
import scala.collection.mutable.ArrayBuffer
import org.shotdraw.standard.AbstractFigure

class TopAlign(view: DrawingView) extends Align("Top", view) {

  private var figure: RectangularFigure = view.selection(0).asInstanceOf[RectangularFigure] //TODO Change that
  
  override def constraints = view.selection.foldLeft(List[Constraint]())((l,f) => f match {
    case rf: RectangularFigure => l ::: List(rf.db.cy :== figure.db.cy)
    case _ => l
  })  
}