/*
 * @(#)UndoableCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import java.util.EventObject

import org.shotdraw.framework.DrawingEditor
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.FigureSelectionListener
import org.shotdraw.standard.AbstractCommand

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class UndoableCommand extends Command with FigureSelectionListener with CommandListener {
  private var myWrappedCommand: Command = null
  private var hasSelectionChanged: Boolean = false
  private var myEventDispatcher: AbstractCommand.EventDispatcher = null
  
  def this(newWrappedCommand: Command) {
    this()
    setWrappedCommand(newWrappedCommand)
    getWrappedCommand.addCommandListener(this)
    setEventDispatcher(createEventDispatcher)
  }

  /**
   * Executes the command.
   */
  def execute {
    hasSelectionChanged = false
    view.addFigureSelectionListener(this)
    getWrappedCommand.execute
    val undoableCommand = getWrappedCommand.getUndoActivity
    if ((undoableCommand != null) && (undoableCommand.isUndoable)) {
      getDrawingEditor.getUndoManager.pushUndo(undoableCommand)
      getDrawingEditor.getUndoManager.clearRedos
    }
    if (!hasSelectionChanged || (getDrawingEditor.getUndoManager.getUndoSize == 1)) {
      getDrawingEditor.figureSelectionChanged(view)
    }
    view.removeFigureSelectionListener(this)
  }

  /**
   * Tests if the command can be executed.
   */
  def isExecutable: Boolean = getWrappedCommand.isExecutable

  protected def setWrappedCommand(newWrappedCommand: Command) {
    myWrappedCommand = newWrappedCommand
  }

  protected def getWrappedCommand: Command = myWrappedCommand

  /**
   * Gets the command name.
   */
  def name: String = getWrappedCommand.name

  def getDrawingEditor: DrawingEditor = getWrappedCommand.getDrawingEditor

  def view: DrawingView = {
    return getDrawingEditor.view
  }

  def figureSelectionChanged(view: DrawingView) {
    hasSelectionChanged = true
  }

  def getUndoActivity: Undoable = new UndoableAdapter(view)

  def setUndoActivity(newUndoableActivity: Undoable) {}

  def addCommandListener(newCommandListener: CommandListener) {
    getEventDispatcher.addCommandListener(newCommandListener)
  }

  def removeCommandListener(oldCommandListener: CommandListener) {
    getEventDispatcher.removeCommandListener(oldCommandListener)
  }

  private def setEventDispatcher(newEventDispatcher: AbstractCommand.EventDispatcher) {
    myEventDispatcher = newEventDispatcher
  }

  protected def getEventDispatcher: AbstractCommand.EventDispatcher = myEventDispatcher

  def createEventDispatcher: AbstractCommand.EventDispatcher = {
    return new AbstractCommand.EventDispatcher(this)
  }

  def commandExecuted(commandEvent: EventObject) {
    getEventDispatcher.fireCommandExecutedEvent
  }

  def commandExecutable(commandEvent: EventObject) {
    getEventDispatcher.fireCommandExecutableEvent
  }

  def commandNotExecutable(commandEvent: EventObject) {
    getEventDispatcher.fireCommandNotExecutableEvent
  }

}

