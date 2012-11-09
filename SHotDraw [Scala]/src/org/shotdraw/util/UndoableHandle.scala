/*
 * @(#)UndoableHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import org.shotdraw.framework._
import org.shotdraw.framework.Drawing
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.framework.Handle

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class UndoableHandle extends Handle {
  /**
   * Constructor for <code>UndoableHandle</code>.
   * @param newWrappedHandle
   */
  def this(newWrappedHandle: Handle) {
    this()
    setWrappedHandle(newWrappedHandle)
  }

  /**
   * Constructor for <code>UndoableHandle</code>.
   * @param newWrappedHandle
   * @param newDrawingView
   * @deprecated use the constructor without the DrawingView instead
   */
  def this(newWrappedHandle: Handle, newDrawingView: DrawingView) {
    this()
    setWrappedHandle(newWrappedHandle)
    setDrawingView(newDrawingView)
  }

  /**
   * Locates the handle on the figure. The handle is drawn
   * centered around the returned point.
   * @see org.shotdraw.framework.Handle#locate()
   */
  def locate: Point = getWrappedHandle.locate

  /**
   * Tracks the start of the interaction. The default implementation
   * does nothing.
   * @param x the x position where the interaction started
   * @param y the y position where the interaction started
   * @param view the handles container
   * @see org.shotdraw.framework.Handle#invokeStart(int, int, org.shotdraw.framework.DrawingView)
   */
  def invokeStart(x: Int, y: Int, view: DrawingView) {
    getWrappedHandle.invokeStart(x, y, view)
  }

  /**
   * Tracks the start of the interaction. The default implementation
   * does nothing.
   * @param x the x position where the interaction started
   * @param y the y position where the interaction started
   * @see org.shotdraw.framework.Handle#invokeStart(int, int, org.shotdraw.framework.Drawing)
   * @deprecated As of version 4.1,
   *             use invokeStart(x, y, drawingView)
   */
  def invokeStart(x: Int, y: Int, drawing: Drawing) {
    getWrappedHandle.invokeStart(x, y, drawing)
  }

  /**
   * Tracks a step of the interaction.
   * @param x the current x position
   * @param y the current y position
   * @param anchorX the x position where the interaction started
   * @param anchorY the y position where the interaction started
   * @see org.shotdraw.framework.Handle#invokeStep(int, int, int, int, org.shotdraw.framework.DrawingView)
   */
  def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    getWrappedHandle.invokeStep(x, y, anchorX, anchorY, view)
  }

  /**
   * Tracks a step of the interaction.
   * @param dx x delta of this step
   * @param dy y delta of this step
   * @see org.shotdraw.framework.Handle#invokeStep(int, int, org.shotdraw.framework.Drawing)
   * @deprecated As of version 4.1,
   *             use invokeStep(x, y, anchorX, anchorY, drawingView)
   */
  def invokeStep(dx: Int, dy: Int, drawing: Drawing) {
    getWrappedHandle.invokeStep(dx, dy, drawing)
  }

  /**
   * Tracks the end of the interaction.
   * @param x the current x position
   * @param y the current y position
   * @param anchorX the x position where the interaction started
   * @param anchorY the y position where the interaction started
   * @see org.shotdraw.framework.Handle#invokeEnd(int, int, int, int, org.shotdraw.framework.DrawingView)
   */
  def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    getWrappedHandle.invokeEnd(x, y, anchorX, anchorY, view)
    val undoableActivity = getWrappedHandle.getUndoActivity
    if ((undoableActivity != null) && (undoableActivity.isUndoable)) {
      view.editor.getUndoManager.pushUndo(undoableActivity)
      view.editor.getUndoManager.clearRedos
    }
  }

  /**
   * Tracks the end of the interaction.
   * @see org.shotdraw.framework.Handle#invokeEnd(int, int, org.shotdraw.framework.Drawing)
   * @deprecated As of version 4.1,
   *             use invokeEnd(x, y, anchorX, anchorY, drawingView).
   */
  def invokeEnd(dx: Int, dy: Int, drawing: Drawing) {
    getWrappedHandle.invokeEnd(dx, dy, drawing)
  }

  /**
   * Gets the handle's owner.
   * @see org.shotdraw.framework.Handle#owner()
   */
  def owner: Figure = getWrappedHandle.owner

  /**
   * Gets the display box of the handle.
   * @see org.shotdraw.framework.Handle#displayBox()
   */
  def displayBox: Rectangle = getWrappedHandle.displayBox

  /**
   * Tests if a point is contained in the handle.
   * @see org.shotdraw.framework.Handle#containsPoint(int, int)
   */
  def containsPoint(x: Int, y: Int): Boolean = getWrappedHandle.containsPoint(x, y)

  /**
   * Draws this handle.
   * @see org.shotdraw.framework.Handle#draw(java.awt.Graphics)
   */
  def draw(g: Graphics) {
    getWrappedHandle.draw(g)
  }

  protected def setWrappedHandle(newWrappedHandle: Handle) {
    myWrappedHandle = newWrappedHandle
  }

  protected def getWrappedHandle: Handle = myWrappedHandle

  /**
   * @deprecated attribute not required anymore
   */
  def getDrawingView: DrawingView = myDrawingView

  /**
   * @deprecated attribute not required anymore
   */
  protected def setDrawingView(newDrawingView: DrawingView) {
    myDrawingView = newDrawingView
  }

  /**
   * @see org.shotdraw.framework.Handle#getUndoActivity()
   */
  def getUndoActivity: Undoable = new UndoableAdapter(getDrawingView)

  /**
   * @see org.shotdraw.framework.Handle#setUndoActivity(org.shotdraw.util.Undoable)
   */
  def setUndoActivity(newUndoableActivity: Undoable) {
  }

  /**
   * @see org.shotdraw.framework.Handle#getCursor()
   */
  def getCursor: Cursor = getWrappedHandle.getCursor

  private var myWrappedHandle: Handle = null
  private var myDrawingView: DrawingView = null
}

