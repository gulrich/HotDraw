/*
 * @(#)PointConstrainer.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

import java.awt._

/**
 * Interface to constrain a Point. This can be used to implement
 * different kinds of grids.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld034.htm>Strategy</a></b><br>
 * DrawingView is the StrategyContext.<br>
 *
 * @see DrawingView
 *
 * @version <$CURRENT_VERSION$>
 */
trait PointConstrainer {
  /**
   * Constrains the given point.
   * @return constrained point.
   */
  def constrainPoint(p: Point): Point

  /**
   * Gets the x offset to move an object.
   */
  def getStepX: Int

  /**
   * Gets the y offset to move an object.
   */
  def getStepY: Int
}

