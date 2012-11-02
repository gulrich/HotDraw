/*
 * @(#)DragTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.framework._
import org.shotdraw.util.UndoableAdapter
import org.shotdraw.util.Undoable
import java.awt._
import java.awt.event.MouseEvent

/**
 * DragTracker implements the dragging of the clicked
 * figure.
 *
 * @see SelectionTool
 *
 * @version <$CURRENT_VERSION$>
 */
object DragTracker {

  class UndoActivity(newDrawingView: DrawingView, newOriginalPoint: Point) extends UndoableAdapter(newDrawingView) {
    private var myOriginalPoint: Point = null
    private var myBackupPoint: Point = null
    setOriginalPoint(newOriginalPoint)
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      moveAffectedFigures(getBackupPoint, getOriginalPoint)
      true
    }

    override def redo: Boolean = {
      if (!super.redo) {
        return false
      }
      moveAffectedFigures(getOriginalPoint, getBackupPoint)
      true
    }

    def setBackupPoint(newBackupPoint: Point) {
      myBackupPoint = newBackupPoint
    }

    def getBackupPoint: Point = myBackupPoint    

    def setOriginalPoint(newOriginalPoint: Point) {
      myOriginalPoint = newOriginalPoint
    }

    def getOriginalPoint: Point = myOriginalPoint

    def moveAffectedFigures(startPoint: Point, endPoint: Point) {
      val figures: Seq[Figure] = getAffectedFigures
      figures foreach {f =>
        f.moveBy(endPoint.x - startPoint.x, endPoint.y - startPoint.y)
      }
    }

  }

}

class DragTracker(newDrawingEditor: DrawingEditor, anchor: Figure) extends AbstractTool(newDrawingEditor) {
  private var fAnchorFigure: Figure = anchor
  private var fLastX: Int = 0
  private var fLastY: Int = 0
  private var fMoved: Boolean = false

  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    super.mouseDown(e, x, y)
    setLastMouseX(x)
    setLastMouseY(y)
    if (e.isShiftDown) {
      getActiveView.toggleSelection(getAnchorFigure)
      setAnchorFigure(null)
    } else if (!getActiveView.isFigureSelected(getAnchorFigure)) {
      getActiveView.clearSelection
      getActiveView.addToSelection(getAnchorFigure)
    }
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(getActiveView.selection)
  }

  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {
    super.mouseDrag(e, x, y)
    setHasMoved((Math.abs(x - getAnchorX) > 4) || (Math.abs(y - getAnchorY) > 4))
    if (hasMoved) {
      getUndoActivity.getAffectedFigures foreach {f =>
        f.moveBy(x - getLastMouseX, y - getLastMouseY)
      }
    }
    setLastMouseX(x)
    setLastMouseY(y)
  }

  protected def setAnchorFigure(newAnchorFigure: Figure) {
    fAnchorFigure = newAnchorFigure
  }

  def getAnchorFigure: Figure = fAnchorFigure

  protected def setLastMouseX(newLastMouseX: Int) {
    fLastX = newLastMouseX
  }

  protected def getLastMouseX: Int = fLastX

  protected def setLastMouseY(newLastMouseY: Int) {
    fLastY = newLastMouseY
  }

  protected def getLastMouseY: Int = fLastY

  /**
   * Check whether the selected figure has been moved since
   * the tool has been activated.
   *
   * @return true if the selected figure has been moved
   */
  def hasMoved: Boolean = fMoved

  protected def setHasMoved(newMoved: Boolean) {
    fMoved = newMoved
  }

  override def activate {}

  override def deactivate {
    if (hasMoved) {
      (getUndoActivity.asInstanceOf[DragTracker.UndoActivity]).setBackupPoint(new Point(getLastMouseX, getLastMouseY))
    } else {
      setUndoActivity(null)
    }
  }

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new DragTracker.UndoActivity(getActiveView, new Point(getLastMouseX, getLastMouseY))

}

