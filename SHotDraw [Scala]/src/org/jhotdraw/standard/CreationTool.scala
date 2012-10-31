/*
 * @(#)CreationTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import java.awt.Point
import java.awt.event.MouseEvent
import org.jhotdraw.framework.DrawingEditor
import org.jhotdraw.framework.Figure
import org.jhotdraw.framework.JHotDrawRuntimeException
import org.jhotdraw.util.Undoable
import scala.collection.mutable.ArrayBuffer

/**
 * A tool to create new figures. The figure to be
 * created is specified by a prototype.
 *
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld029.htm>Prototype</a></b><br>
 * CreationTool creates new figures by cloning a prototype.
 * <hr>
 *
 * @see Figure
 * @see Object#clone
 *
 * @version <$CURRENT_VERSION$>
 */
class CreationTool(newDrawingEditor: DrawingEditor, prototype: Figure) extends AbstractTool(newDrawingEditor) {
  /**
   * the list of currently added figures
   * by: ricardo_padilha.
   * description: This has been added to provide support for creation tools that
   * insert more than one figure to the drawing, for example, by
   * maintaining SHIFT down and clicking. However, this class still
   * maintains its normal behavior of creating only one figure.
   */
  private var fAddedFigures: ArrayBuffer[Figure] = ArrayBuffer()
  /**
   * the currently created figure
   */
  private var fCreatedFigure: Figure = null
  /**
   * the figure that was actually added
   * Note, this can be a different figure from the one which has been created.
   */
  private var myAddedFigure: Figure = null
  /**
   * the prototypical figure that is used to create new figuresthe prototypical figure that is used to create new figures.
   */
  private var myPrototypeFigure: Figure = null
 
  setPrototypeFigure(prototype)
  
  /**
   * Constructs a CreationTool without a prototype.
   * This is for subclassers overriding createFigure.
   */
  protected def this(newDrawingEditor: DrawingEditor) {
    this(newDrawingEditor, null)
  }

  /**
   * Sets the cross hair cursor.
   */
  override def activate {
    super.activate
    if (isUsable) {
      getActiveView.setCursor(new AWTCursor(java.awt.Cursor.CROSSHAIR_CURSOR))
    }
    setAddedFigures(ArrayBuffer[Figure]())
  }

  /**
   * @see org.jhotdraw.framework.Tool#deactivate()
   */
  override def deactivate {
    setCreatedFigure(null)
    setAddedFigure(null)
    setAddedFigures(null)
    super.deactivate
  }

  /**
   * Creates a new figure by cloning the prototype.
   */
  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    super.mouseDown(e, x, y)
    setCreatedFigure(createFigure)
    setAddedFigure(getActiveView.add(getCreatedFigure))
    getAddedFigure.displayBox(new Point(getAnchorX, getAnchorY), new Point(getAnchorX, getAnchorY))
  }

  /**
   * Creates a new figure by cloning the prototype.
   */
  protected def createFigure: Figure = {
    if (getPrototypeFigure == null) {
      throw new JHotDrawRuntimeException("No protoype defined")
    }
    getPrototypeFigure.clone.asInstanceOf[Figure]
  }

  /**
   * Adjusts the extent of the created figure
   */
  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {
    if (getAddedFigure != null) {
      getAddedFigure.displayBox(new Point(getAnchorX, getAnchorY), new Point(x, y))
    }
  }

  /**
   * Checks if the created figure is empty. If it is, the figure
   * is removed from the drawing.
   * @see Figure#isEmpty
   */
  override def mouseUp(e: MouseEvent, x: Int, y: Int) {
    if (getAddedFigure != null && !getCreatedFigure.isEmpty) {
      fAddedFigures += getAddedFigure
    } else {
      getActiveView.remove(getAddedFigure)
    }
    if (getAddedFigures.isEmpty) {
      setUndoActivity(null)
    } else {
      setUndoActivity(createUndoActivity)
      getUndoActivity.setAffectedFigures(getAddedFigures)
    }
    editor.toolDone
  }

  /**
   * As the name suggests this CreationTool uses the Prototype design pattern.
   * Thus, the prototype figure which is used to create new figures of the same
   * type by cloning the original prototype figure.
   * @param newPrototypeFigure figure to be cloned to create new figures
   */
  protected def setPrototypeFigure(newPrototypeFigure: Figure) {
    myPrototypeFigure = newPrototypeFigure
  }

  /**
   * As the name suggests this CreationTool uses the Prototype design pattern.
   * Thus, the prototype figure which is used to create new figures of the same
   * type by cloning the original prototype figure.
   * @return figure to be cloned to create new figures
   */
  protected def getPrototypeFigure: Figure = myPrototypeFigure

  /**
   * Gets the list of currently added figure
   */
  protected def getAddedFigures: ArrayBuffer[Figure] = fAddedFigures

  /**
   * Sets the addedFigures attribute of the CreationTool object
   */
  protected def setAddedFigures(newAddedFigures: ArrayBuffer[Figure]) {
    fAddedFigures = newAddedFigures
  }

  /**
   * Gets the currently created figure
   */
  protected def getCreatedFigure: Figure = fCreatedFigure

  /**
   * Sets the createdFigure attribute of the CreationTool object
   */
  protected def setCreatedFigure(newCreatedFigure: Figure) {
    fCreatedFigure = newCreatedFigure
  }

  /**
   * Gets the figure that was actually added
   * Note, this can be a different figure from the one which has been created.
   */
  protected def getAddedFigure: Figure = myAddedFigure

  /**
   * Sets the addedFigure attribute of the CreationTool object
   */
  protected def setAddedFigure(newAddedFigure: Figure) {
    myAddedFigure = newAddedFigure
  }

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new PasteCommand.UndoActivity(getActiveView)

}