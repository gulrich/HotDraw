package org.shotdraw.framework.align.factory
import org.shotdraw.framework.align.alignments.Align
import org.shotdraw.framework.align.alignments.BottomAlign
import org.shotdraw.framework.align.alignments.HeightAlign
import org.shotdraw.framework.align.alignments.LeftAlign
import org.shotdraw.framework.align.alignments.RightAlign
import org.shotdraw.framework.align.alignments.SideBySideAlign
import org.shotdraw.framework.align.alignments.StackAlign
import org.shotdraw.framework.align.alignments.TopAlign
import org.shotdraw.framework.align.alignments.WidthAlign
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