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

package org.jhotdraw.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jhotdraw.framework.Figure;

/**
 * An Enumeration that enumerates a list back (size-1) to front (0).
 *
 * @version <$CURRENT_VERSION$>
 */
public class ReverseListEnumerator implements Iterator<Figure> {

	private List<Figure> myList;
	private int count;

	public ReverseListEnumerator(List<Figure> l) {
		myList = l;
		count = myList.size() - 1;
	}

	public boolean hasNext() {
		return count >= 0;
	}

	public Figure next() {
		if (count >= 0) {
			return myList.get(count--);
		}
		throw new NoSuchElementException("ReverseListEnumerator");
	}

	public void remove() {
		myList.remove(count);
		count--;
	}
}
