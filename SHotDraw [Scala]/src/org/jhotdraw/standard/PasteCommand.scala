/*
 * @(#)PasteCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import java.awt.Rectangle
import org.jhotdraw.framework.DrawingEditor
import org.jhotdraw.framework.DrawingView
import org.jhotdraw.framework.FigureSelection
import org.jhotdraw.util.Clipboard
import org.jhotdraw.util.Undoable
import org.jhotdraw.util.UndoableAdapter
import org.jhotdraw.framework.Figure

/**
 * Command to insert the clipboard into the drawing.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
object PasteCommand {

  class UndoActivity(newDrawingView: DrawingView) extends UndoableAdapter(newDrawingView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      val deleteVisitor: DeleteFromDrawingVisitor = new DeleteFromDrawingVisitor(getDrawingView.drawing)
      getAffectedFigures foreach {
        _.visit(deleteVisitor)
      }
      getDrawingView.clearSelection
      true
    }

    override def redo: Boolean = {
      if (!isRedoable) {
        return false
      }
      getDrawingView.clearSelection
      setAffectedFigures(getDrawingView.insertFigures(getAffectedFigures, 0, 0, false))
      true
    }
  }

}

class PasteCommand(name: String, newDrawingEditor: DrawingEditor) extends FigureTransferCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    val selection: FigureSelection = Clipboard.getClipboard.getContents.asInstanceOf[FigureSelection]
    if (selection != null) {
      setUndoActivity(createUndoActivity)
      getUndoActivity.setAffectedFigures(selection.getData(StandardFigureSelection.TYPE))
      if (getUndoActivity.getAffectedFigures.isEmpty) {
        setUndoActivity(null)
        return
      }
      val r: Rectangle = getBounds(getUndoActivity.getAffectedFigures)
      view.clearSelection
      val fe: Seq[Figure] = insertFigures(getUndoActivity.getAffectedFigures, r.x + 20, r.y + 20)
      getUndoActivity.setAffectedFigures(fe)
      view.checkDamage
    }
  }

  override def isExecutableWithView: Boolean = Clipboard.getClipboard.getContents != null

  private def getBounds(fe: Seq[Figure]): Rectangle = {
    val r: Rectangle = new Rectangle
    fe foreach { f =>
      r.add(f.displayBox)
    }
    r
  }

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new PasteCommand.UndoActivity(view)
}

