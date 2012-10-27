/*
 * @(#)DuplicateCommand.java
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
import org.jhotdraw.util._

/**
 * Duplicate the selection and select the duplicates.
 *
 * @version <$CURRENT_VERSION$>
 */
class DuplicateCommand(name: String, newDrawingEditor: DrawingEditor) extends FigureTransferCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    val selection: FigureSelection = view.getFigureSelection
    val figures: FigureEnumeration = selection.getData(StandardFigureSelection.TYPE).asInstanceOf[FigureEnumeration]
    getUndoActivity.setAffectedFigures(figures)
    view.clearSelection
    getUndoActivity.setAffectedFigures(insertFigures(getUndoActivity.getAffectedFigures, 10, 10))
    view.checkDamage
  }

  protected override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new PasteCommand.UndoActivity(view)
}

