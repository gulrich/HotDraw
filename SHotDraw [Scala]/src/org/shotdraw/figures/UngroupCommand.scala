/*
 * @(#)UngroupCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import org.shotdraw.framework._
import org.shotdraw.standard._
import org.shotdraw.util.UndoableAdapter
import org.shotdraw.util.Undoable

/**
 * Command to ungroup the selected figures.
 *
 * @see GroupCommand
 *
 * @version <$CURRENT_VERSION$>
 */
object UngroupCommand {

  class UndoActivity(newDrawingView: DrawingView) extends UndoableAdapter(newDrawingView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) return false      
      getDrawingView.clearSelection
      getAffectedFigures foreach { f =>
        getDrawingView.drawing.orphanAll(f.figures)
        val figure: Figure = getDrawingView.drawing.add(f)
        getDrawingView.addToSelection(figure)
      }
      true
    }

    override def redo: Boolean = {
      if (isRedoable) {
        getDrawingView.drawing.orphanAll(getAffectedFigures)
        getDrawingView.clearSelection
        ungroupFigures
        true
      } else false
    }

    private[figures] def ungroupFigures {
      getAffectedFigures foreach { f =>
        val group: Figure = getDrawingView.drawing.orphan(f)
        getDrawingView.drawing.addAll(group.figures)
        getDrawingView.addToSelectionAll(group.figures)
      }
    }
  }

}

class UngroupCommand(name: String, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(view.selection)
    view.clearSelection
    (getUndoActivity.asInstanceOf[UngroupCommand.UndoActivity]).ungroupFigures
    view.checkDamage
  }

  override def isExecutableWithView: Boolean = {
    view.selection find(f => !f.isInstanceOf[GroupFigure]) match {
      case Some(_) => false
      case _ => view.selectionCount > 0
    }
  }

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new UngroupCommand.UndoActivity(view)
}

