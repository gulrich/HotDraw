package org.shotdraw.framework
import org.shotdraw.figures.FigureAttributes
import java.awt.Color
import org.shotdraw.figures.PolyLineFigure.ArrowType

trait FigureAttributeConstant[T] {
  def setAttribute(figAttr: FigureAttributes, value: T)
  def getAttribute(figAttr: FigureAttributes): T
}

case object FrameColor extends FigureAttributeConstant[Color] {
  def setAttribute(figAttr: FigureAttributes, value: Color) {
    figAttr.frameColor = value
  }
  def getAttribute(figAttr: FigureAttributes): Color = figAttr.frameColor
}

case object FillColor extends FigureAttributeConstant[Color] {
  def setAttribute(figAttr: FigureAttributes, value: Color) {
    figAttr.fillColor = value
  }
  def getAttribute(figAttr: FigureAttributes): Color = figAttr.fillColor
}

case object TextColor extends FigureAttributeConstant[Color] {
  def setAttribute(figAttr: FigureAttributes, value: Color) {
    figAttr.textColor = value
  }
  def getAttribute(figAttr: FigureAttributes): Color = figAttr.textColor
}

case object ArrowMode extends FigureAttributeConstant[ArrowType] {
  def setAttribute(figAttr: FigureAttributes, value: ArrowType) {
    figAttr.arrowMode = value
  }
  def getAttribute(figAttr: FigureAttributes): ArrowType = figAttr.arrowMode
}

case object FontName extends FigureAttributeConstant[String] {
  def setAttribute(figAttr: FigureAttributes, value: String) {
    figAttr.fontName = value
  }
  def getAttribute(figAttr: FigureAttributes): String = figAttr.fontName
}

case object FontSize extends FigureAttributeConstant[Int] {
  def setAttribute(figAttr: FigureAttributes, value: Int) {
    figAttr.fontSize = value
  }
  def getAttribute(figAttr: FigureAttributes): Int = figAttr.fontSize
}

case object FontStyle extends FigureAttributeConstant[Int] {
  def setAttribute(figAttr: FigureAttributes, value: Int) {
    figAttr.fontStyle = value
  }
  def getAttribute(figAttr: FigureAttributes): Int = figAttr.fontStyle
}