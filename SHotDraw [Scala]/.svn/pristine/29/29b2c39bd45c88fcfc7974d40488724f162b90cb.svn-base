/*
 * @(#)FigureAttributeConstant.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.framework

import java.io.Serializable

/**
 * A FigureAttribute is a constant for accessing a special figure attribute. It
 * does not contain a value but just defines a unique attribute ID. Therefore,
 * they provide a type-safe way of defining attribute constants.
 * (SourceForge feature request ID: <>)
 *
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
object FigureAttributeConstant {

  private final val serialVersionUID: Long = 8183114242327239478L
  
  var attributeConstants: List[FigureAttributeConstant] = List[FigureAttributeConstant]()
  
  final val FRAME_COLOR_STR: String = "FrameColor"
  final val FRAME_COLOR: FigureAttributeConstant = new FigureAttributeConstant(FRAME_COLOR_STR, 1)
  final val FILL_COLOR_STR: String = "FillColor"
  final val FILL_COLOR: FigureAttributeConstant = new FigureAttributeConstant(FILL_COLOR_STR, 2)
  final val TEXT_COLOR_STR: String = "TextColor"
  final val TEXT_COLOR: FigureAttributeConstant = new FigureAttributeConstant(TEXT_COLOR_STR, 3)
  final val ARROW_MODE_STR: String = "ArrowMode"
  final val ARROW_MODE: FigureAttributeConstant = new FigureAttributeConstant(ARROW_MODE_STR, 4)
  final val FONT_NAME_STR: String = "FontName"
  final val FONT_NAME: FigureAttributeConstant = new FigureAttributeConstant(FONT_NAME_STR, 5)
  final val FONT_SIZE_STR: String = "FontSize"
  final val FONT_SIZE: FigureAttributeConstant = new FigureAttributeConstant(FONT_SIZE_STR, 6)
  final val FONT_STYLE_STR: String = "FontStyle"
  final val FONT_STYLE: FigureAttributeConstant = new FigureAttributeConstant(FONT_STYLE_STR, 7)
  final val URL_STR: String = "URL"
  final val URL: FigureAttributeConstant = new FigureAttributeConstant(URL_STR, 8)
  final val LOCATION_STR: String = "Location"
  final val LOCATION: FigureAttributeConstant = new FigureAttributeConstant(LOCATION_STR, 9)
  final val XALIGNMENT_STR: String = "XAlignment"
  final val XALIGNMENT: FigureAttributeConstant = new FigureAttributeConstant(XALIGNMENT_STR, 10)
  final val YALIGNMENT_STR: String = "YAlignment"
  final val YALIGNMENT: FigureAttributeConstant = new FigureAttributeConstant(YALIGNMENT_STR, 11)
  final val TOP_MARGIN_STR: String = "TopMargin"
  final val TOP_MARGIN: FigureAttributeConstant = new FigureAttributeConstant(TOP_MARGIN_STR, 12)
  final val RIGHT_MARGIN_STR: String = "RightMargin"
  final val RIGHT_MARGIN: FigureAttributeConstant = new FigureAttributeConstant(RIGHT_MARGIN_STR, 13)
  final val BOTTOM_MARGIN_STR: String = "BottomMargin"
  final val BOTTOM_MARGIN: FigureAttributeConstant = new FigureAttributeConstant(BOTTOM_MARGIN_STR, 14)
  final val LEFT_MARGIN_STR: String = "LeftMargin"
  final val LEFT_MARGIN: FigureAttributeConstant = new FigureAttributeConstant(LEFT_MARGIN_STR, 15)
  final val POPUP_MENU_STR: String = "PopupMenu"
  final val POPUP_MENU: FigureAttributeConstant = new FigureAttributeConstant(POPUP_MENU_STR, 16)
  
  /**
   * @return an existing constant for a given name or create a new one
   */
  def getConstant(constantName: String): FigureAttributeConstant = {
    attributeConstants.find((c) => c.getName == constantName) match {
      case Some(const) => const
      case None => new FigureAttributeConstant(constantName)
    }
  }

  def getConstant(constantId: Int): FigureAttributeConstant = attributeConstants(constantId)
  
    /**
   * Constants are put into the place according to their ID, thus, it is
   * recommended to have subsequent attribute IDs.
   */
  private def addConstant(newConstant: FigureAttributeConstant) {
    val idPos: Int = newConstant.getID - 1
    if ((idPos < attributeConstants.length) && (attributeConstants(idPos) != null)) {
      throw new JHotDrawRuntimeException("No unique FigureAttribute ID: " + newConstant.getID)
    }
    if (idPos >= attributeConstants.length) {
      attributeConstants ::= newConstant
    }
    attributeConstants.updated(idPos, newConstant)
  }
  
}

class FigureAttributeConstant extends Serializable with Cloneable {
  
  import FigureAttributeConstant._
  
  private def this(newName: String, newID: Int) {
    this()
    setName(newName)
    setID(newID)
    addConstant(this)
  }

  def this(newName: String) {
    this(newName, FigureAttributeConstant.attributeConstants.length + 1)
  }

  private def setName(newName: String) {
    myName = newName
  }

  def getName: String = myName

  private def setID(newID: Int) {
    myID = newID
  }

  def getID: Int = myID

  override def equals(compareObject: Any): Boolean = {
    if (compareObject == null) {
      return false
    }
    if (!(compareObject.isInstanceOf[FigureAttributeConstant])) {
      return false
    }
    val compareAttribute: FigureAttributeConstant = compareObject.asInstanceOf[FigureAttributeConstant]
    if (compareAttribute.getID != getID) {
      return false
    }
    if ((compareAttribute.getName == null) && (getName == null)) {
      return true
    }
    if ((compareAttribute.getName != null) && (getName != null)) {
      return getName == compareAttribute.getName
    }
    return false
  }
  
  override def hashCode: Int = getID


  private var myID: Int = 0
  private var myName: String = ""

}

