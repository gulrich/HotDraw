/*
 * @(#)FigureAttributes.java
 *
 * Project:     JHotdraw - a GUI framework for technical drawings
 *              http://www.jhotdraw.org
 *              http://jhotdraw.sourceforge.net
 * Copyright:   � by the original author(s) and all contributors
 * License:     Lesser GNU Public License (LGPL)
 *              http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.awt.Color
import java.awt.Font
import java.io.IOException
import java.io.Serializable

import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput

/**
 * A container for a figure's attributes. The attributes are stored
 * as key/value pairs.
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */
trait FigureAttributes extends Serializable {  
  import PolyLineFigure._
  
  private var frameColor: Color = Color.BLACK
  private var fillColor: Color = Color.BLUE
  private var textColor: Color = Color.BLACK
  private var arrowMode: ArrowType = ArrowTipBoth
  private var fontName: String = "Helvetica"
  private var fontSize: Int = 12
  private var fontStyle: Int = Font.PLAIN
  
  
  def getFrameColor: Color = frameColor
  def getFillColor: Color = fillColor
  def getTextColor: Color = textColor
  def getArrowMode: PolyLineFigure.ArrowType = arrowMode
  def getFontName: String = fontName
  def getFontSize: Int = fontSize
  def getFontStyle: Int = fontStyle
  
  def setFrameColor(value: Color) {
    frameColor = value
  }
  def setFillColor(value: Color) {
    fillColor = value
  }
  def setTextColor(value: Color) {
    textColor = value
  }
  def setArrowMode(value: PolyLineFigure.ArrowType) {
    arrowMode = value
  }
  def setFontName(value: String) {
    fontName = value
  }
  def setFontSize(value: Int) {
    fontSize = value
  }
  def setFontStyle(value: Int) {
    fontStyle = value
  }

  /**
   * Reads the attributes from a StorableInput.
   * FigureAttributes store the following types directly:
   * Color, Boolean, String, Int. Other attribute types
   * have to implement the Storable interface or they
   * have to be wrapped by an object that implements Storable.
   * @see Storable
   * @see #write
   */
  def read(dr: StorableInput) {
    val s: String = dr.readString
    if (!(s.toLowerCase == "attributes")) {
      throw new IOException("Attributes expected")
    }
    frameColor = dr.readColor
    fillColor = dr.readColor
    textColor = dr.readColor
    arrowMode = dr.readInt match {
      case 0 => ArrowTipNone
      case 1 => ArrowTipStart
      case 2 => ArrowTipEnd
      case 3 => ArrowTipBoth
      case _ => sys.error("Unknown arrow tip")
    }
    fontName = dr.readString
    fontSize = dr.readInt
    fontStyle = dr.readInt
  }

  /**
   * Writes the attributes to a StorableInput.
   * FigureAttributes store the following types directly:
   * Color, Boolean, String, Int. Other attribute types
   * have to implement the Storable interface or they
   * have to be wrapped by an object that implements Storable.
   * @see Storable
   * @see #write
   */
  def write(dw: StorableOutput) {
    dw.writeString("attributes")
    dw.writeColor(frameColor)
    dw.writeColor(fillColor)
    dw.writeColor(textColor)
    dw.writeInt(arrowMode match {
      case ArrowTipNone => 0  
      case ArrowTipBoth => 1
      case ArrowTipEnd => 2
      case ArrowTipStart => 3
    })
    dw.writeString(fontName)
    dw.writeInt(fontSize)
    dw.writeInt(fontStyle)
  }
}

