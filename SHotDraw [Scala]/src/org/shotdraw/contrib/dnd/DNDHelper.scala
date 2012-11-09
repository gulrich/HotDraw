/*
 * @(#)DNDHelper.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.contrib.dnd

import org.shotdraw.framework._
import java.awt.Component
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.datatransfer.Transferable
import java.awt.dnd._
import java.io._
import java.util.List


/**
 * Changes made in hopes of eventually cleaning up the functionality and 
 * distributing it sensibly. 1/10/02
 * @author  C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object DNDHelper {
  private[dnd] def processReceivedData(flavor: DataFlavor, transferable: Transferable): Any = {
    var receivedData: Any = null
    if (transferable == null) {
      return null
    }
    try {
      if (flavor == DataFlavor.stringFlavor) {
        receivedData = transferable.getTransferData(DataFlavor.stringFlavor)
      }
      else if (flavor == DataFlavor.javaFileListFlavor) {
        val aList: List[Figure] = transferable.getTransferData(DataFlavor.javaFileListFlavor).asInstanceOf[List[Figure]]
        val fList: Array[File] = new Array[File](aList.size)
        aList.toArray(fList)
        receivedData = fList
      }
      else if (flavor == ASCIIFlavor) {
        val is: InputStream = transferable.getTransferData(ASCIIFlavor).asInstanceOf[InputStream]
        val length: Int = is.available
        val bytes: Array[Byte] = new Array[Byte](length)
        val n: Int = is.read(bytes)
        if (n > 0) {
          receivedData = new String(bytes, 0, n)
        }
      }
      else if (flavor == DNDFiguresTransferable.DNDFiguresFlavor) {
        receivedData = transferable.getTransferData(DNDFiguresTransferable.DNDFiguresFlavor)
      }
    }
    catch {
      case ioe: IOException => {
        System.err.println(ioe)
      }
      case ufe: UnsupportedFlavorException => {
        System.err.println(ufe)
      }
      case cce: ClassCastException => {
        System.err.println(cce)
      }
    }
    receivedData
  }

  var ASCIIFlavor: DataFlavor = new DataFlavor("text/plain; charset=ascii", "ASCII text")
}

abstract class DNDHelper(isDragSource: Boolean, isDropTarget: Boolean) {

  /**
   * Do not call this from the constructor.  its methods are overridable.
   */
  def initialize(dgl: DragGestureListener) {
    if (isDragSource) {
      setDragGestureListener(dgl)
      setDragSourceListener(createDragSourceListener)
      setDragGestureRecognizer(createDragGestureRecognizer(getDragGestureListener))
    }
    if (isDropTarget) {
      setDropTargetListener(createDropTargetListener)
      setDropTarget(createDropTarget)
    }
  }

  def deinitialize {
    if (getDragSourceListener != null) {
      destroyDragGestreRecognizer
      setDragSourceListener(null)
    }
    if (getDropTargetListener != null) {
      val dt: DropTarget = NoDropTarget
      setDropTarget(dt)
      setDropTargetListener(null)
    }
  }

  protected def view: DrawingView

  protected def editor: DrawingEditor

  /**
   * This must reflect the capabilities of the dragSsource, not your desired
   * actions.  If you desire limited drag actions, then I suppose you need to
   * make a new drag gesture recognizer?  I do know that if you put for instance
   * ACTION_COPY but your device supports ACTION_COPY_OR_MOVE, then the receiving
   * target may show the rejected icon, but will still be forced improperly to
   * accept your MOVE since the system is not properly calling your MOVE a MOVE
   * because you claimed incorrectly that you were incapable of MOVE.
   */
  protected def getDragSourceActions: Int = DnDConstants.ACTION_COPY_OR_MOVE

  protected def getDropTargetActions: Int = DnDConstants.ACTION_COPY_OR_MOVE

  protected def setDragGestureListener(dragGestureListener: DragGestureListener) {
    this.dragGestureListener = dragGestureListener
  }

  protected def getDragGestureListener: DragGestureListener = dragGestureListener

  protected def setDragGestureRecognizer(dragGestureRecognizer: DragGestureRecognizer) {dgr = dragGestureRecognizer}

  protected def getDragGestureRecognizer: DragGestureRecognizer = dgr

  protected def setDropTarget(newDropTarget: DropTarget) {
    if ((newDropTarget == NoDropTarget) && (dropTarget != null)) {
      dropTarget.setComponent(null)
      dropTarget.removeDropTargetListener(getDropTargetListener)
    }
    dropTarget = newDropTarget
  }
  
  protected def createDropTarget: DropTarget = view match {
    case c: Component => new DropTarget(c, getDropTargetActions, getDropTargetListener) 
    case _ => NoDropTarget
  }

  /**
   * Used to create the gesture recognizer which in effect turns on draggability.
   */
  protected def createDragGestureRecognizer(dgl: DragGestureListener): DragGestureRecognizer = view match {
    case c: Component => DragSource.getDefaultDragSource.createDefaultDragGestureRecognizer(c, getDragSourceActions, dgl)
    case _ => NoDragGestureRecognizer
  }

  /**
   * Used to destroy the gesture listener which ineffect turns off dragability.
   */
  protected def destroyDragGestreRecognizer {
    if (getDragGestureRecognizer != null) {
      getDragGestureRecognizer.removeDragGestureListener(getDragGestureListener)
      getDragGestureRecognizer.setComponent(null)
      setDragGestureRecognizer(null)
    }
  }

  protected def setDropTargetListener(dropTargetListener: DropTargetListener) {
    this.dropTargetListener = dropTargetListener
  }

  protected def getDropTargetListener: DropTargetListener = dropTargetListener

  protected def createDropTargetListener: DropTargetListener = new JHDDropTargetListener(editor, view)

  def getDragSourceListener: DragSourceListener = dragSourceListener

  protected def setDragSourceListener(dragSourceListener: DragSourceListener) {
    this.dragSourceListener = dragSourceListener
  }

  protected def createDragSourceListener: DragSourceListener = new JHDDragSourceListener(editor, view)

  private var dgr: DragGestureRecognizer = null
  private var dragGestureListener: DragGestureListener = null
  private var dropTarget: DropTarget = null
  private var dragSourceListener: DragSourceListener = null
  private var dropTargetListener: DropTargetListener = null
}

object NoDropTarget extends DropTarget
object NoDragSource extends DragSource
object NoDragGestureRecognizer extends DragGestureRecognizer(NoDragSource) {
  override def unregisterListeners() {}
  override def registerListeners() {}
}