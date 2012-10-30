/*
 * @(#)SelectAllCommand.java
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
import org.jhotdraw.util.UndoableAdapter
import org.jhotdraw.util.Undoable

/**
 * Command to select all figures in a view.
 *
 * @version <$CURRENT_VERSION$>
 */
object SelectAllCommand {

  class UndoActivity(newDrawingView: DrawingView) extends UndoableAdapter(newDrawingView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      getDrawingView.clearSelection
      getDrawingView.addToSelectionAll(getAffectedFigures)
      true
    }

    override def redo: Boolean = {
      if (isRedoable) {
        getDrawingView.addToSelectionAll(getDrawingView.drawing.figures)
        return true
      }
      false
    }
  }

}

class SelectAllCommand(name: String, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(view.selection)
    view.addToSelectionAll(view.drawing.figures)
    view.checkDamage
  }

  /**
   * Used in enabling the properties menu item.
   * SelectAll menu item will be enabled only when there ia atleast one figure
   * in the selected drawing view.
   */
  override def isExecutableWithView: Boolean = {
    val fe: Iterator[Figure] = view.drawing.figures.iterator
    fe.hasNext && (fe.next != null)
  }

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new SelectAllCommand.UndoActivity(view)
}

