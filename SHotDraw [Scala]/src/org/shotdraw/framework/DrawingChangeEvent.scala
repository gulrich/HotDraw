/*
 * @(#)DrawingChangeEvent.java
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
 * The event passed to DrawingChangeListeners.
 *
 * @version <$CURRENT_VERSION$>
 */
class DrawingChangeEvent(newSource: Drawing, myRectangle: Rectangle) extends EventObject(newSource) {

  /**
   * Gets the changed drawing
   */
  def getDrawing: Drawing = getSource match {
    case d: Drawing => d
    case _ => error("Drawing expected")
  }

  /**
   * Gets the changed rectangle
   */
  def getInvalidatedRectangle: Rectangle = myRectangle
  
}

