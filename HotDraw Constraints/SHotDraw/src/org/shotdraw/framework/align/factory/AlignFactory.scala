package org.shotdraw.framework.align.factory
import org.shotdraw.framework.align.Align
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.align.TopAlign
import org.shotdraw.framework.align.BackFigureAlign
import org.shotdraw.framework.align.LeftAlign
import org.shotdraw.framework.align.VerticalAlign
import org.shotdraw.framework.align.HorizontalAlign

sealed trait AlignFactory {
  def instance(view: DrawingView): Align
}

object TopAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new TopAlign(view)
}

object LeftAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new LeftAlign(view)
}

object VerticalAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new VerticalAlign(view)
}

object HorizontalAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new HorizontalAlign(view)
}