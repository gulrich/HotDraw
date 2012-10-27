/*
 * @(#)Helper.java
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
import java.awt._

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object Helper {
  def getDrawingView(container: Container): DrawingView = {
    var oldDrawingView: DrawingView = null
    val components: Array[Component] = container.getComponents
    for(comp <- components) { comp match {
      case dv: DrawingView => return dv
      case c: Container =>
        oldDrawingView = getDrawingView(c)
        return oldDrawingView
      case _ => 
    }}
    return null
  }

  def getDrawingView(component: Component): DrawingView = component match {
    case c: Container => getDrawingView(c)
    case dv: DrawingView => dv
    case _ => null
  }
}
