/*
 * @(#)DrawingView.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.framework

import java.awt.image.ImageObserver
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.Point

/**
 * DrawingView renders a Drawing and listens to its changes.
 * It receives user input and delegates it to the current tool.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld026.htm>Observer</a></b><br>
 * DrawingView observes drawing for changes via the DrawingListener interface.<br>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld032.htm>State</a></b><br>
 * DrawingView plays the role of the StateContext in
 * the State pattern. Tool is the State.<br>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld034.htm>Strategy</a></b><br>
 * DrawingView is the StrategyContext in the Strategy pattern
 * with regard to the UpdateStrategy. <br>
 * DrawingView is the StrategyContext for the PointConstrainer.
 *
 * @see Drawing
 * @see Painter
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */
abstract trait DrawingView extends ImageObserver with DrawingChangeListener {
  /**
   * Sets the view's editor.
   */
  def setEditor(editor: DrawingEditor)

  /**
   * Gets the current tool.
   */
  def tool: Tool

  /**
   * Gets the drawing.
   */
  def drawing: Drawing

  /**
   * Sets and installs another drawing in the view.
   */
  def setDrawing(d: Drawing)

  /**
   * Gets the editor.
   */
  def editor: DrawingEditor

  /**
   * Adds a figure to the drawing.
   * @return the added figure.
   */
  def add(figure: Figure): Figure

  /**
   * Removes a figure from the drawing.
   * @return the removed figure
   */
  def remove(figure: Figure): Figure

  /**
   * Adds a collections of figures to the drawing.
   */
  def addAll(figures: Collection[Figure])

  /**
   * Gets the size of the drawing.
   */
  def getSize: Dimension

  /**
   * Gets the minimum dimension of the drawing.
   */
  def getMinimumSize: Dimension

  /**
   * Gets the preferred dimension of the drawing..
   */
  def getPreferredSize: Dimension

  /**
   * Sets the current display update strategy.
   * @see Painter
   */
  def setDisplayUpdate(updateStrategy: Painter)

  /**
   * Gets the current display update strategy.
   * @see Painter
   */
  def getDisplayUpdate: Painter

  /**
   * Gets an enumeration over the currently selected figures.
   * The selection is a snapshot of the current selection
   * which does not get changed anymore
   *
   * @return an enumeration with the currently selected figures.
   */
  def selection: Seq[Figure]

  /**
   * Gets the currently seleced figures in Z order.
   * The selection is a snapshot of the current selection
   * which does not get changed anymore
   *
   * @see #selection
   * @return an enumeration with the currently selected figures.
   */
  def selectionZOrdered: Seq[Figure]

  /**
   * Gets the number of selected figures.
   */
  def selectionCount: Int

  /**
   * Test whether a given figure is selected.
   */
  def isFigureSelected(checkFigure: Figure): Boolean

  /**
   * Adds a figure to the current selection.
   */
  def addToSelection(figure: Figure)

  /**
   * Adds a collections of figures to the current selection.
   */
  def addToSelectionAll(figures: Collection[Figure])

  /**
   * Adds a Seq[Figure] to the current selection.
   */
  def addToSelectionAll(fe: Seq[Figure])

  /**
   * Removes a figure from the selection.
   */
  def removeFromSelection(figure: Figure)

  /**
   * If a figure isn't selected it is added to the selection.
   * Otherwise it is removed from the selection.
   */
  def toggleSelection(figure: Figure)

  /**
   * Clears the current selection.
   */
  def clearSelection

  /**
   * Gets the current selection as a FigureSelection. A FigureSelection
   * can be cut, copied, pasted.
   */
  def getFigureSelection: FigureSelection

  /**
   * Finds a handle at the given coordinates.
   * @return the hit handle, null if no handle is found.
   */
  def findHandle(x: Int, y: Int): Handle

  /**
   * Gets the position of the last click inside the view.
   */
  def lastClick: Point

  /**
   * Sets the current point constrainer.
   */
  def setConstrainer(p: PointConstrainer)

  /**
   * Gets the current grid setting.
   */
  def getConstrainer: PointConstrainer

  /**
   * Checks whether the drawing has some accumulated damage
   */
  def checkDamage

  /**
   * Repair the damaged area
   */
  def repairDamage

  /**
   * Paints the drawing view. The actual drawing is delegated to
   * the current update strategy.
   * @see Painter
   */
  def paint(g: Graphics)

  /**
   * Creates an image with the given dimensions
   */
  def createImage(width: Int, height: Int): Image

  /**
   * Gets a graphic to draw into
   */
  def getGraphics: Graphics

  /**
   * Gets the background color of the DrawingView
   */
  def getBackground: Color

  /**
   * Sets the background color of the DrawingView
   */
  def setBackground(c: Color)

  /**
   * Draws the contents of the drawing view.
   * The view has three layers: background, drawing, handles.
   * The layers are drawn in back to front order.
   */
  def drawAll(g: Graphics)

  /**
   * Draws the given figures.
   * The view has three layers: background, drawing, handles.
   * The layers are drawn in back to front order.
   */
  def draw(g: Graphics, fe: Seq[Figure])

  /**
   * Draws the currently active handles.
   */
  def drawHandles(g: Graphics)

  /**
   * Draws the drawing.
   */
  def drawDrawing(g: Graphics)

  /**
   * Draws the background. If a background pattern is set it
   * is used to fill the background. Otherwise the background
   * is filled in the background color.
   */
  def drawBackground(g: Graphics)

  /**
   * Sets the cursor of the DrawingView
   */
  def setCursor(c: Cursor)

  /**
   * Freezes the view by acquiring the drawing lock.
   * @see Drawing#lock
   */
  def freezeView

  /**
   * Unfreezes the view by releasing the drawing lock.
   * @see Drawing#unlock
   */
  def unfreezeView

  /**
   * Add a listener for selection changes in this DrawingView.
   * @param fsl jhotdraw.framework.FigureSelectionListener
   */
  def addFigureSelectionListener(fsl: FigureSelectionListener)

  /**
   * Remove a listener for selection changes in this DrawingView.
   * @param fsl jhotdraw.framework.FigureSelectionListener
   */
  def removeFigureSelectionListener(fsl: FigureSelectionListener)

  /**
   * Returns a Seq[Figure] of connection figures
   */
  def getConnectionFigures(inFigure: Figure): Seq[Figure]

  /**
   * Inserts figures in a drawing at given offset. Optional check for connection figures
   *
   * @return enumeration which has been added to the drawing. The figures in the enumeration
   *         can have changed during adding them (e.g. they could have been decorated).
   */
  def insertFigures(inFigures: Seq[Figure], dx: Int, dy: Int, bCheck: Boolean): Seq[Figure]

  /**
   * Check whether the DrawingView is interactive, i.e. whether it accepts user input
   * and whether it can display a drawing.
   */
  def isInteractive: Boolean
}

