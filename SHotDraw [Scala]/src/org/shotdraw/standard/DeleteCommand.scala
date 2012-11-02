/*
 * @(#)DeleteCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.framework.DrawingEditor
import org.shotdraw.framework.Figure
import org.shotdraw.util.Undoable
import org.shotdraw.util.UndoableAdapter
import scala.collection.mutable.ArrayBuffer

/**
 * Command to delete the selection.
 *
 * @version <$CURRENT_VERSION$>
 */
object DeleteCommand {

  class UndoActivity(myCommand: FigureTransferCommand) extends UndoableAdapter(myCommand.view) {
    setUndoable(true)
    setRedoable(true)

    /**
     * @see org.shotdraw.util.Undoable#undo()
     */
    override def undo: Boolean = {
      if (super.undo && !getAffectedFigures.isEmpty) {
        getDrawingView.clearSelection
        setAffectedFigures(myCommand.insertFigures(getAffectedFiguresReversed, 0, 0))
        true
      } else false
    }

    /**
     * @see org.shotdraw.util.Undoable#redo()
     */
    override def redo: Boolean = {
      if (isRedoable) {
        myCommand.deleteFigures(getAffectedFigures)
        getDrawingView.clearSelection
        true
      } else false
    }
  }

}

class DeleteCommand(name: String, newDrawingEditor: DrawingEditor) extends FigureTransferCommand(name, newDrawingEditor) {
  /**
   * @see org.shotdraw.util.Command#execute()
   */
  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    var fe: Seq[Figure] = view.selection
    var affected: ArrayBuffer[Figure] = ArrayBuffer[Figure]()
    var f: Figure = null
    var dfe: Seq[Figure] = null
    fe foreach { f =>
      affected += f
      dfe = f.getDependendFigures
      if (dfe != null) {
        dfe foreach {
          affected +=  _
        }
      }
    }
    getUndoActivity.setAffectedFigures(affected)
    deleteFigures(getUndoActivity.getAffectedFigures)
    view.checkDamage
  }

  /**
   * @see org.shotdraw.standard.AbstractCommand#isExecutableWithView()
   */
  protected override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   * @return Undoable
   */
  protected def createUndoActivity: Undoable = new DeleteCommand.UndoActivity(this)
}

