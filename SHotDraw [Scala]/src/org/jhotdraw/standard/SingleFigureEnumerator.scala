/*
 * @(#)SingleFigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import org.jhotdraw.framework._

/**
 * An Enumeration that contains only a single Figures. An instance of this
 * enumeration can be used only once to retrieve the figure as the figure
 * is forgotten after the first retrieval.
 *
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
final class SingleFigureEnumerator(myInitialFigure: Figure) extends FigureEnumeration {
  reset

  /**
   * Returns true if the enumeration contains more elements; false
   * if its empty.
   */
  def hasNext: Boolean = mySingleFigure != null

  /**
   * Returns the next element of the enumeration. Calls to this
   * method will enumerate successive elements.
   * @exception java.util.NoSuchElementException If no more elements exist.
   */
  def next: Figure = {
    val returnFigure: Figure = mySingleFigure
    mySingleFigure = null
    returnFigure
  }

  /**
   * Reset the enumeration so it can be reused again. However, the
   * underlying collection might have changed since the last usage
   * so the elements and the order may vary when using an enumeration
   * which has been reset.
   */
  def reset {
    mySingleFigure = myInitialFigure
  }
  
  def iterator: Iterator[Figure] = List(mySingleFigure).iterator

  private var mySingleFigure: Figure = null
}

