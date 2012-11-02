/*
 * @(#)Locator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

import org.shotdraw.util.Storable
import java.awt._
import java.io.Serializable

/**
 * Locators can be used to locate a position on a figure.<p>
 *
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld034.htm>Strategy</a></b><br>
 * Locator encapsulates the strategy to locate a handle on a figure.
 *
 * @version <$CURRENT_VERSION$>
 */
abstract trait Locator extends Storable with Serializable with Cloneable {
  /**
   * Locates a position on the passed figure.
   * @return a point on the figure.
   */
  def locate(owner: Figure): Point
}


