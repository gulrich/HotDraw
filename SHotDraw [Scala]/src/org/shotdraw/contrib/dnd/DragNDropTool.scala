/*
 * @(#)DragNDropTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.contrib.dnd

import java.awt.Component
import java.awt.Point
import java.awt.dnd.DragGestureListener
import java.awt.event.MouseEvent
import javax.swing.JComponent
import org.shotdraw.framework._
import org.shotdraw.standard._

/**
 * This is a tool which handles drag and drop between Components in
 * JHotDraw and drags from JHotDraw.  It also indirectly
 * handles management of Drops from extra-JVM sources.
 *
 *
 * Drag and Drop is about information moving, not images or objects.  Its about
 * moving a JHD rectangle to another application and that application understanding
 * both its shape, color, attributes, and everything about it. not how it looks.
 *
 * There can be only 1 such tool in an application.  A view can be registered
 * with only a single DropSource as far as I know (maybe not).
 *
 * @todo    For intra JVM transfers we need to pass Point origin as well, and not
 *          assume it will be valid which currently will cause a null pointer exception.
 *          or worse, will be valid with some local value.
 *          The dropSource will prevent simultaneous drops.
 *
 *          For a Container to be initialized to support Drag and Drop, it must first
 *          have a connection to a heavyweight component.  Or more precisely it must have
 *          a peer.  That means new Component() is not capable of being initiated until
 *          it has attachment to a top level component i.e. JFrame.add(comp);  If you add
 *          a Component to a Container, that Container must be the child of some
 *          Container which is added in its heirachy to a topmost Component.  I will
 *          refine this description with more appropriate terms as I think of new ways to
 *          express this.  It won't work until setVisible(true) is called.  then you can
 *          initialize DND.
 *
 *          note: if drop target is same as dragsource then we should draw the object.
 *
 *
 * @author C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object DragNDropTool {
  /**
   * Sets the type of cursor based on what is under the coordinates in the
   * active view.
   */
  def setCursor(x: Int, y: Int, view: DrawingView) {
    if (view == null) {
      return
    }
    val handle = view.findHandle(x, y)
    val figure = view.drawing.findFigure(x, y)
    if (handle != null) {
      view.setCursor(handle.getCursor)
    } else if (figure != null) {
      view.setCursor(new AWTCursor(java.awt.Cursor.MOVE_CURSOR))
    } else {
      view.setCursor(new AWTCursor(java.awt.Cursor.DEFAULT_CURSOR))
    }
  }
}

class DragNDropTool(editor: DrawingEditor) extends AbstractTool(editor) {
  import DragNDropTool._
  setDragGestureListener(createDragGestureListener)

  /**
   * Sent when a new view is created
   */
  override protected def viewCreated(view: DrawingView) {
    super.viewCreated(view)
    if (classOf[DNDInterface].isInstance(view)) {
      val dndi = view.asInstanceOf[DNDInterface]
      dndi.DNDInitialize(getDragGestureListener)
    }
  }

  /**
   * Send when an existing view is about to be destroyed.
   */
  override protected def viewDestroying(view: DrawingView) {
    if (classOf[DNDInterface].isInstance(view)) {
      val dndi = view.asInstanceOf[DNDInterface]
      dndi.DNDDeinitialize
    }
    super.viewDestroying(view)
  }

  /**
   * Turn on drag by adding a DragGestureRegognizer to all Views which are
   * based on Components.
   */
  override def activate {
    super.activate
    setDragOn(true)
  }

  override def deactivate {
    setDragOn(false)
    super.deactivate
  }

  /**
   * Handles mouse moves (if the mouse button is up).
   * Switches the cursors depending on whats under them.
   * Don't use x, y use getX and getY so get the real unlimited position
   * Part of the Tool interface.
   */
  override def mouseMove(evt: MouseEvent, x: Int, y: Int) {
    if (evt.getSource eq getActiveView) {
      setCursor(x, y, getActiveView)
    }
  }

  /**
   * Handles mouse up events. The events are forwarded to the
   * current tracker.
   * Part of the Tool interface.
   */
  override def mouseUp(e: MouseEvent, x: Int, y: Int) {
    if (fChild != null) {
      fChild.mouseUp(e, x, y)
      fChild = null
    }
    setDragOn(true)
    view.unfreezeView
  }

  /**
   * Handles mouse down events and starts the corresponding tracker.
   * Part of the Tool interface.
   */
  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    super.mouseDown(e, x, y)
    if (fChild != null) {
      return
    }
    view.freezeView
    val handle = view.findHandle(getAnchorX, getAnchorY)
    if (handle != null) {
      setDragOn(false)
      fChild = createHandleTracker(handle)
    }
    else {
      val figure = drawing.findFigure(getAnchorX, getAnchorY)
      if (figure != null) {
        fChild = null
        if (e.isShiftDown) {
          view.toggleSelection(figure)
        }
        else if (!view.isFigureSelected(figure)) {
          view.clearSelection
          view.addToSelection(figure)
        }
      }
      else {
        setDragOn(false)
        if (!e.isShiftDown) {
          view.clearSelection
        }
        fChild = createAreaTracker
      }
    }
    if (fChild != null) {
      fChild.mouseDown(e, x, y)
    }
  }

  /**
   * Handles mouse drag events. The events are forwarded to the
   * current tracker.
   * Part of the Tool interface.
   */
  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {
    if (fChild != null) {
      fChild.mouseDrag(e, x, y)
    }
  }

  /**
   * Factory method to create an area tracker. It is used to select an
   * area.
   */
  protected def createAreaTracker: Tool = new SelectAreaTracker(editor)

  /**
   * Factory method to create a Drag tracker. It is used to drag a figure.
   */
  protected def createDragTracker(editor: DrawingEditor, f: Figure): Tool = new DragTracker(editor, f)

  /**
   * Factory method to create a Handle tracker. It is used to track a handle.
   */
  protected def createHandleTracker(handle: Handle): Tool = new HandleTracker(editor, handle)

  private def getDragGestureListener: DragGestureListener = dragGestureListener

  private def setDragGestureListener(dragGestureListener: DragGestureListener) {
    this.dragGestureListener = dragGestureListener
  }

  protected def isDragOn: Boolean = dragOn

  protected def setDragOn(isNewDragOn: Boolean) {
    this.dragOn = isNewDragOn
  }

  private def createDragGestureListener: DragGestureListener = {
    new DragGestureListener {
      def dragGestureRecognized(dge: java.awt.dnd.DragGestureEvent) {
        val c = dge.getComponent
        if (isDragOn == false) {
          return
        }
        if (c.isInstanceOf[DrawingView]) {
          var found: Boolean = false
          val dv = c.asInstanceOf[DrawingView]
          val selectedElements = dv.selection.iterator
          if (!selectedElements.hasNext) {
            return
          }
          val p = dge.getDragOrigin
          found = selectedElements.find(f => f.containsPoint(p.x, p.y)).isDefined
          if (found) {
            val dndff = new DNDFigures(dv.selection.toList, p)
            val trans = new DNDFiguresTransferable(dndff)
            if (c.isInstanceOf[JComponent]) {
              (c.asInstanceOf[JComponent]).setAutoscrolls(false)
            }
            dge.getDragSource.startDrag(dge, null, trans, (dv.asInstanceOf[DNDInterface]).getDragSourceListener)
          }
        }
      }
    }
  }

  private var fChild: Tool = null
  private var dragGestureListener: DragGestureListener = null
  private var dragOn: Boolean = false
}

