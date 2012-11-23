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
package org.shotdraw.framework

import java.awt.Rectangle
import java.util.EventObject

/**
 * FigureChange event passed to FigureChangeListeners.
 *
 * @version <$CURRENT_VERSION$>
 */
class FigureChangeEvent(newSource: Figure, myRectangle: Rectangle, myNestedEvent: FigureChangeEvent) extends EventObject(newSource) {

  def this(newSource: Figure) {
    this(newSource, new Rectangle, null) 
  }

  def this(newSource: Figure, newRect: Rectangle) {
    this(newSource, newRect, null)
  }

  /**
   * Gets the changed figure
   */
  def getFigure: Figure = getSource match {
    case f:Figure => f
    case _ => sys.error("Figure expected")
  }

  /**
   * Gets the changed rectangle
   */
  def getInvalidatedRectangle: Rectangle = myRectangle

  def getNestedEvent: FigureChangeEvent = myNestedEvent
}

