package org.shotdraw.framework.align
import org.shotdraw.framework.DrawingView
import scala.collection.mutable.ArrayBuffer
import org.shotdraw.figures.RectangularFigure
import ch.epfl.lamp.cassowary.Constraint
import org.shotdraw.framework.Figure
import org.shotdraw.standard.AbstractFigure

abstract class Align(name: String, view: DrawingView) {

  private var enabled_ = false
  private var consts: List[Constraint]= List()
  
  def constraints: List[Constraint]
  def enabled = enabled_
  
  def enable() {
    if(consts isEmpty) consts = constraints
    consts foreach {c => view.solver.addConstraint(c) }
    enabled_ = true
  }
  
  def disable() {
   consts foreach {c => view.solver.removeConstraint(c) }
   enabled_ = false
  }
  
  override def toString = name  
}