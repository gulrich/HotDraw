/*
 * @(#)SimpleUpdateStrategy.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt._
import org.shotdraw.framework._

/**
 * The SimpleUpdateStrategy implements an update
 * strategy that directly redraws a DrawingView.
 *
 * @see DrawingView
 *
 * @version <$CURRENT_VERSION$>
 */
class SimpleUpdateStrategy extends Painter {
  /**
   * Draws the view contents.
   */
  def draw(g: Graphics, view: DrawingView) {
    view.drawAll(g)
  }
}

