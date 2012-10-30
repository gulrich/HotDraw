/*
 * @(#)DNDFigures.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.contrib.dnd

import java.awt.Point
import java.io.Serializable
import org.jhotdraw.framework.Figure

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object DNDFigures {
  private final val serialVersionUID: Long = 5964365585838817845L
}

class DNDFigures(fe: List[Figure], newOrigin: Point) extends Serializable {

  fe foreach { f => figures ::= f}

  def getFigures: List[Figure] = figures

  def getOrigin: Point = origin

  private var figures: List[Figure] = List[Figure]()
  private var origin: Point = null
}