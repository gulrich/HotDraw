/*
 * @(#)UndoableAdapter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	? by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.standard.StandardFigureSelection
import scala.collection.mutable.ArrayBuffer

/**
 * Most basic implementation for an Undoable activity. Subclasses should override
 * methods to provide specialized behaviour when necessary.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class UndoableAdapter(newDrawingView: DrawingView) extends Undoable {
  private var myDrawingView: DrawingView = null
  private var myAffectedFigures = ArrayBuffer[Figure]()
  private var myIsUndoable = false
  private var myIsRedoable = false
  setDrawingView(newDrawingView)

  /**
   * Undo the activity
   * @ true if the activity could be undone, false otherwise
   */
  def undo: Boolean = isUndoable

  /**
   * Redo the activity
   * @ true if the activity could be redone, false otherwise
   */
  def redo: Boolean = isRedoable

  def isUndoable: Boolean = myIsUndoable

  def setUndoable(newIsUndoable: Boolean) {myIsUndoable = newIsUndoable}

  def isRedoable: Boolean = myIsRedoable

  def setRedoable(newIsRedoable: Boolean) {myIsRedoable = newIsRedoable}

  def setAffectedFigures(newAffectedFigures: Seq[Figure]) {
    if (newAffectedFigures == null) {
      throw new IllegalArgumentException
    }
    rememberFigures(newAffectedFigures)
  }

  def getAffectedFigures: Seq[Figure] = {
    if (myAffectedFigures == null) Seq()
    else myAffectedFigures
  }

  def getAffectedFiguresReversed: Seq[Figure] = myAffectedFigures.reverse

  def getAffectedFiguresCount: Int = myAffectedFigures.size

  protected def rememberFigures(toBeRemembered: Seq[Figure]) {
    myAffectedFigures.clear
    myAffectedFigures appendAll toBeRemembered
  }

  /**
   * Releases all resources related to an undoable activity
   */
  def release() {
    getAffectedFigures foreach { e => e release}
    setAffectedFigures(Seq())
  }

  /**
   * Create new set of affected figures for redo operation because
   * deleting figures in an undo operation makes them unusable
   * Especially contained figures have been removed from their
   * observing container like CompositeFigure or DecoratorFigure.
   * Duplicating these figures re-establishes the dependencies.
   */
  protected def duplicateAffectedFigures() {
    setAffectedFigures(StandardFigureSelection.duplicateFigures(getAffectedFigures, getAffectedFiguresCount))
  }

  def getDrawingView: DrawingView = myDrawingView
  
  protected def setDrawingView(newDrawingView: DrawingView) {
    myDrawingView = newDrawingView
  }
}