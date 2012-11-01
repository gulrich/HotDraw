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

  class UndoActivity[T](newDrawingView: DrawingView, var myUndoAttribute: FigureAttributeConstant[T], var myUndoValue: T) extends UndoableAdapter(newDrawingView) {
        
    private var myOriginalValues: Map[Figure, T] = Map()
    
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      getAffectedFigures foreach { f =>
        if (getOriginalValue(f) != null) {
          myUndoAttribute.setAttribute(f, getOriginalValue(f))
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
          myUndoAttribute.setAttribute(f, getBackupValue)
        }
      }
      true
    }

    protected def addOriginalValue(affectedFigure: Figure, newOriginalValue: T) {
      myOriginalValues += ((affectedFigure, newOriginalValue))
    }

    protected def getOriginalValue(lookupAffectedFigure: Figure): T = myOriginalValues.get(lookupAffectedFigure) match {
      case Some(value) => value
      case _ => error("ChangeAttributeCommand: Figure " + lookupAffectedFigure + " not found in myOriginalValues")
    }

    protected def setAttribute(newUndoAttribute: FigureAttributeConstant[T]) {
      myUndoAttribute = newUndoAttribute
    }

    def getAttribute: FigureAttributeConstant[T] = myUndoAttribute

    protected def setBackupValue(newUndoValue: T) {
      myUndoValue = newUndoValue
    }

    def getBackupValue: T = myUndoValue

    override def release {
      super.release
      myOriginalValues = Map()
    }

    override def setAffectedFigures(fe: Seq[Figure]) {
      super.setAffectedFigures(fe)
      getAffectedFigures foreach { f =>
        val attributeValue: T = myUndoAttribute.getAttribute(f)
        if (attributeValue != null) {
          addOriginalValue(f, attributeValue)
        }
      }
    }

  }

}

class ChangeAttributeCommand[T](name: String, fAttribute: FigureAttributeConstant[T], fValue: T, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    setUndoActivity(createUndoActivity)
    getUndoActivity.setAffectedFigures(view.selection)
    getUndoActivity.getAffectedFigures foreach(f => fAttribute.setAttribute(f,fValue))
  }

  override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new ChangeAttributeCommand.UndoActivity(view, fAttribute, fValue)
}

