package org.shotdraw.framework.align.alignments

import org.shotdraw.figures.RectangularFigure
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.standard.AbstractFigure
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import ch.epfl.lamp.cassowary.Constraint


class WidthAlign(view: DrawingView) extends Align("Width", view) {

  override def constraints = view.selection.foldLeft(List[Constraint]())((l,f) => f match {
    case rf: RectangularFigure => l :::
      view.selection.foldLeft(List[Constraint]())((l,f) => f match {
        case rff: RectangularFigure if rff != rf => (rf.db.cwidth :== rff.db.cwidth) :: l
        case _ => l
      })
    case _ => l
  })
}

class HeightAlign(view: DrawingView) extends Align("Height", view) {

  override def constraints = view.selection.foldLeft(List[Constraint]())((l,f) => f match {
    case rf: RectangularFigure => l :::
      view.selection.foldLeft(List[Constraint]())((l,f) => f match {
        case rff: RectangularFigure if rff != rf => (rf.db.cheight :== rff.db.cheight) :: l
        case _ => l
      })
    case _ => l
  })
}