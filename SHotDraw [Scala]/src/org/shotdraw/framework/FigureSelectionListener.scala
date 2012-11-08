/*
 * @(#)FigureSelectionListener.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

/**
 * Listener interested in DrawingView selection changes.
 *
 * @version <$CURRENT_VERSION$>
 */
trait FigureSelectionListener {
  /**
   * Sent when the figure selection has changed.
   * @param view DrawingView
   */
  def figureSelectionChanged(view: DrawingView)
}

