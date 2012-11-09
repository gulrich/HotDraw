/*
 * @(#)Handle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import org.shotdraw.util.Undoable

/**
 * Handles are used to change a figure by direct manipulation.
 * Handles know their owning figure and they provide methods to
 * locate the handle on the figure and to track changes.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld004.htm>Adapter</a></b><br>
 * Handles adapt the operations to manipulate a figure to a common interface.
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */
object Handle {
  val HANDLESIZE = 8
}

trait Handle {
  /**
   * Locates the handle on the figure. The handle is drawn
   * centered around the returned point.
   */
  def locate: Point

  /**
   * Tracks the start of the interaction. The default implementation
   * does nothing.
   * @param x the x position where the interaction started
   * @param y the y position where the interaction started
   * @param view the handles container
   */
  def invokeStart(x: Int, y: Int, view: DrawingView)

  /**
   * Tracks the start of the interaction. The default implementation
   * does nothing.
   * @param x the x position where the interaction started
   * @param y the y position where the interaction started
   * @deprecated As of version 4.1, use invokeStart(x, y, drawingView)
   */
  def invokeStart(x: Int, y: Int, drawing: Drawing)

  /**
   * Tracks a step of the interaction.
   * @param x the current x position
   * @param y the current y position
   * @param anchorX the x position where the interaction started
   * @param anchorY the y position where the interaction started
   */
  def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView)

  /**
   * Tracks a step of the interaction.
   * @param dx x delta of this step
   * @param dy y delta of this step
   * @deprecated As of version 4.1,
   *             use invokeStep(x, y, anchorX, anchorY, drawingView)
   */
  def invokeStep(dx: Int, dy: Int, drawing: Drawing)

  /**
   * Tracks the end of the interaction.
   * @param x the current x position
   * @param y the current y position
   * @param anchorX the x position where the interaction started
   * @param anchorY the y position where the interaction started
   */
  def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView)

  /**
   * Tracks the end of the interaction.
   * @deprecated As of version 4.1,
   *             use invokeEnd(x, y, anchorX, anchorY, drawingView).
   */
  def invokeEnd(dx: Int, dy: Int, drawing: Drawing)

  /**
   * Gets the handle's owner.
   */
  def owner: Figure

  /**
   * Gets the display box of the handle.
   */
  def displayBox: Rectangle

  /**
   * Tests if a point is contained in the handle.
   */
  def containsPoint(x: Int, y: Int): Boolean

  /**
   * Draws this handle.
   */
  def draw(g: Graphics)

  /**
   * Returns an Undoable to be used by the Undo/Redo infrastructure.
   * @return Undoable
   */
  def getUndoActivity: Undoable

  /**
   * Sets an Undoable to be used by the Undo/Redo infrastructure.
   * @param newUndoableActivity
   */
  def setUndoActivity(newUndoableActivity: Undoable)

  /**
   * Returns the preferred Cursor for this Handle.
   * @return Cursor
   */
  def getCursor: Cursor
}

