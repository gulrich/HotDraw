/*
 * @(#)SelectionTool.java
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
import org.jhotdraw.util.UndoableTool
import org.jhotdraw.util.UndoableHandle
import org.jhotdraw.contrib.dnd.DragNDropTool
import java.awt.event.MouseEvent

/**
 * Tool to select and manipulate figures.
 * A selection tool is in one of three states, e.g., background
 * selection, figure selection, handle manipulation. The different
 * states are handled by different child tools.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld032.htm>State</a></b><br>
 * SelectionTool is the StateContext and child is the State.
 * The SelectionTool delegates state specific
 * behavior to its current child tool.
 * <hr>
 *
 * @version <$CURRENT_VERSION$>
 */
class SelectionTool(newDrawingEditor: DrawingEditor) extends AbstractTool(newDrawingEditor) {
  /**
   * Handles mouse down events and starts the corresponding tracker.
   */
  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    super.mouseDown(e, x, y)
    if (getDelegateTool != null) {
      return
    }
    view.freezeView
    val handle: Handle = view.findHandle(e.getX, e.getY)
    if (handle != null) {
      setDelegateTool(createHandleTracker(view, handle))
    } else {
      val figure: Figure = drawing.findFigure(e.getX, e.getY)
      if (figure != null) {
        setDelegateTool(createDragTracker(figure))
      } else {
        if (!e.isShiftDown) {
          view.clearSelection
        }
        setDelegateTool(createAreaTracker)
      }
    }
    getDelegateTool.activate
    getDelegateTool.mouseDown(e, x, y)
  }

  /**
   * Handles mouse moves (if the mouse button is up).
   * Switches the cursors depending on whats under them.
   */
  override def mouseMove(evt: MouseEvent, x: Int, y: Int) {
    if (evt.getSource eq getActiveView) {
      DragNDropTool.setCursor(evt.getX, evt.getY, getActiveView)
    }
  }

  /**
   * Handles mouse drag events. The events are forwarded to the
   * current tracker.
   */
  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {
    if (getDelegateTool != null) {
      getDelegateTool.mouseDrag(e, x, y)
    }
  }

  /**
   * Handles mouse up events. The events are forwarded to the
   * current tracker.
   */
  override def mouseUp(e: MouseEvent, x: Int, y: Int) {
    if (getDelegateTool != null) {
      getDelegateTool.mouseUp(e, x, y)
      getDelegateTool.deactivate
      setDelegateTool(null)
    }
    if (view != null) {
      view.unfreezeView
      editor.figureSelectionChanged(view)
    }
  }

  /**
   * Factory method to create a Handle tracker. It is used to track a handle.
   */
  protected def createHandleTracker(view: DrawingView, handle: Handle): Tool = new HandleTracker(editor, new UndoableHandle(handle))

  /**
   * Factory method to create a Drag tracker. It is used to drag a figure.
   */
  protected def createDragTracker(f: Figure): Tool = new UndoableTool(new DragTracker(editor, f))

  /**
   * Factory method to create an area tracker. It is used to select an
   * area.
   */
  protected def createAreaTracker: Tool = new SelectAreaTracker(editor)

  protected def getDelegateTool: Tool = myDelegationTool

  protected final def setDelegateTool(newDelegateTool: Tool) {
    myDelegationTool = newDelegateTool
  }

  private var myDelegationTool: Tool = null
}

