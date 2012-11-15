/*
 * @(#)AbstractTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.EventObject
import java.util.Iterator
import org.shotdraw.framework.Drawing
import org.shotdraw.framework.DrawingEditor
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Tool
import org.shotdraw.framework.ToolListener
import org.shotdraw.framework.ViewChangeListener
import org.shotdraw.util.Undoable
import scala.collection.mutable.ArrayBuffer

/**
 * Default implementation support for Tools.
 *
 * @see DrawingView
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */
object AbstractTool {

  class EventDispatcher(myObservedTool: Tool) {

    def fireToolUsableEvent() {
      myRegisteredListeners foreach { e => e.toolUsable(new EventObject(myObservedTool)) }
    }

    def fireToolUnusableEvent() {
      myRegisteredListeners foreach { e => e.toolUnusable(new EventObject(myObservedTool)) }
    }

    def fireToolActivatedEvent() {
      myRegisteredListeners foreach { e => e.toolActivated(new EventObject(myObservedTool)) }
    }

    def fireToolDeactivatedEvent() {
      myRegisteredListeners foreach { e => e.toolDeactivated(new EventObject(myObservedTool)) }
    }

    def fireToolEnabledEvent() {
      myRegisteredListeners foreach { e => e.toolEnabled(new EventObject(myObservedTool)) }
    }

    def fireToolDisabledEvent() {
      myRegisteredListeners foreach { e => e.toolDisabled(new EventObject(myObservedTool)) }
    }

    def addToolListener(newToolListener: ToolListener) {
      if (!myRegisteredListeners.contains(newToolListener)) {
        myRegisteredListeners += newToolListener
      }
    }

    def removeToolListener(oldToolListener: ToolListener) {
      if (myRegisteredListeners.contains(oldToolListener)) {
        myRegisteredListeners = myRegisteredListeners diff List(oldToolListener)
      }
    }

    private var myRegisteredListeners = ArrayBuffer[ToolListener]()
  }

}

class AbstractTool(newDrawingEditor: DrawingEditor) extends Tool {
  
  
  private var myDrawingEditor: DrawingEditor = null
  /**
   * The position of the initial mouse down.
   * The anchor point is usually the first mouse click performed with this tool.
   */
  private var myAnchorX = 0
  private var myAnchorY = 0
  /**
   * A tool can have a drawing view on which it operates
   * independingly of the currently active drawing view.
   * For example, if a tool can be used
   */
  private var myDrawingView: DrawingView = null
  private var myUndoActivity: Undoable = null
  private var myEventDispatcher: AbstractTool.EventDispatcher = null
  private var myIsUsable = false
  /**
   * Flag to indicate whether to perform usable checks or not
   */
  private var myIsEnabled = false
  
  setEditor(newDrawingEditor)
  setEventDispatcher(createEventDispatcher)
  setEnabled(true)
  checkUsable()
  editor.addViewChangeListener(createViewChangeListener)

  /**
   * Activates the tool for use on the given view. This method is called
   * whenever the user switches to this tool. Use this method to
   * reinitialize a tool.
   * Since tools will be disabled unless it is useable, there will always
   * be an active view when this is called. based on isUsable()
   * Tool should never be activated if the view is null.
   * Ideally, the dditor should take care of that.
   */
  def activate() {
    if (getActiveView != null) {
      getActiveView.clearSelection()
      getActiveView.checkDamage()
      getEventDispatcher.fireToolActivatedEvent()
    }
  }

  /**
   * Deactivates the tool. This method is called whenever the user
   * switches to another tool. Use this method to do some clean-up
   * when the tool is switched. Subclassers should always call
   * super.deactivate.
   * An inactive tool should never be deactivated
   */
  def deactivate() {
    if (isActive) {
      if (getActiveView != null) {
        getActiveView.setCursor(new AWTCursor(java.awt.Cursor.DEFAULT_CURSOR))
      }
      getEventDispatcher.fireToolDeactivatedEvent()
    }
  }

  /**
   * Fired when the selected view changes.
   * Subclasses should always call super.  ViewSelectionChanged() this allows
   * the tools state to be updated and referenced to the new view.
   */
  protected def viewSelectionChanged(oldView: DrawingView, newView: DrawingView) {
    if (isActive) {
      deactivate()
      activate()
    }
    checkUsable()
  }

  /**
   * Sent when a new view is created
   */
  protected def viewCreated(view: DrawingView) {}

  /**
   * Send when an existing view is about to be destroyed.
   */
  protected def viewDestroying(view: DrawingView) {}

  /**
   * Handles mouse down events in the drawing view.
   */
  def mouseDown(e: MouseEvent, x: Int, y: Int) {
    setAnchorX(x)
    setAnchorY(y)
    setView(e.getSource.asInstanceOf[DrawingView])
  }

  /**
   * Handles mouse drag events in the drawing view.
   */
  def mouseDrag(e: MouseEvent, x: Int, y: Int) {}

  /**
   * Handles mouse up in the drawing view.
   */
  def mouseUp(e: MouseEvent, x: Int, y: Int) {}

  /**
   * Handles mouse moves (if the mouse button is up).
   */
  def mouseMove(evt: MouseEvent, x: Int, y: Int) {}

  /**
   * Handles key down events in the drawing view.
   */
  def keyDown(evt: KeyEvent, key: Int) {}

  /**
   * Gets the tool's drawing.
   */
  def drawing: Drawing = view.drawing

  def getActiveDrawing: Drawing = getActiveView.drawing

  /**
   * Gets the tool's editor.
   */
  def editor: DrawingEditor = myDrawingEditor

  protected def setEditor(newDrawingEditor: DrawingEditor) {
    myDrawingEditor = newDrawingEditor
  }

  /**
   * Gets the tool's view (convienence method).
   */
  def view: DrawingView = myDrawingView

  protected def setView(newDrawingView: DrawingView) {
    myDrawingView = newDrawingView
  }

  def getActiveView: DrawingView = editor.view

  /**
   * Tests if the tool can be used or "executed."
   */
  def isUsable: Boolean = isEnabled && myIsUsable

  def setUsable(newIsUsable: Boolean) {
    if (isUsable != newIsUsable) {
      myIsUsable = newIsUsable
      if (isUsable) {
        getEventDispatcher.fireToolUsableEvent()
      } else {
        getEventDispatcher.fireToolUnusableEvent()
      }
    }
  }

  def setEnabled(newIsEnabled: Boolean) {
    if (isEnabled != newIsEnabled) {
      myIsEnabled = newIsEnabled
      if (isEnabled) {
        getEventDispatcher.fireToolEnabledEvent()
      } else {
        getEventDispatcher.fireToolDisabledEvent()
        setUsable(false)
        deactivate()
      }
    }
  }

  def isEnabled: Boolean = myIsEnabled

  /**
   * The anchor point is usually the first mouse click performed with this tool.
   * @see #mouseDown
   */
  protected def setAnchorX(newAnchorX: Int) {
    myAnchorX = newAnchorX
  }

  /**
   * The anchor point is usually the first mouse click performed with this tool.
   *
   * @return the anchor X coordinate for the interaction
   * @see #mouseDown
   */
  protected def getAnchorX: Int = myAnchorX

  /**
   * The anchor point is usually the first mouse click performed with this tool.
   * @see #mouseDown
   */
  protected def setAnchorY(newAnchorY: Int) {
    myAnchorY = newAnchorY
  }

  /**
   * The anchor point is usually the first mouse click performed with this tool.
   *
   * @return the anchor Y coordinate for the interaction
   * @see #mouseDown
   */
  protected def getAnchorY: Int = myAnchorY

  def getUndoActivity: Undoable = myUndoActivity

  def setUndoActivity(newUndoActivity: Undoable) {
    myUndoActivity = newUndoActivity
  }

  def isActive: Boolean = (editor.tool == this) && isUsable

  def addToolListener(newToolListener: ToolListener) {
    getEventDispatcher.addToolListener(newToolListener)
  }

  def removeToolListener(oldToolListener: ToolListener) {
    getEventDispatcher.removeToolListener(oldToolListener)
  }

  private def setEventDispatcher(newEventDispatcher: AbstractTool.EventDispatcher) {
    myEventDispatcher = newEventDispatcher
  }

  protected def getEventDispatcher: AbstractTool.EventDispatcher = myEventDispatcher

  protected def createEventDispatcher: AbstractTool.EventDispatcher = new AbstractTool.EventDispatcher(this)

  protected def createViewChangeListener: ViewChangeListener = {
    new ViewChangeListener {
      def viewSelectionChanged(oldView: DrawingView, newView: DrawingView) {
        AbstractTool.this.viewSelectionChanged(oldView, newView)
      }

      def viewCreated(view: DrawingView) {
        AbstractTool.this.viewCreated(view)
      }

      def viewDestroying(view: DrawingView) {
        AbstractTool.this.viewDestroying(view)
      }
    }
  }

  protected def checkUsable() {
    if (isEnabled) {
      setUsable((getActiveView != null) && getActiveView.isInteractive)
    }
  }
}

