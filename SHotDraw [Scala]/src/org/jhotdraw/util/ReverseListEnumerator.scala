/*
 * @(#)ReverseListEnumerator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import java.util.NoSuchElementException
import org.jhotdraw.framework.Figure

/**
 * An Enumeration that enumerates a list back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
class ReverseListEnumerator(var myList: List[Figure]) extends Iterator[Figure] {

  def hasNext: Boolean = myList.size > 0

  def next: Figure = myList.lastOption match {
    case Some(fig) =>
      myList = myList.init
      fig
    case None => error("ReverseListEnumerator: No such element exception")
  }

  def remove {
    myList = myList.init
  }
}

