/*
 * @(#)FontSizeHandle.java
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
import org.shotdraw.util.Undoable
import org.shotdraw.util.UndoableAdapter
import java.awt.Font
import java.awt.Rectangle
import java.awt.Graphics
import java.awt.Color

/**
 * A Handle to change the font size by direct manipulation.
 *
 * @version <$CURRENT_VERSION$>
 */
object FontSizeHandle {

  class UndoActivity(newView: DrawingView, var myFont: Font) extends UndoableAdapter(newView) {
    private var myOldFontSize: Int = getFont.getSize
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) false
      else {
        swapFont
        true
      }
    }

    override def redo: Boolean = {
      if (!isRedoable) {
        false
      } else {
        swapFont
        true
      }
    }

    private[figures] def swapFont {
      setOldFontSize(replaceFontSize)
      getAffectedFigures foreach ( f => f match {
        case tf: TextFigure => tf.setFont(getFont)
        case _ =>
      })
    }

    private def replaceFontSize: Int = {
      val tempFontSize = getFont.getSize
      setFont(new Font(getFont.getName, getFont.getStyle, getOldFontSize))
      tempFontSize
    }

    private[figures] def setFont(newFont: Font) {
      myFont = newFont
    }

    def getFont: Font = myFont

    private[figures] def setOldFontSize(newOldFontSize: Int) {
      myOldFontSize = newOldFontSize
    }

    def getOldFontSize: Int = myOldFontSize
  }

}

class FontSizeHandle(owner: TextFigure, l: Locator) extends LocatorHandle(owner, l) {

  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    setUndoActivity(createUndoActivity(view))
    getUndoActivity.setAffectedFigures(List(owner))
  }

  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val textOwner = owner.asInstanceOf[TextFigure]
    val activity = getUndoActivity.asInstanceOf[FontSizeHandle.UndoActivity]
    val newSize = activity.getFont.getSize + y - anchorY
    textOwner.setFont(new Font(activity.getFont.getName, activity.getFont.getStyle, newSize))
  }

  override def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val textOwner = owner.asInstanceOf[TextFigure]
    val activity = getUndoActivity.asInstanceOf[FontSizeHandle.UndoActivity]
    if (textOwner.getFont.getSize == activity.getOldFontSize) {
      setUndoActivity(null)
    } else {
      activity.setFont(textOwner.getFont)
    }
  }

  override def draw(g: Graphics) {
    val r = displayBox
    g.setColor(Color.yellow)
    g.fillOval(r.x, r.y, r.width, r.height)
    g.setColor(Color.black)
    g.drawOval(r.x, r.y, r.width, r.height)
  }

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity(newView: DrawingView): Undoable = new FontSizeHandle.UndoActivity(newView, owner.getFont)
}

