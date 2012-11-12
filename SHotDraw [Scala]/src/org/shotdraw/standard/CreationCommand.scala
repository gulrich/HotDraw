/*
 * @(#)ChangeAttributeCommand.java
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


/**
 *
 * @version <$CURRENT_VERSION$>
 */
object CreationCommand {

  class UndoActivity(newDrawingView: DrawingView, var myUndoFigure: Figure) extends UndoableAdapter(newDrawingView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!isRedoable) {
        return false
      }
      val deleteVisitor = new DeleteFromDrawingVisitor(newDrawingView.drawing)
      myUndoFigure.visit(deleteVisitor)
      newDrawingView.clearSelection
      true
    }

    override def redo: Boolean = {
      if (!super.undo) {
        return false
      }
      newDrawingView.add(myUndoFigure)
      true
    }
  }

}

class CreationCommand(name: String, figure: Figure, newDrawingEditor: DrawingEditor) extends FigureTransferCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(Seq(figure))
    println(view.drawing.figures)
    deleteFigures(getUndoActivity.getAffectedFigures)
    println(view.drawing.figures)
  }

  override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   */
  def createUndoActivity: Undoable = new CreationCommand.UndoActivity(view, figure)
}

