/*
 * @(#)ChangeAttributeCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	��� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework.align

import org.shotdraw.framework._
import org.shotdraw.util.UndoableAdapter
import org.shotdraw.util.Undoable
import org.shotdraw.standard.ChangeAttributeCommand
import org.shotdraw.standard.AbstractCommand
import org.shotdraw.framework.align.factory.AlignFactory
import org.shotdraw.framework.align.alignments.Align


/**
 * Command to change a named figure attribute.
 *
 * @version <$CURRENT_VERSION$>
 */
object AlignCommand {

  class UndoActivity(newDrawingView: DrawingView, var myUndoValue: Align) extends UndoableAdapter(newDrawingView) {
    
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      myUndoValue.disable()
      true
    }

    override def redo: Boolean = {
      if (!isRedoable) {
        return false
      }
      myUndoValue.enable()
      true
    }
  }
}

class AlignCommand(name: String, factory: AlignFactory, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  private var a: Align = null
  
  override def execute() {
    super.execute()
    a = factory.instance(view)
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(view.selection)
    AlignManager.add(a)
    a.enable()
  }

  override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new AlignCommand.UndoActivity(newDrawingEditor.view,a)
}

