/*
 * @(#)DrawingEditor.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

import org.shotdraw.util.UndoManager

/**
 * DrawingEditor defines the interface for coordinating
 * the different objects that participate in a drawing editor.
 *
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld022.htm>Mediator</a></b><br>
 * DrawingEditor is the mediator. It decouples the participants
 * of a drawing editor.
 *
 * @see Tool
 * @see DrawingView
 * @see Drawing
 *
 * @version <$CURRENT_VERSION$>
 */
trait DrawingEditor extends FigureSelectionListener {
  /**
   * Gets the editor's drawing view.
   */
  def view: DrawingView

  def views: Array[DrawingView]

  /**
   * Gets the editor's current tool.
   */
  def tool: Tool

  /**
   * Informs the editor that a tool has done its interaction.
   * This method can be used to switch back to the default tool.
   */
  def toolDone()

  /**
   * Informs that the current figure selection has changed.
   * Override this method to handle selection changes.
   */
  def figureSelectionChanged(view: DrawingView)

  def addViewChangeListener(vsl: ViewChangeListener)

  def removeViewChangeListener(vsl: ViewChangeListener)

  /**
   * Shows a status message in the editor's user interface
   */
  def showStatus(string: String)

  def getUndoManager: UndoManager
}

