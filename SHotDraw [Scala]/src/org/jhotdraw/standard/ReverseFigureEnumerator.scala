/*
 * @(#)ReverseFigureEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import org.jhotdraw.util.ReverseListEnumerator
import org.jhotdraw.framework._

/**
 * An Enumeration that enumerates a Collection of figures back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
final class ReverseFigureEnumerator(myInitialList: List[Figure]) extends FigureEnumeration {

  /**
   * Returns true if the enumeration contains more elements; false
   * if its empty.
   */
  def hasNext: Boolean = myIterator.hasNext

  /**
   * Returns the next element casted as a figure of the enumeration. Calls to this
   * method will enumerate successive elements.
   * @exception java.util.NoSuchElementException If no more elements exist.
   */
  def next: Figure = myIterator.next

  /**
   * Reset the enumeration so it can be reused again. However, the
   * underlying collection might have changed since the last usage
   * so the elements and the order may vary when using an enumeration
   * which has been reset.
   */
  def reset {
    myIterator = new ReverseListEnumerator(myInitialList)
  }
  
  override def iterator: Iterator[Figure] = myIterator

  private var myIterator: Iterator[Figure] = new ReverseListEnumerator(myInitialList)
}

