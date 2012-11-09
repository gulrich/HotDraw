/*
 * @(#)StandardDrawing.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.Point
import java.awt.Rectangle
import java.io.IOException
import java.io.ObjectInputStream
import org.shotdraw.framework.Drawing
import org.shotdraw.framework.DrawingChangeEvent
import org.shotdraw.framework.DrawingChangeListener
import org.shotdraw.framework.Figure
import org.shotdraw.framework.FigureChangeEvent
import org.shotdraw.framework.FigureChangeListener
import org.shotdraw.framework.Handle
import scala.collection.mutable.ArrayBuffer

/**
 * The standard implementation of the Drawing interface.
 *
 * @see Drawing
 *
 * @version <$CURRENT_VERSION$>
 */
class StandardDrawing extends CompositeFigure with Drawing {
  
  /**
   * the registered listeners
   */
  @transient
  private var fListeners: ArrayBuffer[DrawingChangeListener] = ArrayBuffer()
  /**
   * boolean that serves as a condition variable
   * to lock the access to the drawing.
   * The lock is recursive and we keep track of the current
   * lock holder.
   */
  @transient
  private var fDrawingLockHolder: Thread = null
  private var myTitle: String = null
  init(new Rectangle(-500, -500, 2000, 2000))

  /**
   * Adds a listener for this drawing.
   */
  def addDrawingChangeListener(listener: DrawingChangeListener) {
    if (fListeners == null) {
      fListeners = ArrayBuffer[DrawingChangeListener]()
    }
    fListeners += listener
  }

  /**
   * Removes a listener from this drawing.
   */
  def removeDrawingChangeListener(listener: DrawingChangeListener) {
    fListeners = fListeners diff List(listener)
  }

  /**
   * Gets an enumeration with all listener for this drawing.
   */
  def drawingChangeListeners: Iterator[DrawingChangeListener] = fListeners.iterator

  /**
   * Removes a figure from the figure list, but
   * doesn't release it. Use this method to temporarily
   * manipulate a figure outside of the drawing.
   *
   * @param figure that is part of the drawing and should be added
   */
  override def orphan(figure: Figure): Figure = {
    val orphanedFigure = super.orphan(figure)
    if (orphanedFigure.listener != null) {
      val rect = invalidateRectangle(displayBox)
      orphanedFigure.listener.figureRequestRemove(new FigureChangeEvent(orphanedFigure, rect))
    }
    orphanedFigure
  }

  override def add(figure: Figure): Figure = {
    val addedFigure = super.add(figure)
    if (addedFigure.listener != null) {
      val rect = invalidateRectangle(displayBox)
      addedFigure.listener.figureRequestUpdate(new FigureChangeEvent(figure, rect))
      addedFigure
    } else addedFigure
  }

  /**
   * Invalidates a rectangle and merges it with the
   * existing damaged area.
   * @see FigureChangeListener
   */
  override def figureInvalidated(e: FigureChangeEvent) {
    if (fListeners != null) {
      fListeners foreach {_.drawingInvalidated(new DrawingChangeEvent(this, e.getInvalidatedRectangle))}
    }
  }

  /**
   * Forces an update of the drawing change listeners.
   */
  def fireDrawingTitleChanged {
    if (fListeners != null) {
      fListeners foreach {_.drawingTitleChanged(new DrawingChangeEvent(this, null))}
    }
  }

  /**
   * Forces an update of the drawing change listeners.
   */
  override def figureRequestUpdate(e: FigureChangeEvent) {
    if (fListeners != null) {
      fListeners foreach {_.drawingRequestUpdate(new DrawingChangeEvent(this, null))}
    }
  }

  /**
   * Return's the figure's handles. This is only used when a drawing
   * is nested inside another drawing.
   */
  def handles: Seq[Handle] = {
    new NullHandle(this, RelativeLocator.northWest) ::
        new NullHandle(this, RelativeLocator.northEast) ::
        new NullHandle(this, RelativeLocator.southWest) ::
        new NullHandle(this, RelativeLocator.southEast) :: Nil
  }

  /**
   * Gets the display box. This is the union of all figures.
   */
  def displayBox: Rectangle = {
    val r = new Rectangle
    if (fFigures.size > 0) {
      figures foreach {f => r.add(f.displayBox)}
    }
    r
  }

  def basicDisplayBox(p1: Point, p2: Point) {}

  /**
   * Acquires the drawing lock.
   */
  def lock: Unit  = synchronized {
    val current = Thread.currentThread
    if (fDrawingLockHolder == current) {
      return
    }
    while (fDrawingLockHolder != null) {
      try {
        wait
      } catch {
        case ex: InterruptedException => {
        }
      }
    }
    fDrawingLockHolder = current
  }

  /**
   * Releases the drawing lock.
   */
  def unlock: Unit = synchronized {
    if (fDrawingLockHolder != null) {
      fDrawingLockHolder = null
      notify
    }
  }

  private def readObject(s: ObjectInputStream) {
    s.defaultReadObject
    fListeners = ArrayBuffer[DrawingChangeListener]()
  }

  def getTitle: String = myTitle

  def setTitle(newTitle: String) {
    myTitle = newTitle
  }
}

