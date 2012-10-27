/*
 * @(#)TextFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.figures

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.awt.Toolkit
import java.io.IOException
import java.io.ObjectInputStream
import org.jhotdraw.framework.Figure
import org.jhotdraw.framework.FigureAttributeConstant
import org.jhotdraw.framework.FigureChangeEvent
import org.jhotdraw.framework.FigureChangeListener
import org.jhotdraw.framework.Handle
import org.jhotdraw.framework.HandleEnumeration
import org.jhotdraw.standard.HandleEnumerator
import org.jhotdraw.standard.NullHandle
import org.jhotdraw.standard.OffsetLocator
import org.jhotdraw.standard.RelativeLocator
import org.jhotdraw.standard.TextHolder
import org.jhotdraw.util.ColorMap
import org.jhotdraw.util.StorableInput
import org.jhotdraw.util.StorableOutput
import java.lang.Object

/**
 * A text figure.
 *
 * @see TextTool
 *
 * @version <$CURRENT_VERSION$>
 */
object TextFigure {
  /**
   * Creates the current font to be used for new text figures.
   */
  def createCurrentFont: Font = new Font(fgCurrentFontName, fgCurrentFontStyle, fgCurrentFontSize)

  /**
   * Sets the current font name
   */
  def setCurrentFontName(name: String) {
    fgCurrentFontName = name
  }

  /**
   * Sets the current font size.
   */
  def setCurrentFontSize(size: Int) {
    fgCurrentFontSize = size
  }

  /**
   * Sets the current font style.
   */
  def setCurrentFontStyle(style: Int) {
    fgCurrentFontStyle = style
  }

  private var fgCurrentFontName: String = "Helvetica"
  private var fgCurrentFontSize: Int = 12
  private var fgCurrentFontStyle: Int = Font.PLAIN
  private final val serialVersionUID: Long = 4599820785949456124L
}

class TextFigure extends AttributeFigure with FigureChangeListener with TextHolder {
  private var fOriginX: Int = 0
  private var fOriginY: Int = 0
  @transient
  private var fSizeIsDirty: Boolean = true
  @transient
  private var fWidth: Int = 0
  @transient
  private var fHeight: Int = 0
  private var fText: String = ""
  private var fFont: Font = TextFigure.createCurrentFont
  private var fIsReadOnly: Boolean = false
  private var fObservedFigure: Figure = null
  private var fLocator: OffsetLocator = null
  setAttribute(FigureAttributeConstant.FILL_COLOR, ColorMap.color("None"))

  /**
   * @see org.jhotdraw.framework.Figure#moveBy(int, int)
   */
  override def moveBy(x: Int, y: Int) {
    willChange
    basicMoveBy(x, y)
    if (getLocator != null) {
      getLocator.moveBy(x, y)
    }
    changed
  }

  protected def basicMoveBy(x: Int, y: Int) {
    fOriginX += x
    fOriginY += y
  }

  /**
   * @see org.jhotdraw.framework.Figure#basicDisplayBox(java.awt.Point, java.awt.Point)
   */
  def basicDisplayBox(newOrigin: Point, newCorner: Point) {
    fOriginX = newOrigin.x
    fOriginY = newOrigin.y
  }

  /**
   * @see org.jhotdraw.framework.Figure#displayBox()
   */
  def displayBox: Rectangle = {
    val extent: Dimension = textExtent
    new Rectangle(fOriginX, fOriginY, extent.width, extent.height)
  }

  /**
   * @see org.jhotdraw.standard.TextHolder#textDisplayBox()
   */
  def textDisplayBox: Rectangle = displayBox

  /**
   * Tests whether this figure is read only.
   */
  def readOnly: Boolean = fIsReadOnly

  /**
   * Sets the read only status of the text figure.
   */
  def setReadOnly(isReadOnly: Boolean) {
    fIsReadOnly = isReadOnly
  }

  /**
   * Gets the font.
   * @see org.jhotdraw.standard.TextHolder#getFont()
   */
  def getFont: Font = fFont

  /**
   * Usually, a TextHolders is implemented by a Figure subclass. To avoid casting
   * a TextHolder to a Figure this method can be used for polymorphism (in this
   * case, let the (same) object appear to be of another type).
   * Note, that the figure returned is not the figure to which the TextHolder is
   * (and its representing figure) connected.
   * @return figure responsible for representing the content of this TextHolder
   * @see org.jhotdraw.standard.TextHolder#getRepresentingFigure()
   */
  def getRepresentingFigure: Figure = this

  /**
   * Sets the font.
   */
  def setFont(newFont: Font) {
    willChange
    fFont = newFont
    markDirty
    changed
  }

  /**
   * Updates the location whenever the figure changes itself.
   * @see org.jhotdraw.framework.Figure#changed()
   */
  override def changed {
    super.changed
  }

  /**
   * A text figure understands the "FontSize", "FontStyle", and "FontName"
   * attributes.
   *
   * @see org.jhotdraw.framework.Figure#getAttribute(java.lang.String)
   * @deprecated use getAttribute(FigureAttributeConstant) instead
   */
  override def getAttribute(name: String): Any = getAttribute(FigureAttributeConstant.getConstant(name))

  /**
   * A text figure understands the "FontSize", "FontStyle", and "FontName"
   * attributes.
   * @see org.jhotdraw.framework.Figure#getAttribute(org.jhotdraw.framework.FigureAttributeConstant)
   */
  override def getAttribute(attributeConstant: FigureAttributeConstant): Option[Any] = {
    val font: Font = getFont
    if (attributeConstant == FigureAttributeConstant.FONT_SIZE) Some(font.getSize)
    else if (attributeConstant == FigureAttributeConstant.FONT_STYLE) Some(font.getStyle)
    else if (attributeConstant == FigureAttributeConstant.FONT_NAME) Some(font.getName)
    else super.getAttribute(attributeConstant)
  }

  /**
   * A text figure understands the "FontSize", "FontStyle", and "FontName"
   * attributes.
   *
   * @see org.jhotdraw.framework.Figure#setAttribute(java.lang.String, java.lang.Object)
   * @deprecated use setAttribute(FigureAttributeConstant, Object) instead
   */
  override def setAttribute(name: String, value: Any) {
    setAttribute(FigureAttributeConstant.getConstant(name), value)
  }

  /**
   * A text figure understands the "FontSize", "FontStyle", and "FontName"
   * attributes.
   * @see org.jhotdraw.framework.Figure#setAttribute(org.jhotdraw.framework.FigureAttributeConstant, java.lang.Object)
   */
  override def setAttribute(attributeConstant: FigureAttributeConstant, value: Any) {
    val font: Font = getFont
    if (attributeConstant == FigureAttributeConstant.FONT_SIZE) {
      val s: Integer = value.asInstanceOf[Integer]
      setFont(new Font(font.getName, font.getStyle, s.intValue))
    } else if (attributeConstant == FigureAttributeConstant.FONT_STYLE) {
      val s: Integer = value.asInstanceOf[Integer]
      var style: Int = font.getStyle
      if (s.intValue == Font.PLAIN) {
        style = Font.PLAIN
      } else {
        style = style ^ s.intValue
      }
      setFont(new Font(font.getName, style, font.getSize))
    } else if (attributeConstant == FigureAttributeConstant.FONT_NAME) {
      val n: String = value.asInstanceOf[String]
      setFont(new Font(n, font.getStyle, font.getSize))
    } else {
      super.setAttribute(attributeConstant, value)
    }
  }

  /**
   * Gets the text shown by the text figure.
   * @see org.jhotdraw.standard.TextHolder#getText()
   */
  def getText: String = fText

  /**
   * Sets the text shown by the text figure.
   * @see org.jhotdraw.standard.TextHolder#setText(java.lang.String)
   */
  def setText(newText: String) {
    if (newText == null || !(newText == fText)) {
      willChange
      fText = newText
      markDirty
      changed
    }
  }

  /**
   * Tests whether the figure accepts typing.
   * @see org.jhotdraw.standard.TextHolder#acceptsTyping()
   */
  def acceptsTyping: Boolean = !fIsReadOnly

  /**
   * @see org.jhotdraw.figures.AttributeFigure#drawBackground(java.awt.Graphics)
   */
  override def drawBackground(g: Graphics) {
    val r: Rectangle = displayBox
    g.fillRect(r.x, r.y, r.width, r.height)
  }

  /**
   * @see org.jhotdraw.figures.AttributeFigure#drawFrame(java.awt.Graphics)
   */
  override def drawFrame(g: Graphics) {
    g.setFont(fFont)
    getAttribute(FigureAttributeConstant.TEXT_COLOR) match {
      case Some(c: Color) => g.setColor(c)
      case other => error("Color exptected, but " + other + " found")
    }
    val metrics: FontMetrics = g.getFontMetrics(fFont)
    val r: Rectangle = displayBox
    g.drawString(getText, r.x, r.y + metrics.getAscent)
  }

  protected def textExtent: Dimension = {
    if (!fSizeIsDirty) {
      return new Dimension(fWidth, fHeight)
    }
    val metrics: FontMetrics = Toolkit.getDefaultToolkit.getFontMetrics(fFont)
    fWidth = metrics.stringWidth(getText)
    fHeight = metrics.getHeight
    fSizeIsDirty = false
    new Dimension(fWidth, fHeight)
  }

  protected def markDirty {
    fSizeIsDirty = true
  }

  /**
   * Gets the number of columns to be overlaid when the figure is edited.
   * @see org.jhotdraw.standard.TextHolder#overlayColumns()
   */
  def overlayColumns: Int = {
    val length: Int = getText.length
    var columns: Int = 20
    if (length != 0) {
      columns = getText.length + 3
    }
    columns
  }

  /**
   * @see org.jhotdraw.framework.Figure#handles()
   */
  def handles: HandleEnumeration = {
    new HandleEnumerator(new NullHandle(this, RelativeLocator.northWest) ::
        new NullHandle(this, RelativeLocator.northEast) ::
        new NullHandle(this, RelativeLocator.southEast) ::
        new FontSizeHandle(this, RelativeLocator.southWest) :: Nil)
  }

  /**
   * @see org.jhotdraw.util.Storable#write(org.jhotdraw.util.StorableOutput)
   */
  override def write(dw: StorableOutput) {
    super.write(dw)
    val r: Rectangle = displayBox
    dw.writeInt(r.x)
    dw.writeInt(r.y)
    dw.writeString(getText)
    dw.writeString(fFont.getName)
    dw.writeInt(fFont.getStyle)
    dw.writeInt(fFont.getSize)
    dw.writeBoolean(fIsReadOnly)
    dw.writeStorable(getObservedFigure)
    dw.writeStorable(getLocator)
  }

  /**
   * @see org.jhotdraw.util.Storable#read(org.jhotdraw.util.StorableInput)
   */
  override def read(dr: StorableInput) {
    super.read(dr)
    markDirty
    basicDisplayBox(new Point(dr.readInt, dr.readInt), null)
    setText(dr.readString)
    fFont = new Font(dr.readString, dr.readInt, dr.readInt)
    fIsReadOnly = dr.readBoolean
    setObservedFigure(dr.readStorable.asInstanceOf[Figure])
    if (getObservedFigure != null) {
      getObservedFigure.addFigureChangeListener(this)
    }
    setLocator(dr.readStorable.asInstanceOf[OffsetLocator])
  }

  private def readObject(s: ObjectInputStream) {
    s.defaultReadObject
    if (getObservedFigure != null) {
      getObservedFigure.addFigureChangeListener(this)
    }
    markDirty
  }

  /**
   * @see org.jhotdraw.standard.TextHolder#connect(org.jhotdraw.framework.Figure)
   */
  def connect(figure: Figure) {
    if (getObservedFigure != null) {
      getObservedFigure.removeFigureChangeListener(this)
    }
    setObservedFigure(figure)
    setLocator(new OffsetLocator(getObservedFigure.connectedTextLocator(this)))
    getObservedFigure.addFigureChangeListener(this)
    willChange
    updateLocation
    changed
  }

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureChanged(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureChanged(e: FigureChangeEvent) {
    willChange
    updateLocation
    changed
  }

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureRemoved(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureRemoved(e: FigureChangeEvent) {
    if (listener != null) {
      val rect: Rectangle = invalidateRectangle(displayBox)
      listener.figureRemoved(new FigureChangeEvent(this, rect, e))
    }
  }

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureRequestRemove(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureRequestRemove(e: FigureChangeEvent) {}

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureInvalidated(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureInvalidated(e: FigureChangeEvent) {}

  /**
   * @see org.jhotdraw.framework.FigureChangeListener#figureRequestUpdate(org.jhotdraw.framework.FigureChangeEvent)
   */
  def figureRequestUpdate(e: FigureChangeEvent) {}

  /**
   * Updates the location relative to the connected figure.
   * The TextFigure is centered around the located point.
   */
  protected def updateLocation {
    if (getLocator != null) {
      val p: Point = getLocator.locate(getObservedFigure)
      p.x -= size.width / 2 + fOriginX
      p.y -= size.height / 2 + fOriginY
      if (p.x != 0 || p.y != 0) {
        basicMoveBy(p.x, p.y)
      }
    }
  }

  /**
   * @see org.jhotdraw.framework.Figure#release()
   */
  override def release {
    super.release
    disconnect(getObservedFigure)
  }

  /**
   * Disconnects a text holder from a connect figure.
   * @see org.jhotdraw.standard.TextHolder#disconnect(org.jhotdraw.framework.Figure)
   */
  def disconnect(disconnectFigure: Figure) {
    if (disconnectFigure != null) {
      disconnectFigure.removeFigureChangeListener(this)
    }
    setLocator(null)
    setObservedFigure(null)
  }

  protected def setObservedFigure(newObservedFigure: Figure) {
    fObservedFigure = newObservedFigure
  }

  def getObservedFigure: Figure = fObservedFigure

  protected def setLocator(newLocator: OffsetLocator) {
    fLocator = newLocator
  }

  protected def getLocator: OffsetLocator = fLocator

  /**
   * @see org.jhotdraw.framework.Figure#getTextHolder()
   */
  override def getTextHolder: TextHolder = this
}

