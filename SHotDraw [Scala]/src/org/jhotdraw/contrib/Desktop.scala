/*
 * @(#)Desktop.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.contrib

import org.jhotdraw.framework.DrawingView

/**
 * @author  C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object Desktop {
  final val PRIMARY: Int = 0
  final val SECONDARY: Int = 1
  final val TERTIARY: Int = 2
}

abstract trait Desktop {
  /**
   * For those absent minded components that were not paying attention to the
   * listener events.
   */
  def getActiveDrawingView: DrawingView

  def addToDesktop(dv: DrawingView, location: Int)

  def removeFromDesktop(dv: DrawingView, location: Int)

  def removeAllFromDesktop(location: Int)

  def getAllFromDesktop(location: Int): List[DrawingView]

  def updateTitle(newDrawingTitle: String)

  def addDesktopListener(dpl: DesktopListener)

  def removeDesktopListener(dpl: DesktopListener)
}