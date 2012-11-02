/*
 * @(#)UndoableTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import org.shotdraw.framework._
import org.shotdraw.standard.AbstractTool
import java.awt.event.MouseEvent
import java.awt.event.KeyEvent
import java.util.EventObject

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class UndoableTool extends Tool with ToolListener {
  def this(newWrappedTool: Tool) {
    this()
    setEventDispatcher(createEventDispatcher)
    setWrappedTool(newWrappedTool)
    getWrappedTool.addToolListener(this)
  }

  /**
   * Activates the tool for the given view. This method is called
   * whenever the user switches to this tool. Use this method to
   * reinitialize a tool.
   */
  def activate {
    getWrappedTool.activate
  }

  /**
   * Deactivates the tool. This method is called whenever the user
   * switches to another tool. Use this method to do some clean-up
   * when the tool is switched. Subclassers should always call
   * super.deactivate.
   */
  def deactivate {
    getWrappedTool.deactivate
    val undoActivity: Undoable = getWrappedTool.getUndoActivity
    if ((undoActivity != null) && (undoActivity.isUndoable)) {
      editor.getUndoManager.pushUndo(undoActivity)
      editor.getUndoManager.clearRedos
      editor.figureSelectionChanged(getActiveView)
    }
  }

  /**
   * Handles mouse down events in the drawing view.
   */
  def mouseDown(e: MouseEvent, x: Int, y: Int) {getWrappedTool.mouseDown(e, x, y)}

  /**
   * Handles mouse drag events in the drawing view.
   */
  def mouseDrag(e: MouseEvent, x: Int, y: Int) {getWrappedTool.mouseDrag(e, x, y)}

  /**
   * Handles mouse up in the drawing view. After the mouse button
   * has been released, the associated tool activity can be undone
   * if the associated tool supports the undo operation from the Undoable interface.
   *
   * @see org.shotdraw.util.Undoable
   */
  def mouseUp(e: MouseEvent, x: Int, y: Int) {getWrappedTool.mouseUp(e, x, y)}

  /**
   * Handles mouse moves (if the mouse button is up).
   */
  def mouseMove(evt: MouseEvent, x: Int, y: Int) {getWrappedTool.mouseMove(evt, x, y)}

  /**
   * Handles key down events in the drawing view.
   */
  def keyDown(evt: KeyEvent, key: Int) {getWrappedTool.keyDown(evt, key)}

  def isUsable: Boolean = getWrappedTool.isUsable

  def isActive: Boolean = editor.tool == this

  def isEnabled: Boolean = getWrappedTool.isEnabled

  def setUsable(newIsUsable: Boolean) {getWrappedTool.setUsable(newIsUsable)}

  def setEnabled(newIsEnabled: Boolean) {getWrappedTool.setEnabled(newIsEnabled)}

  protected def setWrappedTool(newWrappedTool: Tool) {myWrappedTool = newWrappedTool}

  protected def getWrappedTool: Tool = myWrappedTool

  def editor: DrawingEditor = getWrappedTool.editor

  def view: DrawingView = editor.view

  def getUndoActivity: Undoable = new UndoableAdapter(view)

  def setUndoActivity(newUndoableActivity: Undoable) {}

  def toolUsable(toolEvent: EventObject) {getEventDispatcher.fireToolUsableEvent}

  def toolUnusable(toolEvent: EventObject) {getEventDispatcher.fireToolUnusableEvent}

  def toolActivated(toolEvent: EventObject) {getEventDispatcher.fireToolActivatedEvent}

  def toolDeactivated(toolEvent: EventObject) {getEventDispatcher.fireToolDeactivatedEvent}

  def toolEnabled(toolEvent: EventObject) {getEventDispatcher.fireToolEnabledEvent}

  def toolDisabled(toolEvent: EventObject) {getEventDispatcher.fireToolDisabledEvent}

  def addToolListener(newToolListener: ToolListener) {getEventDispatcher.addToolListener(newToolListener)}

  def removeToolListener(oldToolListener: ToolListener) {getEventDispatcher.removeToolListener(oldToolListener)}

  private def setEventDispatcher(newEventDispatcher: AbstractTool.EventDispatcher) {myEventDispatcher = newEventDispatcher}

  protected def getEventDispatcher: AbstractTool.EventDispatcher = myEventDispatcher

  def createEventDispatcher: AbstractTool.EventDispatcher = new AbstractTool.EventDispatcher(this)

  def getActiveView: DrawingView = editor.view

  private var myWrappedTool: Tool = null
  private var myEventDispatcher: AbstractTool.EventDispatcher = null
}

