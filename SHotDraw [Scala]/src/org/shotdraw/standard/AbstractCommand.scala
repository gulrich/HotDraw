/*
 * @(#)AbstractCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.framework.DrawingEditor
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.FigureSelectionListener
import org.shotdraw.framework.JHotDrawRuntimeException
import org.shotdraw.framework.ViewChangeListener
import org.shotdraw.util.Command
import org.shotdraw.util.CommandListener
import org.shotdraw.util.Undoable
import java.util.EventObject
import scala.collection.mutable.ArrayBuffer

/**
 * @author Helge Horch
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
object AbstractCommand {

  class EventDispatcher(myObservedCommand: Command) {

    def fireCommandExecutedEvent {
      myRegisteredListeners foreach { l =>
        l.commandExecuted(new EventObject(myObservedCommand))
      }
    }

    def fireCommandExecutableEvent {
      myRegisteredListeners foreach { l =>
        l.commandExecutable(new EventObject(myObservedCommand))
      }
    }

    def fireCommandNotExecutableEvent {
            myRegisteredListeners foreach { l =>
        l.commandNotExecutable(new EventObject(myObservedCommand))
      }
    }

    def addCommandListener(newCommandListener: CommandListener) {
      if (!myRegisteredListeners.contains(newCommandListener)) {
        myRegisteredListeners += newCommandListener
      }
    }

    def removeCommandListener(oldCommandListener: CommandListener) {
      if (myRegisteredListeners.contains(oldCommandListener)) {
        myRegisteredListeners = myRegisteredListeners diff List(oldCommandListener)
      }
    }

    private var myRegisteredListeners = ArrayBuffer[CommandListener]()
  }

}

class AbstractCommand(var myName: String, var myDrawingEditor: DrawingEditor, var myIsViewRequired: Boolean) extends Command with FigureSelectionListener {
  
  private var myUndoableActivity: Undoable = null
  private var myEventDispatcher: AbstractCommand.EventDispatcher = null
  
  getDrawingEditor.addViewChangeListener(createViewChangeListener)
  setEventDispatcher(createEventDispatcher)
  
  /**
   * Constructs a command with the given name that applies to the given view.
   * @param newName java.lang.String
   * @param newDrawingEditor the DrawingEditor which manages the views
   */
  def this(newName: String, newDrawingEditor: DrawingEditor) {
    this(newName, newDrawingEditor, true)
  }

  protected def viewSelectionChanged(oldView: DrawingView, newView: DrawingView) {
    if (oldView != null) {
      oldView.removeFigureSelectionListener(this)
    }
    if (newView != null) {
      newView.addFigureSelectionListener(this)
    }
    if (isViewRequired) {
      val isOldViewInteractive = (oldView != null) && oldView.isInteractive
      val isNewViewInteractive = (newView != null) && newView.isInteractive
      if (!isOldViewInteractive && isNewViewInteractive) {
        getEventDispatcher.fireCommandExecutableEvent
      } else if (isOldViewInteractive && !isNewViewInteractive) {
        getEventDispatcher.fireCommandNotExecutableEvent
      }
    }
  }

  /**
   * Sent when a new view is created
   */
  protected def viewCreated(view: DrawingView) {}

  /**
   * Send when an existing view is about to be destroyed.
   */
  protected def viewDestroying(view: DrawingView) {}

  /**
   * @param view a DrawingView
   */
  def figureSelectionChanged(view: DrawingView) {}

  /**
   * @return DrawingEditor associated with this command
   */
  def getDrawingEditor: DrawingEditor = myDrawingEditor

  private def setDrawingEditor(newDrawingEditor: DrawingEditor) {
    myDrawingEditor = newDrawingEditor
  }

  /**
   * Convenience method
   *
   * @return DrawingView currently active in the editor
   */
  def view: DrawingView = getDrawingEditor.view

  /**
   * Gets the command name.
   */
  def name: String = myName

  def setName(newName: String) {
    myName = newName
  }

  /**
   * Releases resources associated with this command
   */
  def dispose {
    if (view != null) {
      view.removeFigureSelectionListener(this)
    }
  }

  /**
   * Executes the command.
   */
  def execute {
    if (view == null) {
      throw new JHotDrawRuntimeException("execute should NOT be getting called when view() == null")
    }
  }

  /**
   * Tests if the command can be executed. The view must be valid when this is
   * called. Per default, a command is executable if at
   * least one figure is selected in the current activated
   * view.
   */
  def isExecutable: Boolean = {
    if (isViewRequired) {
      if ((view == null) || !view.isInteractive) {
        return false
      }
    }
    isExecutableWithView && !((isViewRequired) && ((view == null) || !view.isInteractive)) 
  }

  protected def isViewRequired: Boolean = myIsViewRequired

  protected def isExecutableWithView: Boolean = true

  def getUndoActivity: Undoable = myUndoableActivity

  def setUndoActivity(newUndoableActivity: Undoable) {
    myUndoableActivity = newUndoableActivity
  }

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

  protected def createEventDispatcher: AbstractCommand.EventDispatcher = new AbstractCommand.EventDispatcher(this)

  protected def createViewChangeListener: ViewChangeListener = new ViewChangeListener {
    def viewSelectionChanged(oldView: DrawingView, newView: DrawingView) {
      AbstractCommand.this.viewSelectionChanged(oldView, newView)
    }
    def viewCreated(view: DrawingView) {
      AbstractCommand.this.viewCreated(view)
    }
    def viewDestroying(view: DrawingView) {
      AbstractCommand.this.viewDestroying(view)
    }
  }
}