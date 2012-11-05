/*
 * @(#) JHotDraw.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.application

/**
 * A very basic example for demonstraing JHotDraw's capabilities. It contains only
 * a few additional tools apart from the selection tool already provided by its superclass.
 * It uses only a single document interface (SDI) and not a multiple document interface (MDI)
 * because the applicateion frame is derived DrawApplication rather than MDI_DrawApplication.
 * To enable MDI for this application, NothingApp must inherit from MDI_DrawApplication
 * and be recompiled.
 *
 * @version <$CURRENT_VERSION$>
 */

object JHotDraw extends DrawApplication {
  def main(args: Array[String]) {
    val window: DrawApplication = JHotDraw
    window.open
  }
}

