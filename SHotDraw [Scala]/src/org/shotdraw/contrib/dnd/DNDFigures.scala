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
package org.shotdraw.contrib.dnd

import java.awt.Point
import java.io.Serializable
import org.shotdraw.framework.Figure
import scala.collection.mutable.ArrayBuffer

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class DNDFigures(fe: List[Figure], newOrigin: Point) extends Serializable {

  fe foreach { f => figures += f}

  def getFigures: ArrayBuffer[Figure] = figures

  def getOrigin: Point = origin

  private var figures = ArrayBuffer[Figure]()
  private var origin: Point = null
}