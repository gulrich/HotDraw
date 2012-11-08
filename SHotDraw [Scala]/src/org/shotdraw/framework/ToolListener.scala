/*
 * @(#)ToolListener.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

import java.util.EventObject

/**
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
trait ToolListener {
  def toolEnabled(toolEvent: EventObject)

  def toolDisabled(toolEvent: EventObject)

  def toolUsable(toolEvent: EventObject)

  def toolUnusable(toolEvent: EventObject)

  def toolActivated(toolEvent: EventObject)

  def toolDeactivated(toolEvent: EventObject)
}

