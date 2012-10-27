/*
 * @(#)UndoManager.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import org.jhotdraw.framework.DrawingView

/**
 * This class manages all the undoable commands. It keeps track of all
 * the modifications done through user interactions.
 *
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object UndoManager {
  /**
   * Maximum default buffer size for undo and redo stack
   */
  final val DEFAULT_BUFFER_SIZE: Int = 20
}

class UndoManager(maxStackCapacity: Int) {
  
  def this() {
    this(UndoManager.DEFAULT_BUFFER_SIZE)
  }

  def pushUndo(undoActivity: Undoable) {
    if (undoActivity.isUndoable) {
      undoStack = removeFirstElementInFullList(undoStack)
      undoStack ::= undoActivity
    } else undoStack = List()
  }

  def pushRedo(redoActivity: Undoable) {
    if (redoActivity.isRedoable) {
      redoStack = removeFirstElementInFullList(redoStack)
      if ((getRedoSize == 0) || (peekRedo != redoActivity)) {
        redoStack ::= redoActivity
      }
    } else redoStack = List()
  }

  /**
   * If buffersize exceeds, remove the oldest command
   */
  private def removeFirstElementInFullList(l: List[Undoable]): List[Undoable] = {
    if (l.size >= maxStackCapacity) {
      l.head.release
      l.tail
    }
    l
  }

  private def getLastElement(l: List[Undoable]): Option[Undoable] = l.lastOption

  def isUndoable: Boolean = getLastElement(undoStack).isDefined && getLastElement(undoStack).get.isUndoable

  def isRedoable: Boolean = getLastElement(redoStack).isDefined && getLastElement(redoStack).get.isRedoable 

  protected def peekUndo: Undoable = getLastElement(undoStack) match {
    case Some(undoable) => undoable
    case None => null
  }

  protected def peekRedo: Undoable = getLastElement(redoStack) match {
    case Some(redoable) => redoable
    case None => null
  }
  /**
   * Returns the current size of undo buffer.
   */
  def getUndoSize: Int = undoStack.size

  /**
   * Returns the current size of redo buffer.
   */
  def getRedoSize: Int = redoStack.size

  /**
   * Throw NoSuchElementException if there is none
   */
  def popUndo: Undoable = {
    val lastUndoable: Undoable = peekUndo
    undoStack = undoStack.init
    lastUndoable
  }

  /**
   * Throw NoSuchElementException if there is none
   */
  def popRedo: Undoable = {
    val lastUndoable: Undoable = peekRedo
    redoStack = redoStack.init
    lastUndoable
  }

  def clearUndos {undoStack = List()}

  def clearRedos {redoStack = List()}

  /**
   * Removes all undo activities that operate on the given DrawingView.
   * @param checkDV DrawingView which is compared undo's DrawingView
   */
  def clearUndos(checkDV: DrawingView) {undoStack.remove(e => e.getDrawingView == checkDV)}

  /**
   * Removes all redo activities that operate on the given DrawingView.
   * @param checkDV DrawingView which is compared redo's DrawingView
   */
  def clearRedos(checkDV: DrawingView) {redoStack.remove(e => e.getDrawingView == checkDV)}

  /**
   * Collection of undo activities
   */
  private var redoStack: List[Undoable] = List[Undoable]()
  /**
   * Collection of undo activities
   */
  private var undoStack: List[Undoable] = List[Undoable]()
}

