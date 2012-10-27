/*
 * @(#)BringToFrontCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import org.jhotdraw.framework.DrawingEditor
import org.jhotdraw.framework.DrawingView
import org.jhotdraw.framework.Figure
import org.jhotdraw.util.Undoable

/**
 * BringToFrontCommand brings the selected figures in the front of
 * the other figures.
 *
 * @see SendToBackCommand
 * @version <$CURRENT_VERSION$>
 */
object BringToFrontCommand {

  class UndoActivity(newDrawingView: DrawingView) extends SendToBackCommand.UndoActivity(newDrawingView) {

    override protected def sendToCommand(f: Figure) {
      getDrawingView.drawing.bringToFront(f)
    }
  }

}

class BringToFrontCommand(name: String, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(view.selection)
    getUndoActivity.getAffectedFigures foreach {view.drawing.bringToFront(_) }
    view.checkDamage
  }

  override def isExecutableWithView: Boolean = view.selectionCount > 0

  protected def createUndoActivity: Undoable = new BringToFrontCommand.UndoActivity(view)
}

