/*
 * @(#)LocatorHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.Point
import org.shotdraw.framework.Cursor
import org.shotdraw.framework.Figure
import org.shotdraw.framework.Locator

/**
 * A LocatorHandle implements a Handle by delegating the location requests to
 * a Locator object.
 *
 * @see LocatorHandle
 *
 * @version <$CURRENT_VERSION$>
 */
class LocatorHandle(owner: Figure, private val fLocator: Locator) extends AbstractHandle(owner) {

  /**
   * This should be cloned or it gives the receiver the opportunity to alter
   * our internal behavior.
   */
  def getLocator: Locator = fLocator

  /**
   * Locates the handle on the figure by forwarding the request
   * to its figure.
   */
  def locate: Point = fLocator.locate(owner)

  /**
   * @see org.shotdraw.framework.Handle#getCursor()
   */
  override def getCursor: Cursor = {
    var c = super.getCursor
    getLocator match {
      case rl: RelativeLocator =>
        if(rl == RelativeLocator.north) new AWTCursor(java.awt.Cursor.N_RESIZE_CURSOR)
        else if(rl == RelativeLocator.northEast) new AWTCursor(java.awt.Cursor.NE_RESIZE_CURSOR)
        else if(rl == RelativeLocator.east) new AWTCursor(java.awt.Cursor.E_RESIZE_CURSOR)
        else if(rl == RelativeLocator.southEast) new AWTCursor(java.awt.Cursor.SE_RESIZE_CURSOR)
        else if(rl == RelativeLocator.south) new AWTCursor(java.awt.Cursor.S_RESIZE_CURSOR)
        else if(rl == RelativeLocator.southWest) new AWTCursor(java.awt.Cursor.SW_RESIZE_CURSOR)
        else if(rl == RelativeLocator.west) new AWTCursor(java.awt.Cursor.W_RESIZE_CURSOR)
        else if(rl == RelativeLocator.northWest) new AWTCursor(java.awt.Cursor.NW_RESIZE_CURSOR)
        else c
      case _ => c 
    }
  }
}