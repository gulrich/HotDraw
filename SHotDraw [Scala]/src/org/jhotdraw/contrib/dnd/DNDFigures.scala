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
import org.jhotdraw.framework.FigureEnumeration
import org.jhotdraw.standard.FigureEnumerator

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object DNDFigures {
  private final val serialVersionUID: Long = 5964365585838817845L
}

class DNDFigures(fe: FigureEnumeration, newOrigin: Point) extends Serializable {

  fe foreach { f => figures ::= f}

  def getFigures: FigureEnumeration = new FigureEnumerator(figures)

  def getOrigin: Point = origin

  private var figures: List[Figure] = List[Figure]()
  private var origin: Point = null
}