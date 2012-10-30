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
package org.jhotdraw.standard

import org.jhotdraw.framework._
import org.jhotdraw.util.UndoableAdapter
import org.jhotdraw.util.Undoable
import java.lang.Object

/**
 * Command to change a named figure attribute.
 *
 * @version <$CURRENT_VERSION$>
 */
object ChangeAttributeCommand {

  class UndoActivity(newDrawingView: DrawingView, newUndoAttribute: FigureAttributeConstant, newUndoValue: Any) extends UndoableAdapter(newDrawingView) {
    
    
    private var myUndoAttribute: FigureAttributeConstant = null
    private var myOriginalValues: Map[Figure, Any] = Map()
    private var myUndoValue: Any = null
    
    setBackupValue(newUndoValue)
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      getAffectedFigures foreach { f =>
        if (getOriginalValue(f) != null) {
          f.setAttribute(getAttribute, getOriginalValue(f))
        }
      }
      true
    }

    override def redo: Boolean = {
      if (!isRedoable) {
        return false
      }
      getAffectedFigures foreach { f =>
        if (getBackupValue != null) {
          f.setAttribute(getAttribute, getBackupValue)
        }
      }
      true
    }

    protected def addOriginalValue(affectedFigure: Figure, newOriginalValue: Any) {
      myOriginalValues += ((affectedFigure, newOriginalValue))
    }

    protected def getOriginalValue(lookupAffectedFigure: Figure): Any = myOriginalValues.get(lookupAffectedFigure)

    protected def setAttribute(newUndoAttribute: FigureAttributeConstant) {
      myUndoAttribute = newUndoAttribute
    }

    def getAttribute: FigureAttributeConstant = myUndoAttribute

    protected def setBackupValue(newUndoValue: Any) {
      myUndoValue = newUndoValue
    }

    def getBackupValue: Any = myUndoValue

    override def release {
      super.release
      myOriginalValues = null
    }

    override def setAffectedFigures(fe: Seq[Figure]) {
      super.setAffectedFigures(fe)
      getAffectedFigures foreach { f =>
        val attributeValue: Any = f.getAttribute(getAttribute)
        if (attributeValue != null) {
          addOriginalValue(f, attributeValue)
        }
      }
    }

  }

}

class ChangeAttributeCommand(name: String, fAttribute: FigureAttributeConstant, fValue: Any, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(view.selection)
    getUndoActivity.getAffectedFigures foreach(_.setAttribute(fAttribute, fValue))
  }

  override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new ChangeAttributeCommand.UndoActivity(view, fAttribute, fValue)
}

