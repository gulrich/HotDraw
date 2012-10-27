/*
 * @(#)Tool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.framework

import org.jhotdraw.util.Undoable
import java.awt.event.MouseEvent
import java.awt.event.KeyEvent

/**
 * A tool defines a mode of the drawing view. All input events
 * targeted to the drawing view are forwarded to its current tool.<p>
 * Tools inform their editor when they are done with an interaction
 * by calling the editor's toolDone() method.
 * The Tools are created once and reused. They
 * are initialized/deinitialized with activate()/deactivate().
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld032.htm>State</a></b><br>
 * Tool plays the role of the State. In encapsulates all state
 * specific behavior. DrawingView plays the role of the StateContext.
 *
 * A tool can be in the following states: <br>
 * disabled<->enabled[unusable<->usable[active<->inactive]]
 * <->always_usable[active<->inactive]<->disabled
 * <br> where each square bracket indicates a state nesting level
 * and arrows possible state transitions.
 * Unusable tools are always inactive as well and disabled tools
 * are always unusable as well.
 * State changes are propagated to registered ToolListeners.
 *
 * @see DrawingView
 *
 * @version <$CURRENT_VERSION$>
 */
abstract trait Tool {
  /**
   * An active tool is the currently selected tool in the
   * DrawingView. A tool can be activated/deactivated
   * by calling the activate()/deactivate() method.
   *
   * @return true if the tool is the selected tool in the DrawingView, false otherwise
   * @see #isEnabled
   * @see #isUsable
   */
  def isActive: Boolean

  /**
   * Activates the tool for the given view. This method is called
   * whenever the user switches to this tool. Use this method to
   * reinitialize a tool.
   * Note, a valid view must be present in order for the tool to accept activation
   */
  def activate

  /**
   * Deactivates the tool. This method is called whenever the user
   * switches to another tool. Use this method to do some clean-up
   * when the tool is switched. Subclassers should always call
   * super.deactivate.
   */
  def deactivate

  /**
   * Handles mouse down events in the drawing view.
   */
  def mouseDown(e: MouseEvent, x: Int, y: Int)

  /**
   * Handles mouse drag events in the drawing view.
   */
  def mouseDrag(e: MouseEvent, x: Int, y: Int)

  /**
   * Handles mouse up in the drawing view.
   */
  def mouseUp(e: MouseEvent, x: Int, y: Int)

  /**
   * Handles mouse moves (if the mouse button is up).
   */
  def mouseMove(evt: MouseEvent, x: Int, y: Int)

  /**
   * Handles key down events in the drawing view.
   */
  def keyDown(evt: KeyEvent, key: Int)

  /**
   * A tool must be enabled in order to use it and to activate/deactivate it.
   * Typically, the program enables or disables a tool.
   *
   * @see #isUsable
   * @see #isActive
   */
  def isEnabled: Boolean

  def setEnabled(enableUsableCheck: Boolean)

  /**
   * A usable tool is a enabled and either active or inactive.
   * Typically, the tool should be able to determine itself whether it is
   * usable or not.
   *
   * @see #isEnabled
   * @see #isUsable
   */
  def isUsable: Boolean

  def setUsable(newIsUsable: Boolean)

  def editor: DrawingEditor

  def getUndoActivity: Undoable

  def setUndoActivity(newUndoableActivity: Undoable)

  def addToolListener(newToolListener: ToolListener)

  def removeToolListener(oldToolListener: ToolListener)
}

