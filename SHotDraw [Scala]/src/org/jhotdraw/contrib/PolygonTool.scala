/*
 * @(#)PolygonTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.contrib

import org.jhotdraw.framework._
import org.jhotdraw.standard._
import org.jhotdraw.util.Undoable
import java.awt.event.MouseEvent
import java.awt.Point

/**
 * Based on ScribbleTool
 *
 * @author Doug Lea  (dl at gee) - Fri Feb 28 07:47:05 1997
 * @version <$CURRENT_VERSION$>
 */
class PolygonTool(newDrawingEditor: DrawingEditor) extends AbstractTool(newDrawingEditor) {

  override def activate {
    super.activate
    fPolygon = null
  }

  override def deactivate {
    if (fPolygon != null) {
      fPolygon.smoothPoints
      if (fPolygon.pointCount < 3 || fPolygon.size.width < 4 || fPolygon.size.height < 4) {
        getActiveView.drawing.remove(fPolygon)
        setUndoActivity(null)
      }
    }
    fPolygon = null
    super.deactivate
  }

  private def addPoint(x: Int, y: Int) {
    if (fPolygon == null) {
      fPolygon = new PolygonFigure(x, y)
      setAddedFigure(view.add(fPolygon))
      fPolygon.addPoint(x, y)
    } else if (fLastX != x || fLastY != y) {
      fPolygon.addPoint(x, y)
    }
    fLastX = x
    fLastY = y
  }

  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    super.mouseDown(e, x, y)

    if (e.getClickCount >= 2) {
      if (fPolygon != null) {
        fPolygon.smoothPoints
        setUndoActivity(createUndoActivity)
        getUndoActivity.setAffectedFigures(List(getAddedFigure))
        editor.toolDone
      }
      fPolygon = null
    } else {
      addPoint(e.getX, e.getY)
    }
  }

  override def mouseMove(e: MouseEvent, x: Int, y: Int) {
    if (e.getSource eq getActiveView) {
      if (fPolygon != null) {
        if (fPolygon.pointCount > 1) {
          fPolygon.setPointAt(new Point(x, y), fPolygon.pointCount - 1)
          getActiveView.checkDamage
        }
      }
    }
  }

  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {
    addPoint(e.getX, e.getY)
  }

  override def mouseUp(e: MouseEvent, x: Int, y: Int) {}

  /**
   * Gets the figure that was actually added
   * Note, this can be a different figure from the one which has been created.
   */
  protected def getAddedFigure: Figure = myAddedFigure

  private def setAddedFigure(newAddedFigure: Figure) {
    myAddedFigure = newAddedFigure
  }

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new PasteCommand.UndoActivity(view)

  private var fPolygon: PolygonFigure = null
  private var fLastX: Int = 0
  private var fLastY: Int = 0
  /**
   * the figure that was actually added
   * Note, this can be a different figure from the one which has been created.
   */
  private var myAddedFigure: Figure = null
}

