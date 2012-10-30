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
package org.jhotdraw.figures

import java.awt.Color
import java.io.IOException
import java.io.Serializable
import org.jhotdraw.framework.Figure
import org.jhotdraw.framework.FigureAttributeConstant
import org.jhotdraw.util.Storable
import org.jhotdraw.util.StorableInput
import org.jhotdraw.util.StorableOutput
import scala.collection.mutable.Map
import javax.swing.JPopupMenu

/**
 * A container for a figure's attributes. The attributes are stored
 * as key/value pairs.
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */
object FigureAttributes {
  def writeColor(dw: StorableOutput, colorName: String, color: Color) {
    if (color != null) {
      dw.writeString(colorName)
      dw.writeInt(color.getRed)
      dw.writeInt(color.getGreen)
      dw.writeInt(color.getBlue)
    }
  }

  def readColor(dr: StorableInput): Color = new Color(dr.readInt, dr.readInt, dr.readInt)

  private final val serialVersionUID: Long = -6886355144423666716L
}

class FigureAttributes extends Object with Cloneable with Serializable {
  import FigureAttributes._
  /**
   * Gets the attribute with the given name.
   * @return attribute or null if the key is not defined
   */
  def get(attributeConstant: FigureAttributeConstant): Option[Any] = fMap.get(attributeConstant)

  /**
   * Sets the attribute with the given name and
   * overwrites its previous value.
   */
  def set(attributeConstant: FigureAttributeConstant, value: Any) {
    if (value != null) {
      fMap.put(attributeConstant, value)
    }
    else {
      fMap.remove(attributeConstant)
    }
  }

  /**
   * Tests if an attribute is defined.
   */
  def hasDefined(attributeConstant: FigureAttributeConstant): Boolean = fMap.exists{ case (attr, obj) => attr == attributeConstant}

  /**
   * Clones the attributes.
   */
  override def clone: java.lang.Object = {
    try {
      val a: FigureAttributes = super.clone.asInstanceOf[FigureAttributes]
      a.fMap = fMap.clone
      a
    } catch {
      case e: CloneNotSupportedException => {
        throw new InternalError
      }
    }
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
    fMap = Map()
    val size: Int = dr.readInt
    for(i <- 0 to size-1) {
      val key: String = dr.readString
      val valtype: String = dr.readString
      var vl: Any = null
      if (valtype == "Color") {
        vl = new Color(dr.readInt, dr.readInt, dr.readInt)
      } else if (valtype == "Boolean") {
        vl = new java.lang.Boolean(dr.readString)
      } else if (valtype == "String") {
        vl = dr.readString
      } else if (valtype == "Int") {
        vl = new Integer(dr.readInt)
      } else if (valtype == "Storable") {
        vl = dr.readStorable
      }
      if (!(valtype == Figure.POPUP_MENU || valtype == "UNKNOWN")) {
        val attributeConstant: FigureAttributeConstant = FigureAttributeConstant.getConstant(key)
        set(attributeConstant, vl)
      }
    }
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
    dw.writeInt(fMap.size)
    fMap foreach { case (fac, attr) =>
      val attributeName: String = fac.getName
      dw.writeString(attributeName)
      attr match {
        case s: String =>
          dw.writeString("String")
          dw.writeString(s)
        case c: Color => writeColor(dw, "Color", c)
        case b: Boolean =>
          dw.writeString("Boolean")
          if (b) dw.writeString("TRUE")
          else dw.writeString("FALSE")
        case i: Int =>
          dw.writeString("Int")
          dw.writeInt(i)
        case s: Storable =>
          dw.writeString("Storable")
          dw.writeStorable(s)
        case jpm: JPopupMenu => dw.writeString(Figure.POPUP_MENU)
        case _ =>
          error("Unknown attribute: " + attr)
          dw.writeString("UNKNOWN")
      }
    }
  }

  private var fMap: Map[FigureAttributeConstant, Any] = Map()
}

