/*
 * @(#)DesktopEventService.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.contrib

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
abstract trait DesktopListener {
  def drawingViewAdded(dpe: DesktopEvent)

  def drawingViewRemoved(dpe: DesktopEvent)

  def drawingViewSelected(dpe: DesktopEvent)
}