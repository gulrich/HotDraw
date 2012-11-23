/*
 * @(#)GroupHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.awt._
import org.shotdraw.framework._
import org.shotdraw.standard.NullHandle

/**
 * A Handle for a GroupFigure.
 *
 * @version <$CURRENT_VERSION$>
 */
final class GroupHandle(owner: Figure, locator: Locator) extends NullHandle(owner, locator) {
  /**
   * Draws the Group handle.
   */
  override def draw(g: Graphics) {
    val r = displayBox
    g.setColor(Color.black)
    g.drawRect(r.x, r.y, r.width, r.height)
    g.setColor(Color.white)
    g.fillRect(r.x+1, r.y+1, r.width-1, r.height-1)
  }
}

