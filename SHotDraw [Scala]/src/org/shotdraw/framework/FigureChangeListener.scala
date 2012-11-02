/*
 * @(#)FigureChangeListener.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

import java.util.EventListener

/**
 * Listener interested in Figure changes.
 *
 * @version <$CURRENT_VERSION$>
 */
abstract trait FigureChangeListener extends EventListener {
  /**
   * Sent when an area is invalid
   */
  def figureInvalidated(e: FigureChangeEvent)

  /**
   * Sent when a figure changed
   */
  def figureChanged(e: FigureChangeEvent)

  /**
   * Sent when a figure was removed
   */
  def figureRemoved(e: FigureChangeEvent)

  /**
   * Sent when requesting to remove a figure.
   */
  def figureRequestRemove(e: FigureChangeEvent)

  /**
   * Sent when an update should happen.
   *
   */
  def figureRequestUpdate(e: FigureChangeEvent)
}

