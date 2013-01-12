package org.shotdraw.framework.align

import org.shotdraw.figures.RectangularFigure
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.standard.AbstractFigure
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import ch.epfl.lamp.cassowary.Constraint


class BottomAlign(view: DrawingView) extends Align("Bottom", view) {

  override def constraints = view.selection.foldLeft(List[Constraint]())((l,f) => f match {
    case rf: RectangularFigure => l :::
      view.selection.foldLeft(List[Constraint]())((l,f) => f match {
        case rff: RectangularFigure if rff != rf => (rf.db.cy :== rff.db.cy+rff.db.cheight-rf.db.cheight) :: l
        case _ => l
      })
    case _ => l
  })
}