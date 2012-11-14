/*
 * @(#)SendToBackCommand.java
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
 * A command to send the selection to the back of the drawing.
 *
 * @version <$CURRENT_VERSION$>
 */
object SendToBackCommand {

  class UndoActivity(newDrawingView: DrawingView) extends UndoableAdapter(newDrawingView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      getAffectedFigures foreach { f =>
        val currentFigureLayer = getOriginalLayer(f)
        getDrawingView.drawing.sendToLayer(f, currentFigureLayer)
      }
      true
    }

    override def redo: Boolean = {
      if (!isRedoable) {
        return false
      }
      getAffectedFigures foreach {sendToCommand(_)}
      true
    }

    protected def sendToCommand(f: Figure) {
      getDrawingView.drawing.sendToBack(f)
    }

    protected def addOriginalLayer(affectedFigure: Figure, newOriginalLayer: Int) {
      myOriginalLayers += ((affectedFigure, newOriginalLayer))
    }

    protected def getOriginalLayer(lookupAffectedFigure: Figure): Int = myOriginalLayers.get(lookupAffectedFigure) match {
      case Some(i) => i
      case _ => -1
    }

    override def setAffectedFigures(fe: Seq[Figure]) {
      super.setAffectedFigures(fe)
      getAffectedFigures foreach { f =>
        val originalLayer = getDrawingView.drawing.getLayer(f)
        addOriginalLayer(f, originalLayer) 
      }
    }

    private var myOriginalLayers = Map[Figure, Int]()
  }

}

class SendToBackCommand(name: String, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  override def execute() {
    super.execute
    super.setUndoActivity(createUndoActivity)
    super.getUndoActivity.setAffectedFigures(view.selectionZOrdered)
    super.getUndoActivity.getAffectedFigures foreach {
      super.view.drawing.sendToBack(_)
    }
    super.view.checkDamage
  }

  override protected def isExecutableWithView: Boolean = view.selectionCount > 0

  protected def createUndoActivity: Undoable = new SendToBackCommand.UndoActivity(view)
}

