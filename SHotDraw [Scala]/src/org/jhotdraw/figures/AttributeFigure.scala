/*
 * @(#)AttributeFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.figures

import org.jhotdraw.util._
import org.jhotdraw.framework._
import org.jhotdraw.standard._
import java.awt._
import java.io._
import java.lang.Object

/**
 * A figure that can keep track of an open ended set of attributes.
 * The attributes are stored in a dictionary implemented by
 * FigureAttributes.
 *
 * @see Figure
 * @see Handle
 * @see FigureAttributes
 *
 * @version <$CURRENT_VERSION$>
 */
object AttributeFigure {
  private def initializeAttributes {
    fgDefaultAttributes = new FigureAttributes
    fgDefaultAttributes.set(FigureAttributeConstant.FRAME_COLOR, Color.black)
    fgDefaultAttributes.set(FigureAttributeConstant.FILL_COLOR, new Color(0x70DB93))
    fgDefaultAttributes.set(FigureAttributeConstant.TEXT_COLOR, Color.black)
    fgDefaultAttributes.set(FigureAttributeConstant.ARROW_MODE, new Integer(0))
    fgDefaultAttributes.set(FigureAttributeConstant.FONT_NAME, "Helvetica")
    fgDefaultAttributes.set(FigureAttributeConstant.FONT_SIZE, new Integer(12))
    fgDefaultAttributes.set(FigureAttributeConstant.FONT_STYLE, new Integer(Font.PLAIN))
  }

  /**
   * Sets or adds a default value for a named attribute
   * @see #getAttribute
   */
  def setDefaultAttribute(name: String, value: AnyRef): Any = {
    val currentValue: Any = getDefaultAttribute(name)
    fgDefaultAttributes.set(FigureAttributeConstant.getConstant(name), value)
    currentValue
  }

  /**
   * Initializes a  default value for a named attribute
   * The difference between this method and setDefaultAttribute is that
   * if the attribute is already set then it will not be changed.<BR>
   * The purpose is to allow more than one source requiring the attribute
   * to initialize it, but only the first initialization will be used.
   *
   * @see #getAttribute
   * @see #setDefaultAttribute
   */
  def initDefaultAttribute(name: String, value: Any): Any = {
    val currentValue: Any = getDefaultAttribute(name)
    if (currentValue != null) {
      currentValue
    } else {
	  fgDefaultAttributes.set(FigureAttributeConstant.getConstant(name), value)
	  null
    }
  }

  /**
   * Gets a the default value for a named attribute
   * @see #getAttribute
   */
  def getDefaultAttribute(name: String): Any = {
    if (fgDefaultAttributes == null) {
      initializeAttributes
    }
    fgDefaultAttributes.get(FigureAttributeConstant.getConstant(name))
  }

  def getDefaultAttribute(attributeConstant: FigureAttributeConstant): Option[Any] = {
    if (fgDefaultAttributes == null) {
      initializeAttributes
    }
    fgDefaultAttributes.get(attributeConstant)
  }

  /**
   * The default attributes associated with a figure.
   * If a figure doesn't have an attribute set, a default
   * value from this shared attribute set is returned.
   * @see #getAttribute
   * @see #setAttribute
   */
  private var fgDefaultAttributes: FigureAttributes = null
  private final val serialVersionUID: Long = -10857585979273442L
}

abstract class AttributeFigure extends AbstractFigure {
  /**
   * The attributes of a figure. Each figure can have
   * an open ended set of attributes. Attributes are
   * identified by name.
   * @see #getAttribute
   * @see #setAttribute
   */
  private var fAttributes: FigureAttributes = null
  
  /**
   * Draws the figure in the given graphics. Draw is a template
   * method calling drawBackground followed by drawFrame.
   */
  def draw(g: Graphics) {
    val fill: Color = getFillColor
    if (!ColorMap.isTransparent(fill)) {
      g.setColor(fill)
      drawBackground(g)
    }
    val frame: Color = getFrameColor
    if (!ColorMap.isTransparent(frame)) {
      g.setColor(frame)
      drawFrame(g)
    }
  }

  /**
   * Draws the background of the figure.
   * @see #draw
   */
  protected def drawBackground(g: Graphics) {}

  /**
   * Draws the frame of the figure.
   * @see #draw
   */
  protected def drawFrame(g: Graphics) {}

  /**
   * Gets the fill color of a figure. This is a convenience
   * method.
   * @see #getAttribute
   */
  def getFillColor: Color = getAttribute(FigureAttributeConstant.FILL_COLOR) match {
    case Some(c: Color) => c
    case other => error("Error, color expected, "+other+" found")
  }


  /**
   * Gets the frame color of a figure. This is a convenience
   * method.
   * @see #getAttribute
   */
  def getFrameColor: Color = getAttribute(FigureAttributeConstant.FRAME_COLOR) match {
    case Some(c: Color) => c
    case other => error("Error, color expected, "+other+" found")
  }

  /**
   * Returns the named attribute or null if a
   * a figure doesn't have an attribute.
   * All figures support the attribute names
   * FillColor and FrameColor
   * @deprecated use getAttribute(FigureAttributeConstant) instead
   */
  override def getAttribute(name: String): Any = getAttribute(FigureAttributeConstant.getConstant(name))

  override def getAttribute(attributeConstant: FigureAttributeConstant): Option[Any] = {
      if (fAttributes != null && fAttributes.hasDefined(attributeConstant))
        fAttributes.get(attributeConstant)
      else
        AttributeFigure.getDefaultAttribute(attributeConstant)
  }

  /**
   * Sets the named attribute to the new value
   * @deprecated use setAttribute(FigureAttributeConstant, Object) instead
   */
  override def setAttribute(name: String, value: Any) {
    setAttribute(FigureAttributeConstant.getConstant(name), value)
  }

  override def setAttribute(attributeConstant: FigureAttributeConstant, value: Any) {
    if (fAttributes == null) {
      fAttributes = new FigureAttributes
    }
    fAttributes.set(attributeConstant, value)
    changed
  }

  /**
   * Stores the Figure to a StorableOutput.
   */
  override def write(dw: StorableOutput) {
    super.write(dw)
    if (fAttributes == null) {
      dw.writeString("no_attributes")
    } else {
      dw.writeString("attributes")
      fAttributes.write(dw)
    }
  }

  /**
   * Reads the Figure from a StorableInput.
   */
  override def read(dr: StorableInput) {
    super.read(dr)
    val s: String = dr.readString
    if (s.toLowerCase == "attributes") {
      fAttributes = new FigureAttributes
      fAttributes.read(dr)
    }
  }

  private def writeObject(o: ObjectOutputStream) {
    val associatedMenu: Any = getAttribute(Figure.POPUP_MENU)
    if (associatedMenu != null) {
      setAttribute(Figure.POPUP_MENU, null)
    }
    o.defaultWriteObject
    if (associatedMenu != null) {
      setAttribute(Figure.POPUP_MENU, associatedMenu)
    }
  }
}

