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
import java.io.IOException
import java.io.Serializable
import org.shotdraw.framework.Figure
import org.shotdraw.util.Storable
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput
import scala.collection.mutable.Map
import javax.swing.JPopupMenu
import java.awt.Font

/**
 * A container for a figure's attributes. The attributes are stored
 * as key/value pairs.
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */
object FigureAttributes {
  private final val serialVersionUID: Long = -6886355144423666716L
}

trait FigureAttributes extends Serializable {
  import FigureAttributes._
  
  private var frameColor: Color = Color.BLACK
  private var fillColor: Color = Color.BLUE
  private var textColor: Color = Color.BLACK
  private var arrowMode: Int = PolyLineFigure.ARROW_TIP_BOTH
  private var fontName: String = "Helvetica"
  private var fontSize: Int = 12
  private var fontStyle: Int = Font.PLAIN
  
  
  def getFrameColor: Color = frameColor
  def getFillColor: Color = fillColor
  def getTextColor: Color = textColor
  def getArrowMode: Int = arrowMode
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
  def setArrowMode(value: Int) {
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
    arrowMode = dr.readInt
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
    dw.writeInt(arrowMode)
    dw.writeString(fontName)
    dw.writeInt(fontSize)
    dw.writeInt(fontStyle)
  }
}

