/*
 * @(#)CommandListener.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import java.util.EventObject

/**
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
abstract trait CommandListener {
  def commandExecuted(commandEvent: EventObject)

  def commandExecutable(commandEvent: EventObject)

  def commandNotExecutable(commandEvent: EventObject)
}

