/*
 * @(#)AbstractHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import org.jhotdraw.framework._
import org.jhotdraw.util.Undoable

/**
 * AbstractHandle provides defaulf implementation for the Handle interface.
 *
 * @see org.jhotdraw.framework.Figure
 * @see org.jhotdraw.framework.Handle
 * @version <$CURRENT_VERSION$>
 */
object AbstractHandle {
  /**
   * The standard size of a handle.
   */
  final val HANDLESIZE: Int = 8
}

abstract class AbstractHandle(fOwner: Figure) extends Handle {
  import AbstractHandle._
  /**
   * @param x the x position where the interaction started
   * @param y the y position where the interaction started
   * @param view the handles container
   * @see org.jhotdraw.framework.Handle#invokeStart(int, int, org.jhotdraw.framework.DrawingView)
   */
  def invokeStart(x: Int, y: Int, view: DrawingView) {
    invokeStart(x, y, view.drawing)
  }

  /**
   * Tracks the start of the interaction. The default implementation
   * does nothing.
   * @param x the x position where the interaction started
   * @param y the y position where the interaction started
   * @see org.jhotdraw.framework.Handle#invokeStart(int, int, org.jhotdraw.framework.Drawing)
   * @deprecated As of version 4.1,
   *             use invokeStart(x, y, drawingView)
   */
  def invokeStart(x: Int, y: Int, drawing: Drawing) {}

  /**
   * Tracks a step of the interaction.
   * @param x the current x position
   * @param y the current y position
   * @param anchorX the x position where the interaction started
   * @param anchorY the y position where the interaction started
   * @see org.jhotdraw.framework.Handle#invokeStep(int, int, int, int, org.jhotdraw.framework.DrawingView)
   */
  def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    invokeStep(x - anchorX, y - anchorY, view.drawing)
  }

  /**
   * Tracks a step of the interaction.
   * @param dx x delta of this step
   * @param dy y delta of this step
   * @see org.jhotdraw.framework.Handle#invokeStep(int, int, org.jhotdraw.framework.Drawing)
   * @deprecated As of version 4.1,
   *             use invokeStep(x, y, anchorX, anchorY, drawingView)
   */
  def invokeStep(dx: Int, dy: Int, drawing: Drawing) {}

  /**
   * Tracks the end of the interaction.
   * @param x the current x position
   * @param y the current y position
   * @param anchorX the x position where the interaction started
   * @param anchorY the y position where the interaction started
   * @see org.jhotdraw.framework.Handle#invokeEnd(int, int, int, int, org.jhotdraw.framework.DrawingView)
   */
  def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    invokeEnd(x - anchorX, y - anchorY, view.drawing)
  }

  /**
   * Tracks the end of the interaction.
   * @see org.jhotdraw.framework.Handle#invokeEnd(int, int, org.jhotdraw.framework.Drawing)
   * @deprecated As of version 4.1,
   *             use invokeEnd(x, y, anchorX, anchorY, drawingView).
   */
  def invokeEnd(dx: Int, dy: Int, drawing: Drawing) {}

  /**
   * Gets the handle's owner.
   * @see org.jhotdraw.framework.Handle#owner()
   */
  def owner: Figure = fOwner

  /**
   * Gets the display box of the handle.
   * @see org.jhotdraw.framework.Handle#displayBox()
   */
  def displayBox: Rectangle = {
    val p: Point = locate
    new Rectangle(p.x - HANDLESIZE / 2, p.y - HANDLESIZE / 2, HANDLESIZE, HANDLESIZE)
  }

  /**
   * Tests if a point is contained in the handle.
   * @see org.jhotdraw.framework.Handle#containsPoint(int, int)
   */
  def containsPoint(x: Int, y: Int): Boolean = displayBox.contains(x, y)

  /**
   * Draws this handle.
   * @see org.jhotdraw.framework.Handle#draw(java.awt.Graphics)
   */
  def draw(g: Graphics) {
    val r: Rectangle = displayBox
    g.setColor(Color.white)
    g.fillRect(r.x, r.y, r.width, r.height)
    g.setColor(Color.black)
    g.drawRect(r.x, r.y, r.width, r.height)
  }

  /**
   * @see org.jhotdraw.framework.Handle#getUndoActivity()
   */
  def getUndoActivity: Undoable = myUndoableActivity

  /**
   * @see org.jhotdraw.framework.Handle#setUndoActivity(org.jhotdraw.util.Undoable)
   */
  def setUndoActivity(newUndoableActivity: Undoable) {
    myUndoableActivity = newUndoableActivity
  }

  /**
   * @see org.jhotdraw.framework.Handle#getCursor()
   */
  def getCursor: Cursor = new AWTCursor(java.awt.Cursor.DEFAULT_CURSOR)

  private var myUndoableActivity: Undoable = null
}

