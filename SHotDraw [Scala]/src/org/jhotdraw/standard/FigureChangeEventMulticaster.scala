/*
 * @(#)FigureChangeEventMulticaster.java
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
import java.awt._
import java.util._

/**
 * Manages a list of FigureChangeListeners to be notified of
 * specific FigureChangeEvents.
 *
 * @version <$CURRENT_VERSION$>
 */
object FigureChangeEventMulticaster {
  def add(a: FigureChangeListener, b: FigureChangeListener): FigureChangeListener = addInternal(a, b).asInstanceOf[FigureChangeListener]

  def remove(l: FigureChangeListener, oldl: FigureChangeListener): FigureChangeListener = removeInternal(l, oldl).asInstanceOf[FigureChangeListener]

  protected def addInternal(a: FigureChangeListener, b: FigureChangeListener): EventListener = {
    if (a == null) {
      return b
    }
    if (b == null) {
      return a
    }
    new FigureChangeEventMulticaster(a, b)
  }

  protected def removeInternal(l: EventListener, oldl: EventListener): EventListener = {
    if (l == oldl || l == null) null
    else if (l.isInstanceOf[FigureChangeEventMulticaster]) (l.asInstanceOf[FigureChangeEventMulticaster]).remove(oldl)
    else l
  }
}

class FigureChangeEventMulticaster(newListenerA: FigureChangeListener, newListenerB: FigureChangeListener) extends AWTEventMulticaster(newListenerA, newListenerB) with FigureChangeListener {
  import FigureChangeEventMulticaster._
  
  def figureInvalidated(e: FigureChangeEvent) {
    (a.asInstanceOf[FigureChangeListener]).figureInvalidated(e)
    (b.asInstanceOf[FigureChangeListener]).figureInvalidated(e)
  }

  def figureRequestRemove(e: FigureChangeEvent) {
    (a.asInstanceOf[FigureChangeListener]).figureRequestRemove(e)
    (b.asInstanceOf[FigureChangeListener]).figureRequestRemove(e)
  }

  def figureRequestUpdate(e: FigureChangeEvent) {
    (a.asInstanceOf[FigureChangeListener]).figureRequestUpdate(e)
    (b.asInstanceOf[FigureChangeListener]).figureRequestUpdate(e)
  }

  def figureChanged(e: FigureChangeEvent) {
    (a.asInstanceOf[FigureChangeListener]).figureChanged(e)
    (b.asInstanceOf[FigureChangeListener]).figureChanged(e)
  }

  def figureRemoved(e: FigureChangeEvent) {
    (a.asInstanceOf[FigureChangeListener]).figureRemoved(e)
    (b.asInstanceOf[FigureChangeListener]).figureRemoved(e)
  }

  protected override def remove(oldl: EventListener): EventListener = {
    if (oldl == a) {
      return b
    }
    if (oldl == b) {
      return a
    }
    val a2: EventListener = removeInternal(a, oldl)
    val b2: EventListener = removeInternal(b, oldl)
    if (a2 == a && b2 == b) {
      return this
    } else {
      addInternal(a2.asInstanceOf[FigureChangeListener], b2.asInstanceOf[FigureChangeListener])
    }
  }
}

