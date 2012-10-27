/*
 * @(#)DrawingChangeListener.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.framework

/**
 * Listener interested in Drawing changes.
 *
 * @version <$CURRENT_VERSION$>
 */
abstract trait DrawingChangeListener {
  /**
   * Sent when an area is invalid
   */
  def drawingInvalidated(e: DrawingChangeEvent)

  /**
   * Sent when the drawing Title has changed
   */
  def drawingTitleChanged(e: DrawingChangeEvent)

  /**
   * Sent when the drawing wants to be refreshed
   */
  def drawingRequestUpdate(e: DrawingChangeEvent)
}

