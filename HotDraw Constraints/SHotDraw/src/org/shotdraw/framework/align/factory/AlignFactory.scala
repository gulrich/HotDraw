package org.shotdraw.framework.align.factory
import org.shotdraw.framework.align.Align
import org.shotdraw.framework.align.Align
import org.shotdraw.framework.align.Align
import org.shotdraw.framework.align.HeightAlign
import org.shotdraw.framework.align.HorizontalAlign
import org.shotdraw.framework.align.LeftAlign
import org.shotdraw.framework.align.TopAlign
import org.shotdraw.framework.align.VerticalAlign
import org.shotdraw.framework.align.WidthAlign
import org.shotdraw.framework.DrawingView

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

object WidthAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new WidthAlign(view)
}

object HeightAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new HeightAlign(view)
}