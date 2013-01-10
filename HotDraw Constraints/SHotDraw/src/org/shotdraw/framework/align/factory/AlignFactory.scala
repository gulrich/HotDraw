package org.shotdraw.framework.align.factory
import org.shotdraw.framework.align.Align
import org.shotdraw.framework.align.Align
import org.shotdraw.framework.align.Align
import org.shotdraw.framework.align.HeightAlign
import org.shotdraw.framework.align.LeftAlign
import org.shotdraw.framework.align.TopAlign
import org.shotdraw.framework.align.WidthAlign
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.align.RightAlign
import org.shotdraw.framework.align.BottomAlign
import org.shotdraw.framework.align.SideBySideAlign
import org.shotdraw.framework.align.Align
import org.shotdraw.framework.align.StackAlign

sealed trait AlignFactory {
  def instance(view: DrawingView): Align
}

object TopAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new TopAlign(view)
}

object LeftAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new LeftAlign(view)
}

object BottomAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new BottomAlign(view)
}

object RightAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new RightAlign(view)
}

object WidthAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new WidthAlign(view)
}

object HeightAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new HeightAlign(view)
}

object SideBySideAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new SideBySideAlign(view)
}

object StackAlignFactory extends AlignFactory {
  override def instance(view: DrawingView) = new StackAlign(view)
}