/*
 * @(#)OrderFigureElement.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import org.jhotdraw.framework.Figure

/**
 * @author WMG (INIT Copyright (C) 2000 All rights reserved)
 * @version <$CURRENT_VERSION$>
 */
class OrderedFigureElement(_theFigure: Figure, _nZ: Int) extends Comparable[OrderedFigureElement] {

  def getFigure: Figure = _theFigure

  def getZValue: Int = _nZ

  def compareTo(o: OrderedFigureElement): Int = {
    val ofe: OrderedFigureElement = o.asInstanceOf[OrderedFigureElement]
    if (_nZ == ofe.getZValue) {
      return 0
    }
    if (_nZ > ofe.getZValue) {
      return 1
    }
    -1
  }
}

