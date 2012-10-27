/*
 * @(#)FigureChangeEvent.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.framework

import java.awt.Rectangle
import java.util.EventObject

/**
 * FigureChange event passed to FigureChangeListeners.
 *
 * @version <$CURRENT_VERSION$>
 */
object FigureChangeEvent {
  private final val serialVersionUID: Long = 665951480886293118L
  private final val EMPTY_RECTANGLE: Rectangle = new Rectangle(0, 0, 0, 0)
}

class FigureChangeEvent(newSource: Figure, myRectangle: Rectangle, myNestedEvent: FigureChangeEvent) extends EventObject(newSource) {

  def this(newSource: Figure) {
    this(newSource, FigureChangeEvent.EMPTY_RECTANGLE, null) 
  }

  def this(newSource: Figure, newRect: Rectangle) {
    this(newSource, newRect, null)
  }

  /**
   * Gets the changed figure
   */
  def getFigure: Figure = getSource match {
    case f:Figure => f
    case _ => error("Figure expected")
  }

  /**
   * Gets the changed rectangle
   */
  def getInvalidatedRectangle: Rectangle = myRectangle

  def getNestedEvent: FigureChangeEvent = myNestedEvent
}

