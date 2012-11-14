/*
 * @(#)NullDrawingView.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt._
import javax.swing.JPanel
import org.shotdraw.framework._

/**
 * This DrawingView provides a very basic implementation. It does not perform any
 * functionality apart from keeping track of its state represented by some important
 * fields. It is a Null-value object and is used instead of a null reference to
 * avoid null pointer exception. This concept is known as the Null-value object
 * bug pattern.
 *
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object NullDrawingView {
  def getManagedDrawingView(editor: DrawingEditor): DrawingView = {
    if (drawingViewManager.exists{case (de, dv) => de == editor}) {
      drawingViewManager.get(editor).asInstanceOf[DrawingView]
    } else {
      val newDrawingView = new NullDrawingView(editor)
      drawingViewManager += ((editor, newDrawingView))
      newDrawingView
    }
  }

  private var drawingViewManager = Map[DrawingEditor, DrawingView]()
}

class NullDrawingView(var myDrawingEditor: DrawingEditor) extends JPanel with DrawingView {
  /**
   * Sets the view's editor.
   */
  def setEditor(editor: DrawingEditor) {
    myDrawingEditor = editor
  }

  /**
   * Gets the current tool.
   */
  def tool: Tool = editor.tool

  /**
   * Gets the drawing.
   */
  def drawing: Drawing = myDrawing

  /**
   * Sets and installs another drawing in the view.
   */
  def setDrawing(d: Drawing) {
    myDrawing = d
  }

  /**
   * Gets the editor.
   */
  def editor: DrawingEditor = myDrawingEditor

  /**
   * Adds a figure to the drawing.
   * @return the added figure.
   */
  def add(figure: Figure): Figure = figure

  /**
   * Removes a figure from the drawing.
   * @return the removed figure
   */
  def remove(figure: Figure): Figure = figure

  /**
   * Adds a collection of figures to the drawing.
   */
  def addAll(figures: Iterable[Figure]) {}

  /**
   * Gets the size of the drawing.
   */
  override def getSize: Dimension = new Dimension

  /**
   * Gets the minimum dimension of the drawing.
   */
  override def getMinimumSize: Dimension = new Dimension

  /**
   * Gets the preferred dimension of the drawing..
   */
  override def getPreferredSize: Dimension = new Dimension

  /**
   * Sets the current display update strategy.
   * @see Painter
   */
  def setDisplayUpdate(newUpdateStrategy: Painter) {
    myUpdateStrategy = newUpdateStrategy
  }

  /**
   * Gets the current display update strategy.
   * @see Painter
   */
  def getDisplayUpdate: Painter = myUpdateStrategy

  /**
   * Gets an enumeration over the currently selected figures.
   * The selection is a snapshot of the current selection
   * which does not get changed anymore
   *
   * @return an enumeration with the currently selected figures.
   */
  def selection: Seq[Figure] = Seq()

  /**
   * Gets the currently seleced figures in Z order.
   * @see #selection
   * @return a Seq[Figure] with the selected figures. This enumeration
   *         represents a snapshot of the current selection.
   */
  def selectionZOrdered: Seq[Figure] = Seq()

  /**
   * Gets the number of selected figures.
   */
  def selectionCount: Int = 0
    
  /**
   * Test whether a given figure is selected.
   */
  def isFigureSelected(checkFigure: Figure): Boolean = false

  /**
   * Adds a figure to the current selection.
   */
  def addToSelection(figure: Figure) {}

  /**
   * Adds a Collection of figures to the current selection.
   */
  def addToSelectionAll(figures: Iterable[Figure]) {}

  /**
   * Adds a Seq[Figure] to the current selection.
   */
  def addToSelectionAll(fe: Seq[Figure]) {}

  /**
   * Removes a figure from the selection.
   */
  def removeFromSelection(figure: Figure) {}

  /**
   * If a figure isn't selected it is added to the selection.
   * Otherwise it is removed from the selection.
   */
  def toggleSelection(figure: Figure) {}

  /**
   * Clears the current selection.
   */
  def clearSelection {}

  /**
   * Gets the current selection as a FigureSelection. A FigureSelection
   * can be cut, copied, pasted.
   */
  def getFigureSelection: FigureSelection = new StandardFigureSelection(selection, 0)

  /**
   * Finds a handle at the given coordinates.
   * @return the hit handle, null if no handle is found.
   */
  def findHandle(x: Int, y: Int): Handle = null

  /**
   * Gets the position of the last click inside the view.
   */
  def lastClick: Point = new Point

  /**
   * Sets the current point constrainer.
   */
  def setConstrainer(p: PointConstrainer) {}

  /**
   * Gets the current grid setting.
   */
  def getConstrainer: PointConstrainer = null

  /**
   * Checks whether the drawing has some accumulated damage
   */
  def checkDamage {}

  /**
   * Repair the damaged area
   */
  def repairDamage {}

  /**
   * Paints the drawing view. The actual drawing is delegated to
   * the current update strategy.
   * @see Painter
   */
  override def paint(g: Graphics) {}

  /**
   * Creates an image with the given dimensions
   */
  override def createImage(width: Int, height: Int): Image = null

  /**
   * Gets a graphic to draw into
   */
  override def getGraphics: Graphics = null

  /**
   * Gets the background color of the DrawingView
   */
  override def getBackground: Color = myBackgroundColor

  /**
   * Sets the background color of the DrawingView
   */
  override def setBackground(c: Color) {
    myBackgroundColor = c
  }

  /**
   * Draws the contents of the drawing view.
   * The view has three layers: background, drawing, handles.
   * The layers are drawn in back to front order.
   */
  def drawAll(g: Graphics) {}

  /**
   * Draws the given figures.
   * The view has three layers: background, drawing, handles.
   * The layers are drawn in back to front order.
   */
  def draw(g: Graphics, fe: Seq[Figure]) {}

  /**
   * Draws the currently active handles.
   */
  def drawHandles(g: Graphics) {}

  /**
   * Draws the drawing.
   */
  def drawDrawing(g: Graphics) {}

  /**
   * Draws the background. If a background pattern is set it
   * is used to fill the background. Otherwise the background
   * is filled in the background color.
   */
  def drawBackground(g: Graphics) {}

  /**
   * Sets the cursor of the DrawingView
   */
  def setCursor(c: org.shotdraw.framework.Cursor) {}

  /**
   * Freezes the view by acquiring the drawing lock.
   * @see Drawing#lock
   */
  def freezeView {}

  /**
   * Unfreezes the view by releasing the drawing lock.
   * @see Drawing#unlock
   */
  def unfreezeView {}

  /**
   * Add a listener for selection changes in this DrawingView.
   * @param fsl jhotdraw.framework.FigureSelectionListener
   */
  def addFigureSelectionListener(fsl: FigureSelectionListener) {}

  /**
   * Remove a listener for selection changes in this DrawingView.
   * @param fsl jhotdraw.framework.FigureSelectionListener
   */
  def removeFigureSelectionListener(fsl: FigureSelectionListener) {}

  /**
   * Returns a Seq[Figure] of connection figures
   */
  def getConnectionFigures(inFigure: Figure): Seq[Figure] = Seq()

  /**
   * Inserts figures in a drawing at given offset. Optional check for connection figures
   *
   * @return enumeration which has been added to the drawing. The figures in the enumeration
   *         can have changed during adding them (e.g. they could have been decorated).
   */
  def insertFigures(inFigures: Seq[Figure], dx: Int, dy: Int, bCheck: Boolean): Seq[Figure] = Seq()

  def drawingInvalidated(e: DrawingChangeEvent) {}

  def drawingRequestUpdate(e: DrawingChangeEvent) {}

  def drawingTitleChanged(e: DrawingChangeEvent) {}

  def isInteractive: Boolean = false

  private var myDrawing: Drawing = new StandardDrawing
  private var myUpdateStrategy: Painter = null
  private var myBackgroundColor: Color = null
}

