/*
 * JHDDropTargetListener.java
 *
 * Created on January 28, 2003, 4:23 PM
 */
package org.shotdraw.contrib.dnd

import org.shotdraw.framework._
import org.shotdraw.standard.DeleteFromDrawingVisitor
import java.io.File
import org.shotdraw.util.Undoable
import java.awt.Point
import java.awt.datatransfer.DataFlavor
import java.awt.dnd._

import org.shotdraw.util.UndoableAdapter

/**
 *
 * @author  Administrator
 */
object JHDDropTargetListener {
  private def log(message: String) {
  }

  class AddUndoActivity(newDrawingView: DrawingView) extends UndoableAdapter(newDrawingView) {
    log("AddUndoActivity created " + newDrawingView)
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      log("AddUndoActivity AddUndoActivity undo")
      val deleteVisitor = new DeleteFromDrawingVisitor(getDrawingView.drawing)
      getAffectedFigures foreach { f => f.visit(deleteVisitor)}
      setAffectedFigures(deleteVisitor.getDeletedFigures)
      getDrawingView.clearSelection
      undone = true
      true
    }

    override def redo: Boolean = {
      if (!isRedoable) {
        return false
      }
      log("AddUndoActivity redo")
      getDrawingView.clearSelection
      setAffectedFigures(getDrawingView.insertFigures(getAffectedFigures, 0, 0, false))
      undone = false
      true
    }

    /**
     * Since this is an add operation, figures can only be released if it
     * has been undone.
     */
    override def release {
      if (undone == true) {
        getAffectedFigures foreach { f =>
          getDrawingView.drawing.remove(f)
          f.release
        }
      }
      setAffectedFigures(Seq())
    }

    private var undone = false
  }

}

class JHDDropTargetListener(drawingEditor: DrawingEditor, drawingView: DrawingView) extends DropTargetListener {
  import JHDDropTargetListener._
  
  protected def view: DrawingView = drawingView

  protected def editor: DrawingEditor = editor

  /**
   * Called when a drag operation has encountered the DropTarget.
   */
  def dragEnter(dtde: DropTargetDragEvent) {
    log("DropTargetDragEvent-dragEnter")
    supportDropTargetDragEvent(dtde)
    if (fLastX == 0) {
      fLastX = dtde.getLocation.x
    }
    if (fLastY == 0) {
      fLastY = dtde.getLocation.y
    }
  }

  /**
   * The drag operation has departed the DropTarget without dropping.
   */
  def dragExit(dte: DropTargetEvent) {
    log("DropTargetEvent-dragExit")
  }

  /**
   * Called when a drag operation is ongoing on the DropTarget.
   */
  def dragOver(dtde: DropTargetDragEvent) {
    if (supportDropTargetDragEvent(dtde) == true) {
      val x = dtde.getLocation.x
      val y = dtde.getLocation.y
      if ((math.abs(x - fLastX) > 0) || (math.abs(y - fLastY) > 0)) {
        fLastX = x
        fLastY = y
      }
    }
  }

  /**
   * The drag operation has terminated with a drop on this DropTarget.
   * Be nice to somehow incorporate FigureTransferCommand here.
   */
  def drop(dtde: DropTargetDropEvent) {
    System.out.println("DropTargetDropEvent-drop")
    if (dtde.isDataFlavorSupported(DNDFiguresTransferable.DNDFiguresFlavor) == true) {
      log("DNDFiguresFlavor")
      if ((dtde.getDropAction & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
        log("copy or move")
        if (dtde.isLocalTransfer == false) {
          System.err.println("Intra-JVM Transfers not implemented for figures yet.")
          dtde.rejectDrop
          return
        }
        dtde.acceptDrop(dtde.getDropAction)
        try {
          setTargetUndoActivity(createTargetUndoActivity(view))
          val ff = DNDHelper.processReceivedData(DNDFiguresTransferable.DNDFiguresFlavor, dtde.getTransferable).asInstanceOf[DNDFigures]
          getTargetUndoActivity.setAffectedFigures(ff.getFigures)
          val theO = ff.getOrigin
          view.clearSelection
          val newP = dtde.getLocation
          val dx = newP.x - theO.x
          val dy = newP.y - theO.y
          log("mouse at " + newP)
          val fe = view.insertFigures(getTargetUndoActivity.getAffectedFigures, dx, dy, false)
          getTargetUndoActivity.setAffectedFigures(fe)
          if (dtde.getDropAction == DnDConstants.ACTION_MOVE) {
            view.addToSelectionAll(getTargetUndoActivity.getAffectedFigures)
          }
          view.checkDamage
          editor.getUndoManager.pushUndo(getTargetUndoActivity)
          editor.getUndoManager.clearRedos
          editor.figureSelectionChanged(view)
          dtde.dropComplete(true)
        } catch {
          case npe: NullPointerException => {
            npe.printStackTrace
            dtde.dropComplete(false)
          }
        }
      } else {
        dtde.rejectDrop
      }
    } else if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      log("String flavor dropped.")
      dtde.acceptDrop(dtde.getDropAction)
      val o = DNDHelper.processReceivedData(DataFlavor.stringFlavor, dtde.getTransferable)
      if (o != null) {
        log("Received string flavored data.")
        dtde.getDropTargetContext.dropComplete(true)
      }
      else {
        dtde.getDropTargetContext.dropComplete(false)
      }
    }
    else if (dtde.isDataFlavorSupported(DNDHelper.ASCIIFlavor) == true) {
      log("ASCII Flavor dropped.")
      dtde.acceptDrop(DnDConstants.ACTION_COPY)
      val o = DNDHelper.processReceivedData(DNDHelper.ASCIIFlavor, dtde.getTransferable)
      if (o != null) {
        log("Received ASCII Flavored data.")
        dtde.getDropTargetContext.dropComplete(true)
      }
      else {
        dtde.getDropTargetContext.dropComplete(false)
      }
    }
    else if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      log("Java File List Flavor dropped.")
      dtde.acceptDrop(DnDConstants.ACTION_COPY)
      val fList = DNDHelper.processReceivedData(DataFlavor.javaFileListFlavor, dtde.getTransferable).asInstanceOf[Array[File]].toList
      if (fList != null) {
        log("Got list of files.")
        fList foreach { x => 
          println(x.getAbsolutePath)
        }
        dtde.getDropTargetContext.dropComplete(true)
      } else {
        dtde.getDropTargetContext.dropComplete(false)
      }
    }
    fLastX = 0
    fLastY = 0
  }

  /**
   * Called if the user has modified the current drop gesture.
   */
  def dropActionChanged(dtde: DropTargetDragEvent) {
    log("DropTargetDragEvent-dropActionChanged")
    supportDropTargetDragEvent(dtde)
  }

  /**
   * Tests wether the Drag event is of a type that we support handling
   * Check the DND interface and support the events it says it supports
   * if not a dnd interface comp, then dont support! because we dont even
   * really know what kind of view it is.
   */
  protected def supportDropTargetDragEvent(dtde: DropTargetDragEvent): Boolean = {
    if (dtde.isDataFlavorSupported(DNDFiguresTransferable.DNDFiguresFlavor)) {
      if ((dtde.getDropAction & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
        dtde.acceptDrag(dtde.getDropAction)
        true
      } else {
        dtde.rejectDrag
        false
      }
    } else if (dtde.isDataFlavorSupported(DNDHelper.ASCIIFlavor) || dtde.isDataFlavorSupported(DataFlavor.stringFlavor) || dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      dtde.acceptDrag(dtde.getDropAction)
      true
    } else {
      dtde.rejectDrag
      false
    }
  }

  /**
   * Factory method for undo activity
   */
  protected def createTargetUndoActivity(view: DrawingView): Undoable = new JHDDropTargetListener.AddUndoActivity(view)

  protected def setTargetUndoActivity(undoable: Undoable) {
    targetUndoable = undoable
  }

  protected def getTargetUndoActivity: Undoable = targetUndoable

  private var fLastX = 0
  private var fLastY = 0
  private var targetUndoable: Undoable = null
}

