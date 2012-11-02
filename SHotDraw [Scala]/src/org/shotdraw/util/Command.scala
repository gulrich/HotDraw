/*
 * @(#)Command.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import org.shotdraw.framework.DrawingEditor

/**
 * Commands encapsulate an action to be executed. Commands have
 * a name and can be used in conjunction with <i>Command enabled</i>
 * ui components.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld010.htm>Command</a></b><br>
 * Command is a simple instance of the command pattern without undo
 * support.
 * <hr>
 *
 * @see CommandButton
 * @see CommandMenu
 * @see CommandChoice
 *
 * @version <$CURRENT_VERSION$>
 */
abstract trait Command {
  /**
   * Executes the command.
   */
  def execute

  /**
   * Tests if the command can be executed.
   */
  def isExecutable: Boolean

  /**
   * Gets the command name.
   */
  def name: String

  def getDrawingEditor: DrawingEditor

  def getUndoActivity: Undoable

  def setUndoActivity(newUndoableActivity: Undoable)

  def addCommandListener(newCommandListener: CommandListener)

  def removeCommandListener(oldCommandListener: CommandListener)
}

