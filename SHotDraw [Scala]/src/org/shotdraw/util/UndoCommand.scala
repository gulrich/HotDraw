/*
 * @(#)UndoCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import org.shotdraw.standard._
import org.shotdraw.framework._

/**
 * Command to undo the latest change in the drawing.
 * Undo activities can be undone only once, therefore they
 * are not added to the undo stack again (redo activities
 * can be added to the redo stack again, because they can
 * be redone several times, every time pushing a corresponding
 * undo activity as well).
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class UndoCommand(name: String, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    val um: UndoManager = getDrawingEditor.getUndoManager
    if ((um == null) || !um.isUndoable) {
      return
    }
    val lastUndoable: Undoable = um.popUndo
    val hasBeenUndone: Boolean = lastUndoable.undo
    if (hasBeenUndone && lastUndoable.isRedoable) {
      um.pushRedo(lastUndoable)
    }
    lastUndoable.getDrawingView.checkDamage
    getDrawingEditor.figureSelectionChanged(lastUndoable.getDrawingView)
  }

  /**
   * Used in enabling the undo menu item.
   * Undo menu item will be enabled only when there is atleast one undoable
   * activity registered with UndoManager.
   */
  override def isExecutableWithView: Boolean = {
    val um: UndoManager = getDrawingEditor.getUndoManager
    (um != null) && (um.getUndoSize > 0)
  }
}

