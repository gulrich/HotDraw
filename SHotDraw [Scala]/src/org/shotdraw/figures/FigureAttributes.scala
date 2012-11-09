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
  
  private var _frameColor: Color = Color.BLACK
  private var _fillColor: Color = Color.BLUE
  private var _textColor: Color = Color.BLACK
  private var _arrowMode: ArrowType = ArrowTipBoth
  private var _fontName: String = "Helvetica"
  private var _fontSize: Int = 12
  private var _fontStyle: Int = Font.PLAIN
  
  
  def frameColor: Color = _frameColor
  def fillColor: Color = _fillColor
  def textColor: Color = _textColor
  def arrowMode: PolyLineFigure.ArrowType = _arrowMode
  def fontName: String = _fontName
  def fontSize: Int = _fontSize
  def fontStyle: Int = _fontStyle
  
  def frameColor_=(value: Color) {
    _frameColor = value
  }
  def fillColor_=(value: Color) {
    _fillColor = value
  }
  def textColor_=(value: Color) {
    _textColor = value
  }
  def arrowMode_=(value: PolyLineFigure.ArrowType) {
    _arrowMode = value
  }
  def fontName_=(value: String) {
    _fontName = value
  }
  def fontSize_=(value: Int) {
    _fontSize = value
  }
  def fontStyle_=(value: Int) {
    _fontStyle = value
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
    val s = dr.readString
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
      case ArrowTipStart => 1
      case ArrowTipEnd => 2
      case ArrowTipBoth => 3
    })
    dw.writeString(fontName)
    dw.writeInt(fontSize)
    dw.writeInt(fontStyle)
  }
}

