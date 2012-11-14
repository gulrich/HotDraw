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
package org.shotdraw.figures

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
import org.shotdraw.framework.Figure
import org.shotdraw.framework.FigureAttributeConstant
import org.shotdraw.framework.FigureChangeEvent
import org.shotdraw.framework.FigureChangeListener
import org.shotdraw.framework.Handle
import org.shotdraw.standard.NullHandle
import org.shotdraw.standard.OffsetLocator
import org.shotdraw.standard.RelativeLocator
import org.shotdraw.standard.TextHolder
import org.shotdraw.util.ColorMap
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput

import org.shotdraw.framework.Connector
import org.shotdraw.standard.ChopBoxConnector
import org.shotdraw.standard.AbstractFigure

/**
 * A text figure.
 *
 * @see TextTool
 *
 * @version <$CURRENT_VERSION$>
 */
class TextFigure extends AbstractFigure with FigureChangeListener with TextHolder {
  fillColor = ColorMap.color("None")
  private var fOriginX = 0
  private var fOriginY = 0
  @transient
  private var fSizeIsDirty = true
  @transient
  private var fWidth = 0
  @transient
  private var fHeight = 0
  private var fText = ""
  private var fFont = new Font(fontName, fontStyle, fontSize)
  private var fIsReadOnly = false
  private var fObservedFigure: Figure = null
  private var fLocator: OffsetLocator = null

  /**
   * @see org.shotdraw.framework.Figure#moveBy(int, int)
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
   * @see org.shotdraw.framework.Figure#basicDisplayBox(java.awt.Point, java.awt.Point)
   */
  def basicDisplayBox(newOrigin: Point, newCorner: Point) {
    fOriginX = newOrigin.x
    fOriginY = newOrigin.y
  }

  /**
   * @see org.shotdraw.framework.Figure#displayBox()
   */
  def displayBox: Rectangle = {
    val extent = textExtent
    new Rectangle(fOriginX, fOriginY, extent.width, extent.height)
  }

  /**
   * @see org.shotdraw.standard.TextHolder#textDisplayBox()
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
   * @see org.shotdraw.standard.TextHolder#getFont()
   */
  def getFont: Font = fFont

  /**
   * Usually, a TextHolders is implemented by a Figure subclass. To avoid casting
   * a TextHolder to a Figure this method can be used for polymorphism (in this
   * case, let the (same) object appear to be of another type).
   * Note, that the figure returned is not the figure to which the TextHolder is
   * (and its representing figure) connected.
   * @return figure responsible for representing the content of this TextHolder
   * @see org.shotdraw.standard.TextHolder#getRepresentingFigure()
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
  
  override def fontStyle_=(value: Int) {
    super.fontStyle = value
    setFont(new Font(getFont.getName, value, getFont.getSize))
  }
  
  override def fontName_=(value: String) {
    super.fontName = value
    setFont(new Font(value, getFont.getStyle, getFont.getSize))
  }
  
  override def fontSize_=(value: Int) {
    super.fontSize = value
    setFont(new Font(getFont.getName, getFont.getStyle, value))
  }

  /**
   * Updates the location whenever the figure changes itself.
   * @see org.shotdraw.framework.Figure#changed()
   */
  override def changed {
    super.changed
  }


  /**
   * Gets the text shown by the text figure.
   * @see org.shotdraw.standard.TextHolder#getText()
   */
  def getText: String = fText

  /**
   * Sets the text shown by the text figure.
   * @see org.shotdraw.standard.TextHolder#setText(java.lang.String)
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
   * @see org.shotdraw.standard.TextHolder#acceptsTyping()
   */
  def acceptsTyping: Boolean = !fIsReadOnly

  /**
   * @see org.shotdraw.figures.AbstractFigure#drawBackground(java.awt.Graphics)
   */
  override def drawBackground(g: Graphics) {
    val r = displayBox
    g.fillRect(r.x, r.y, r.width, r.height)
  }

  /**
   * @see org.shotdraw.figures.AbstractFigure#drawFrame(java.awt.Graphics)
   */
  override def drawFrame(g: Graphics) {
    g.setFont(fFont)
    g.setColor(textColor)
    val metrics = g.getFontMetrics(fFont)
    val r = displayBox
    g.drawString(getText, r.x, r.y + metrics.getAscent)
  }

  protected def textExtent: Dimension = {
    if (!fSizeIsDirty) {
      return new Dimension(fWidth, fHeight)
    }
    val metrics = Toolkit.getDefaultToolkit.getFontMetrics(fFont)
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
   * @see org.shotdraw.standard.TextHolder#overlayColumns()
   */
  def overlayColumns: Int = {
    val length = getText.length
    var columns = 20
    if (length != 0) {
      columns = getText.length + 3
    }
    columns
  }

  /**
   * @see org.shotdraw.framework.Figure#handles()
   */
  def handles: Seq[Handle] = {
    new NullHandle(this, RelativeLocator.northWest) ::
        new NullHandle(this, RelativeLocator.northEast) ::
        new NullHandle(this, RelativeLocator.southEast) ::
        new FontSizeHandle(this, RelativeLocator.southWest) :: Nil
  }

  /**
   * @see org.shotdraw.util.Storable#write(org.shotdraw.util.StorableOutput)
   */
  override def write(dw: StorableOutput) {
    super.write(dw)
    val r = displayBox
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
   * @see org.shotdraw.util.Storable#read(org.shotdraw.util.StorableInput)
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
   * @see org.shotdraw.standard.TextHolder#connect(org.shotdraw.framework.Figure)
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
   * @see org.shotdraw.framework.FigureChangeListener#figureChanged(org.shotdraw.framework.FigureChangeEvent)
   */
  def figureChanged(e: FigureChangeEvent) {
    willChange
    updateLocation
    changed
  }

  /**
   * @see org.shotdraw.framework.FigureChangeListener#figureRemoved(org.shotdraw.framework.FigureChangeEvent)
   */
  def figureRemoved(e: FigureChangeEvent) {
    if (listener != null) {
      val rect = invalidateRectangle(displayBox)
      listener.figureRemoved(new FigureChangeEvent(this, rect, e))
    }
  }

  /**
   * @see org.shotdraw.framework.FigureChangeListener#figureRequestRemove(org.shotdraw.framework.FigureChangeEvent)
   */
  def figureRequestRemove(e: FigureChangeEvent) {}

  /**
   * @see org.shotdraw.framework.FigureChangeListener#figureInvalidated(org.shotdraw.framework.FigureChangeEvent)
   */
  def figureInvalidated(e: FigureChangeEvent) {}

  /**
   * @see org.shotdraw.framework.FigureChangeListener#figureRequestUpdate(org.shotdraw.framework.FigureChangeEvent)
   */
  def figureRequestUpdate(e: FigureChangeEvent) {}

  /**
   * Updates the location relative to the connected figure.
   * The TextFigure is centered around the located point.
   */
  protected def updateLocation {
    if (getLocator != null) {
      val p = getLocator.locate(getObservedFigure)
      p.x -= size.width / 2 + fOriginX
      p.y -= size.height / 2 + fOriginY
      if (p.x != 0 || p.y != 0) {
        basicMoveBy(p.x, p.y)
      }
    }
  }
  
  /**
   * @see org.shotdraw.framework.Figure#release()
   */
  override def release {
    super.release
    disconnect(getObservedFigure)
  }

  /**
   * Disconnects a text holder from a connect figure.
   * @see org.shotdraw.standard.TextHolder#disconnect(org.shotdraw.framework.Figure)
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
   * @see org.shotdraw.framework.Figure#getTextHolder()
   */
  override def getTextHolder: TextHolder = this
}

