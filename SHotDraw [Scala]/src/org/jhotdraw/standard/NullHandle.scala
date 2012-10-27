/*
 * @(#)NullHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import java.awt._
import org.jhotdraw.framework._

/**
 * A handle that doesn't change the owned figure. Its only purpose is
 * to show feedback that a figure is selected.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b>NullObject</b><br>
 * NullObject enables to treat handles that don't do
 * anything in the same way as other handles.
 *
 * @version <$CURRENT_VERSION$>
 */
class NullHandle(owner: Figure, locator: Locator) extends LocatorHandle(owner, locator) {

  /**
   * Draws the NullHandle. NullHandles are drawn as a
   * red framed rectangle.
   */
  override def draw(g: Graphics) {
    val r: Rectangle = displayBox
    g.setColor(Color.black)
    g.drawRect(r.x, r.y, r.width, r.height)
  }

  /**
   * The handle's locator.
   */
  protected var fLocator: Locator = null
}

