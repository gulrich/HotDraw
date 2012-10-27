/*
 * @(#)FigureEnumerator.java
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
import org.jhotdraw.framework.FigureEnumeration

/**
 * An Enumeration for a Collection of Figures.
 *
 * @version <$CURRENT_VERSION$>
 */
final object FigureEnumerator {
  def getEmptyEnumeration: FigureEnumeration = singletonEmptyEnumerator

  private var singletonEmptyEnumerator: FigureEnumerator = new FigureEnumerator(List[Figure]())
}

final class FigureEnumerator(val myInitialCollection: Collection[Figure]) extends FigureEnumeration {
  reset

  /**
   * Returns true if the enumeration contains more elements; false
   * if its empty.
   */
  def hasNext: Boolean = myIterator.hasNext

  /**
   * Returns the next element of the enumeration. Calls to this
   * method will enumerate successive elements.
   * @exception java.util.NoSuchElementException If no more elements exist.
   */
  def next = myIterator.next

  /**
   * Reset the enumeration so it can be reused again. However, the
   * underlying collection might have changed since the last usage
   * so the elements and the order may vary when using an enumeration
   * which has been reset.
   */
  def reset {
    myIterator = myInitialCollection.iterator
  }

  def addAll(fe: FigureEnumerator): FigureEnumerator = {
    var tmp: List[Figure] = myInitialCollection.toList
    fe.myInitialCollection foreach { f =>
      tmp ::= f
    }
    new FigureEnumerator(tmp)
  }
  
  def iterator: Iterator[Figure] = myIterator

  private var myIterator: Iterator[Figure] = myInitialCollection.iterator
}

