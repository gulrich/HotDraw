/*
 * @(#)ColorMap.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import java.awt.Color
import scala.collection.immutable.HashMap

/**
 * A map that is filled with some standard colors. The colors
 * can be looked up by name or index.
 *
 * @version <$CURRENT_VERSION$>
 */
object ColorMap {
  
  def color(name: String): Color = fMap(name)

  def isTransparent(col: Color): Boolean = col == color("None")
  
  def jMap: java.util.HashMap[String, Color] = {
    val map = new java.util.HashMap[String, Color]()
    fMap foreach { case (name, color) => map.put(name, color)}
    map
  }
  
  val fMap: Map[String, Color] = Map(("Black", Color.black), ("Blue", Color.blue), ("Green", Color.green), ("Red", Color.red), ("Pink", Color.pink), ("Magenta", Color.magenta), ("Orange", Color.orange), ("Yellow", Color.yellow), ("New Tan", new Color(0xEBC79E)), ("Aquamarine", new Color(0x70DB93)), ("Sea Green", new Color(0x238E68)), ("Dark Gray", Color.darkGray), ("Light Gray", Color.lightGray), ("White", Color.white), ("None", new Color(0xFFC79E)))
}


