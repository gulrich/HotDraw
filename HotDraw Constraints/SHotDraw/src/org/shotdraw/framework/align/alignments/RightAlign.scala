package org.shotdraw.framework.align.alignments

import org.shotdraw.figures.RectangularFigure
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.standard.AbstractFigure
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import ch.epfl.lamp.cassowary.Constraint


class RightAlign(view: DrawingView) extends Align("Right", view) {

  override def constraints = view.selection.foldLeft(List[Constraint]())((l,f) => f match {
    case rf: RectangularFigure => l :::
      view.selection.foldLeft(List[Constraint]())((l,f) => f match {
        case rff: RectangularFigure if rff != rf => (rf.db.cx :== rff.db.cx+rff.db.cwidth-rf.db.cwidth) :: l
        case _ => l
      })
    case _ => l
  })
}