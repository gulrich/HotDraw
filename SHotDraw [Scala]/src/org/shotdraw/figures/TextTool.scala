/*
 * @(#)TextTool.java
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
import org.shotdraw.util.FloatingTextField
import org.shotdraw.util.UndoableAdapter
import org.shotdraw.util.Undoable
import java.awt.event._
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Container

/**
 * Tool to create new or edit existing text figures.
 * The editing behavior is implemented by overlaying the
 * Figure providing the text with a FloatingTextField.<p>
 * A tool interaction is done once a Figure that is not
 * a TextHolder is clicked.
 *
 * @see TextHolder
 * @see FloatingTextField
 *
 * @version <$CURRENT_VERSION$>
 */
object TextTool {

  class UndoActivity(newDrawingView: DrawingView, newOriginalText: String) extends UndoableAdapter(newDrawingView) {
    setOriginalText(newOriginalText)
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      getDrawingView.clearSelection
      setText(getOriginalText)
      true
    }

    override def redo: Boolean = {
      if (!super.redo) {
        return false
      }
      getDrawingView.clearSelection
      setText(getBackupText)
      true
    }

    protected def isValidText(toBeChecked: String): Boolean = ((toBeChecked != null) && (toBeChecked.length > 0))

    protected def setText(newText: String) {
      getAffectedFigures foreach { f =>
        if (f.getTextHolder != null) f.getTextHolder.setText(newText)
      }
    }

    def setBackupText(newBackupText: String) {
      myBackupText = newBackupText
    }

    def getBackupText: String = myBackupText

    def setOriginalText(newOriginalText: String) {
      myOriginalText = newOriginalText
    }

    def getOriginalText: String = myOriginalText

    private var myOriginalText: String = null
    private var myBackupText: String = null
  }

}

class TextTool(newDrawingEditor: DrawingEditor, prototype: Figure) extends CreationTool(newDrawingEditor, prototype) {
  /**
   * If the pressed figure is a TextHolder it can be edited otherwise
   * a new text figure is created.
   */
  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    setView(e.getSource.asInstanceOf[DrawingView])
    if (getTypingTarget != null) {
      editor.toolDone
      return
    }
    var textHolder: TextHolder = null
    val pressedFigure: Figure = drawing.findFigureInside(x, y)
    if (pressedFigure != null) {
      textHolder = pressedFigure.getTextHolder
      setSelectedFigure(pressedFigure)
    }
    if ((textHolder != null) && textHolder.acceptsTyping) {
      beginEdit(textHolder)
    } else {
      super.mouseDown(e, x, y)
      view.checkDamage
      beginEdit(getCreatedFigure.getTextHolder)
    }
  }

  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {}

  override def mouseUp(e: MouseEvent, x: Int, y: Int) {
    if (!isActive) {
      editor.toolDone
    }
  }

  /**
   * Terminates the editing of a text figure.
   */
  override def deactivate {
    endEdit
    super.deactivate
  }

  /**
   * Sets the text cursor.
   */
  override def activate {
    super.activate
  }

  /**
   * Test whether the text tool is currently activated and is displaying
   * a overlay TextFigure for accepting input.
   *
   * @return true, if the text tool has a accepting target TextFigure for its input, false otherwise
   */
  override def isActive: Boolean = (getTypingTarget != null)

  protected def beginEdit(figure: TextHolder) {
    if (getFloatingTextField == null) {
      setFloatingTextField(createFloatingTextField)
    }
    if (figure != getTypingTarget && getTypingTarget != null) {
      endEdit
    }
    getFloatingTextField.createOverlay(view.asInstanceOf[Container], figure.getFont)
    getFloatingTextField.setBounds(fieldBounds(figure), figure.getText)
    setTypingTarget(figure)
  }

  protected def endEdit {
    if (getTypingTarget != null) {
      if (getAddedFigure != null) {
        if (!isDeleteTextFigure) {
          setUndoActivity(createPasteUndoActivity)
          getUndoActivity.setAffectedFigures(List(getAddedFigure))
          getTypingTarget.setText(getFloatingTextField.getText)
        }
      } else if (isDeleteTextFigure) {
        setUndoActivity(createDeleteUndoActivity)
        getUndoActivity.setAffectedFigures(List(getSelectedFigure))
        getUndoActivity.redo
      } else {
        setUndoActivity(createUndoActivity)
        getUndoActivity.setAffectedFigures(List(getTypingTarget.getRepresentingFigure))
        getTypingTarget.setText(getFloatingTextField.getText)
        (getUndoActivity.asInstanceOf[TextTool.UndoActivity]).setBackupText(getTypingTarget.getText)
      }
      setTypingTarget(null)
      getFloatingTextField.endOverlay
    } else {
      setUndoActivity(null)
    }
    setAddedFigure(null)
    setCreatedFigure(null)
    setSelectedFigure(null)
  }

  protected def isDeleteTextFigure: Boolean = getFloatingTextField.getText.length == 0

  private def fieldBounds(figure: TextHolder): Rectangle = {
    val box: Rectangle = figure.textDisplayBox
    val nChars: Int = figure.overlayColumns
    val d: Dimension = getFloatingTextField.getPreferredSize(nChars)
    new Rectangle(box.x, box.y, d.width, d.height)
  }

  protected def setTypingTarget(newTypingTarget: TextHolder) {
    myTypingTarget = newTypingTarget
  }

  protected def getTypingTarget: TextHolder = myTypingTarget

  private def setSelectedFigure(newSelectedFigure: Figure) {
    mySelectedFigure = newSelectedFigure
  }

  protected def getSelectedFigure: Figure = mySelectedFigure

  private def createFloatingTextField: FloatingTextField = new FloatingTextField

  private def setFloatingTextField(newFloatingTextField: FloatingTextField) {
    myTextField = newFloatingTextField
  }

  protected def getFloatingTextField: FloatingTextField = myTextField

  protected def createDeleteUndoActivity: Undoable = {
    val cmd: FigureTransferCommand = new DeleteCommand("Delete", editor)
    new DeleteCommand.UndoActivity(cmd)
  }

  protected def createPasteUndoActivity: Undoable = new PasteCommand.UndoActivity(view)

  /**
   * Factory method for undo activity
   */
  override protected def createUndoActivity: Undoable = new TextTool.UndoActivity(view, getTypingTarget.getText)

  private var myTextField: FloatingTextField = null
  private var myTypingTarget: TextHolder = null
  /**
   * The selected figure is different from the TextHolder as the TextHolder
   * may be included in a DecoratorFigure. Thus, the DecoratorFigure is selected
   * while the TextFigure is edited.
   */
  private var mySelectedFigure: Figure = null
}