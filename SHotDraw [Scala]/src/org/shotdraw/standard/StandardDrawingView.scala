/*
 * @(#)StandardDrawingView.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	? by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Insets
import java.awt.Point
import java.awt.PrintGraphics
import java.awt.Rectangle
import java.awt.dnd.DragGestureListener
import java.awt.dnd.DragSourceListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.io.IOException
import java.io.ObjectInputStream
import javax.swing.JOptionPane
import javax.swing.JPanel
import org.shotdraw.contrib.AutoscrollHelper
import org.shotdraw.contrib.dnd.DNDHelper
import org.shotdraw.contrib.dnd.DNDInterface
import org.shotdraw.framework.ConnectionFigure
import org.shotdraw.framework.Connector
import org.shotdraw.framework.Cursor
import org.shotdraw.framework.Drawing
import org.shotdraw.framework.DrawingChangeEvent
import org.shotdraw.framework.DrawingChangeListener
import org.shotdraw.framework.DrawingEditor
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.framework.FigureSelection
import org.shotdraw.framework.FigureSelectionListener
import org.shotdraw.framework.Handle
import org.shotdraw.framework.Painter
import org.shotdraw.framework.PointConstrainer
import org.shotdraw.framework.Tool
import org.shotdraw.util.Command
import org.shotdraw.util.Geom
import org.shotdraw.util.UndoableCommand
import java.lang.Object
import scala.collection.mutable.ArrayBuffer

/**
 * The standard implementation of DrawingView.
 *
 * @see DrawingView
 * @see Painter
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */
object StandardDrawingView {
  /**
   * Scrolling increment
   */
  final val MINIMUM_WIDTH: Int = 400
  final val MINIMUM_HEIGHT: Int = 300
  final val SCROLL_INCR: Int = 100
  final val SCROLL_OFFSET: Int = 10
  
  private var count: Int = 0
  def counter = {
    count = count+1
    count
  }
}

class StandardDrawingView(var newEditor: DrawingEditor, width: Int, height: Int) extends JPanel with DrawingView with DNDInterface with java.awt.dnd.Autoscroll {
  import StandardDrawingView._
    
  /**
   * The DrawingEditor of the view.
   * @see #tool
   */
  @transient
  private var fEditor: DrawingEditor = newEditor
  /**
   * the registered listeners for selection changes
   */
  @transient
  private var fSelectionListeners: ArrayBuffer[FigureSelectionListener] = ArrayBuffer[FigureSelectionListener]()
  /**
   * The shown drawing.
   */
  private var fDrawing: Drawing = null
  /**
   * the accumulated damaged area
   */
  @transient
  private var fDamage: Rectangle = null
  /**
   * The list of currently selected figures.
   */
  @transient
  private var fSelection: ArrayBuffer[Figure] = ArrayBuffer[Figure]()
  /**
   * The shown selection handles.
   */
  @transient
  private var fSelectionHandles: ArrayBuffer[Handle] = ArrayBuffer()
  /**
   * The position of the last mouse click
   * inside the view.
   */
  private var fLastClick: Point = null
  /**
   * A List of optional backgrounds. The list contains
   * view painters that are drawn before the contents,
   * that is in the background.
   */
  private var fBackgrounds: ArrayBuffer[Painter] = ArrayBuffer()
  /**
   * A List of optional foregrounds. The list contains
   * view painters that are drawn after the contents,
   * that is in the foreground.
   */
  private var fForegrounds: ArrayBuffer[Painter] = ArrayBuffer()
  /**
   * The update strategy used to repair the view.
   */
  private var fUpdateStrategy: Painter = null
  /**
   * The grid used to constrain points for snap to
   * grid functionality.
   */
  private var fConstrainer: PointConstrainer = null
  private var myCounter: Int = StandardDrawingView.counter
  private var dndh: DNDHelper = null
  /**
   * Listener for mouse clicks.
   */
  private var myMouseListener: MouseListener = null
  /**
   * Listener for mouse movements.
   */
  private var motionListener: MouseMotionListener = null
  /**
   * Listener for the keyboard.
   */
  private var myKeyListener: KeyListener = null
  /**
   * Reflects whether the drawing view is in read-only mode (from a user's
   * perspective).
   */
  private var myIsReadOnly: Boolean = false
  /** *** Autoscroll support *****/
  private var ash: StandardDrawingView#ASH = new StandardDrawingView.this.ASH(10)
  
  setAutoscrolls(true)
  setPreferredSize(new Dimension(width, height))
  fSelectionListeners = ArrayBuffer[FigureSelectionListener]()
  addFigureSelectionListener(fEditor)
  setLastClick(new Point(0, 0))
  fConstrainer = null
  fSelection = ArrayBuffer()
  setDisplayUpdate(createDisplayUpdate)
  setBackground(Color.lightGray)
  addMouseListener(createMouseListener)
  addMouseMotionListener(createMouseMotionListener)
  addKeyListener(createKeyListener)
  
  /**
   * Constructs the view.
   */
  def this(editor: DrawingEditor) {
    this(editor, StandardDrawingView.MINIMUM_WIDTH, StandardDrawingView.MINIMUM_HEIGHT)
  }


  protected def createMouseListener: MouseListener = {
    myMouseListener = new StandardDrawingView.this.DrawingViewMouseListener
    myMouseListener
  }

  protected def createMouseMotionListener: MouseMotionListener = {
    motionListener = new StandardDrawingView.this.DrawingViewMouseMotionListener
    motionListener
  }

  protected def createKeyListener: KeyListener = {
    myKeyListener = new DrawingViewKeyListener
    myKeyListener
  }

  /**
   * Factory method which can be overriden by subclasses
   */
  protected def createDisplayUpdate: Painter = new SimpleUpdateStrategy

  /**
   * Sets the view's editor.
   */
  def setEditor(editor: DrawingEditor) {
    fEditor = editor
  }

  /**
   * Gets the current tool.
   */
  def tool: Tool = editor.tool

  /**
   * Gets the drawing.
   */
  def drawing: Drawing = fDrawing

  /**
   * Sets and installs another drawing in the view.
   */
  def setDrawing(d: Drawing) {
    if (drawing != null) {
      clearSelection
      drawing.removeDrawingChangeListener(this)
    }
    fDrawing = d
    if (drawing != null) {
      drawing.addDrawingChangeListener(this)
    }
    checkMinimumSize
    this.repaint()
  }

  /**
   * Gets the editor.
   */
  def editor: DrawingEditor = fEditor

  /**
   * Adds a figure to the drawing.
   * @return the added figure.
   */
  def add(figure: Figure): Figure = drawing.add(figure)

  /**
   * Removes a figure from the drawing.
   * @return the removed figure
   */
  def remove(figure: Figure): Figure = drawing.remove(figure)

  /**
   * Adds a Collection of figures to the drawing.
   */
  def addAll(figures: Collection[Figure]) {
    figures foreach { f => add(f)}
  }

  /**
   * Check existance of figure in the drawing
   */
  def figureExists(inf: Figure, fe: Seq[Figure]): Boolean = {
    fe.find(figure => figure.includes(inf)).isDefined
  }

  /**
   * Inserts a Seq[Figure] of figures and translates them by the
   * given offset. This function is used to insert figures from clipboards (cut/copy)
   *
   * @return enumeration which has been added to the drawing. The figures in the enumeration
   *         can have changed during adding them (e.g. they could have been decorated).
   */
  def insertFigures(fe: Seq[Figure], dx: Int, dy: Int, bCheck: Boolean): Seq[Figure] = {
    if (fe == null) {
      return Seq[Figure]()
    }
    var vCF: ArrayBuffer[ConnectionFigure] = ArrayBuffer[ConnectionFigure]()
    val visitor: InsertIntoDrawingVisitor = new InsertIntoDrawingVisitor(drawing)
    fe foreach (f => f match {
      case cf: ConnectionFigure => vCF += cf
      case _ if f != null => 
        f.moveBy(dx, dy)
        f.visit(visitor)
    })
    vCF foreach { cf =>
      val sf: Figure = cf.startFigure
      val ef: Figure = cf.endFigure
      if (figureExists(sf, drawing.figures) && figureExists(ef, drawing.figures) && (!bCheck || cf.canConnect(sf, ef))) {
        if (bCheck) {
          val sp: Point = sf.center
          val ep: Point = ef.center
          val fStartConnector: Connector = cf.startFigure.connectorAt(ep.x, ep.y)
          val fEndConnector: Connector = cf.endFigure.connectorAt(sp.x, sp.y)
          if (fEndConnector != null && fStartConnector != null) {
            cf.connectStart(fStartConnector)
            cf.connectEnd(fEndConnector)
            cf.updateConnection
          }
        }
        cf.visit(visitor)
      }
    }
    addToSelectionAll(visitor.getInsertedFigures)
    return visitor.getInsertedFigures
  }

  /**
   * Returns a Seq[Figure] of connectionfigures attached to this figure
   */
  def getConnectionFigures(inFigure: Figure): Seq[Figure] = {
    if (inFigure == null || !inFigure.canConnect) {
      return null
    }
    var result: ArrayBuffer[Figure] = ArrayBuffer[Figure]()
    drawing.figures foreach (f => f match {
      case cf: ConnectionFigure if !(isFigureSelected(f)) =>
        if (cf.startFigure.includes(inFigure) || cf.endFigure.includes(inFigure)) result += f
      case _ =>
    })
    result
  }

  /**
   * Sets the current display update strategy.
   * @see Painter
   */
  def setDisplayUpdate(updateStrategy: Painter) {
    fUpdateStrategy = updateStrategy
  }

  /**
   * Sets the current display update strategy.
   * @see Painter
   */
  def getDisplayUpdate: Painter = fUpdateStrategy

  /**
   * Gets an enumeration over the currently selected figures.
   * The selection is a snapshot of the current selection
   * which does not get changed anymore
   *
   * @return an enumeration with the currently selected figures.
   */
  def selection: Seq[Figure] = selectionZOrdered

  /**
   * Gets the currently selected figures in Z order.
   * @see #selection
   * @return a Seq[Figure] with the selected figures. The enumeration
   *         represents a snapshot of the current selection.
   */
  def selectionZOrdered: Seq[Figure] = fSelection.reverse

  /**
   * Gets the number of selected figures.
   */
  def selectionCount: Int = fSelection.size

  /**
   * Test whether a given figure is selected.
   */
  def isFigureSelected(checkFigure: Figure): Boolean = fSelection.contains(checkFigure)

  /**
   * Adds a figure to the current selection. The figure is only selected if
   * it is also contained in the Drawing associated with this DrawingView.
   */
  def addToSelection(figure: Figure) {
    if (addToSelectionImpl(figure) == true) {
      fireSelectionChanged
    }
  }

  protected def addToSelectionImpl(figure: Figure): Boolean = {
    var changed: Boolean = false
    if (!isFigureSelected(figure) && drawing.includes(figure)) {
      fSelection += figure
      fSelectionHandles = null
      figure.invalidate
      changed = true
    }
    changed
  }

  /**
   * Adds a Collection of figures to the current selection.
   */
  def addToSelectionAll(figures: Collection[Figure]) {
    addToSelectionAll(figures)
  }

  /**
   * Adds a Seq[Figure] to the current selection.
   */
  def addToSelectionAll(fe: Seq[Figure]) {
    var changed: Boolean = false
    fe foreach { f => changed |= addToSelectionImpl(f)}
    if (changed) {
      fireSelectionChanged
    }
  }

  /**
   * Removes a figure from the selection.
   */
  def removeFromSelection(figure: Figure) {
    if (isFigureSelected(figure)) {
      fSelection = fSelection diff List(figure)
      fSelectionHandles = null
      figure.invalidate
      fireSelectionChanged
    }
  }

  /**
   * If a figure isn't selected it is added to the selection.
   * Otherwise it is removed from the selection.
   */
  def toggleSelection(figure: Figure) {
    if (isFigureSelected(figure)) {
      removeFromSelection(figure)
    } else {
      addToSelection(figure)
    }
    fireSelectionChanged
  }

  /**
   * Clears the current selection.
   */
  def clearSelection {
    if (selectionCount == 0) {
      return
    }
    selection foreach { f =>
      f.invalidate
    }
    fSelection = ArrayBuffer[Figure]()
    fSelectionHandles = null
    fireSelectionChanged
  }

  /**
   * Gets an enumeration of the currently active handles.
   */
  protected def selectionHandles: Seq[Handle] = {
    if (fSelectionHandles == null) {
      fSelectionHandles = ArrayBuffer[Handle]()
      selection foreach { f =>
        f.handles foreach { h =>
          fSelectionHandles += h
        }
      }
    }
    fSelectionHandles
  }

  /**
   * Gets the current selection as a FigureSelection. A FigureSelection
   * can be cut, copied, pasted.
   */
  def getFigureSelection: FigureSelection = new StandardFigureSelection(selectionZOrdered, selectionCount)

  /**
   * Finds a handle at the given coordinates.
   * @return the hit handle, null if no handle is found.
   */
  def findHandle(x: Int, y: Int): Handle = selectionHandles.find(h => h.containsPoint(x,y)) match {
    case Some(handle) => handle
    case _ => null
  }

  /**
   * Informs that the current selection changed.
   * By default this event is forwarded to the
   * drawing editor.
   */
  protected def fireSelectionChanged {
    if (fSelectionListeners != null) {
      fSelectionListeners foreach {fsl =>
        fsl.figureSelectionChanged(this)
      }
    }
  }

  protected def getDamage: Rectangle = fDamage

  protected def setDamage(r: Rectangle) {
    fDamage = r
  }

  /**
   * Gets the position of the last click inside the view.
   */
  def lastClick: Point = fLastClick

  protected def setLastClick(newLastClick: Point) {
    fLastClick = newLastClick
  }

  /**
   * Sets the grid spacing that is used to constrain points.
   */
  def setConstrainer(c: PointConstrainer) {
    fConstrainer = c
  }

  /**
   * Gets the current constrainer.
   */
  def getConstrainer: PointConstrainer = fConstrainer

  /**
   * Constrains a point to the current grid.
   */
  protected def constrainPoint(p: Point): Point = {
    val size: Dimension = getSize
    p.x = Geom.range(1, size.width, p.x)
    p.y = Geom.range(1, size.height, p.y)
    if (fConstrainer != null) fConstrainer.constrainPoint(p)
    else p
  }

  private def moveSelection(dx: Int, dy: Int) {
    selection.foreach (f => f.moveBy(dx, dy))
    checkDamage
  }

  /**
   * Refreshes the drawing if there is some accumulated damage
   */
  def checkDamage {
    val each: Iterator[DrawingChangeListener] = drawing.drawingChangeListeners
    each foreach (l => l match {
      case dv: DrawingView => dv.repairDamage
      case _ =>
    })
  }

  def repairDamage {
    if (getDamage != null) {
      repaint(getDamage.x, getDamage.y, getDamage.width, getDamage.height)
      setDamage(null)
    }
  }

  def drawingInvalidated(e: DrawingChangeEvent) {
    val r: Rectangle = e.getInvalidatedRectangle
    if (getDamage == null) {
      setDamage(r)
    } else {
      val damagedR: Rectangle = getDamage
      damagedR.add(r)
      setDamage(damagedR)
    }
  }

  def drawingRequestUpdate(e: DrawingChangeEvent) {
    repairDamage
  }

  def drawingTitleChanged(e: DrawingChangeEvent) {}

  /**
   * Paints the drawing view. The actual drawing is delegated to
   * the current update strategy.
   * @see Painter
   */
  protected override def paintComponent(g: Graphics) {
    if (getDisplayUpdate != null) {
      getDisplayUpdate.draw(g, this)
    }
  }

  /**
   * Draws the contents of the drawing view.
   * The view has three layers: background, drawing, handles.
   * The layers are drawn in back to front order.
   */
  def drawAll(g: Graphics) {
    val isPrinting: Boolean = g.isInstanceOf[PrintGraphics]
    drawBackground(g)
    if ((fBackgrounds != null) && !isPrinting) {
      drawPainters(g, fBackgrounds)
    }
    drawDrawing(g)
    if ((fForegrounds != null) && !isPrinting) {
      drawPainters(g, fForegrounds)
    }
    if (!isPrinting) {
      drawHandles(g)
    }
  }

  /**
   * Draws the given figures.
   * The view has three layers: background, drawing, handles.
   * The layers are drawn in back to front order.
   * No background is drawn.
   */
  def draw(g: Graphics, fe: Seq[Figure]) {
    val isPrinting: Boolean = g.isInstanceOf[PrintGraphics]
    if ((fBackgrounds != null) && !isPrinting) {
      drawPainters(g, fBackgrounds)
    }
    drawing.draw(g, fe)
    if ((fForegrounds != null) && !isPrinting) {
      drawPainters(g, fForegrounds)
    }
    if (!isPrinting) {
      drawHandles(g)
    }
  }

  /**
   * Draws the currently active handles.
   */
  def drawHandles(g: Graphics) {
    selectionHandles foreach (_.draw(g))
  }

  /**
   * Draws the drawing.
   */
  def drawDrawing(g: Graphics) {
    drawing.draw(g)
  }

  /**
   * Draws the background. If a background pattern is set it
   * is used to fill the background. Otherwise the background
   * is filled in the background color.
   */
  def drawBackground(g: Graphics) {
    g.setColor(getBackground)
    g.fillRect(0, 0, this.getBounds().width, this.getBounds().height)
  }

  protected def drawPainters(g: Graphics, v: ArrayBuffer[Painter]) {
    v foreach {_.draw(g,this)}
  }

  /**
   * Adds a background.
   */
  def addBackground(painter: Painter) {
    if (fBackgrounds == null) {
      fBackgrounds = ArrayBuffer[Painter]()
    }
    fBackgrounds += painter
    this.repaint()
  }

  /**
   * Removes a background.
   */
  def removeBackground(painter: Painter) {
    if (fBackgrounds != null) {
      fBackgrounds = fBackgrounds diff List(painter)
    }
    this.repaint()
  }

  protected def getBackgrounds: ArrayBuffer[Painter] = fBackgrounds

  /**
   * Removes a foreground.
   */
  def removeForeground(painter: Painter) {
    if (fForegrounds != null) {
      fForegrounds = fForegrounds diff List(painter)
    }
    this.repaint()
  }

  /**
   * Adds a foreground.
   */
  def addForeground(painter: Painter) {
    if (fForegrounds == null) {
      fForegrounds = ArrayBuffer[Painter]()
    }
    fForegrounds += painter
    this.repaint()
  }

  protected def getForegrounds: ArrayBuffer[Painter] = fForegrounds
  
  /**
   * Freezes the view by acquiring the drawing lock.
   * @see Drawing#lock
   */
  def freezeView {
    drawing.lock
  }

  /**
   * Unfreezes the view by releasing the drawing lock.
   * @see Drawing#unlock
   */
  def unfreezeView {
    drawing.unlock
  }

  private def readObject(s: ObjectInputStream) {
    s.defaultReadObject
    fSelection = ArrayBuffer[Figure]()
    if (drawing != null) {
      drawing.addDrawingChangeListener(this)
    }
    fSelectionListeners = ArrayBuffer[FigureSelectionListener]()
  }

  protected def checkMinimumSize {
    val d: Dimension = getDrawingSize
    val v: Dimension = getPreferredSize
    if (v.height < d.height || v.width < d.width) {
      v.height = d.height + SCROLL_OFFSET
      v.width = d.width + SCROLL_OFFSET
      setPreferredSize(v)
    }
  }

  /**
   * Return the size of the area occupied by the contained figures inside
   * the drawing. This method is called by checkMinimumSize().
   */
  protected def getDrawingSize: Dimension = {
    val d: Dimension = new Dimension(0, 0)
    if (drawing != null) {
      drawing.figures foreach { f =>
        val r: Rectangle = f.displayBox
        d.width = Math.max(d.width, r.x + r.width)
        d.height = Math.max(d.height, r.y + r.height)
      }
    }
    d
  }

  /**
   * @see java.awt.Component#isFocusTraversable()
   * @deprecated see super class
   */
  override def isFocusTraversable: Boolean = true

  def isInteractive: Boolean = true

  def keyTyped(e: KeyEvent) {}

  def keyReleased(e: KeyEvent) {}

  /**
   * Add a listener for selection changes.
   * @param fsl jhotdraw.framework.FigureSelectionListener
   */
  def addFigureSelectionListener(fsl: FigureSelectionListener) {
    fSelectionListeners += fsl
  }

  /**
   * Remove a listener for selection changes.
   * @param fsl jhotdraw.framework.FigureSelectionListener
   */
  def removeFigureSelectionListener(fsl: FigureSelectionListener) {
    fSelectionListeners = fSelectionListeners diff List(fsl)
  }

  def getDefaultDNDActions: Int = java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE

  def autoscroll(p: Point) {
    ash.autoscroll(p)
  }

  def getAutoscrollInsets: Insets = ash.getAutoscrollInsets

  override def toString: String = "DrawingView Nr: " + myCounter

  /**
   * Default action when any uncaught exception bubbled from
   * the mouse event handlers of the tools. Subclass may override it
   * to provide other action.
   */
  protected def handleMouseEventException(t: Throwable) {
    JOptionPane.showMessageDialog(this, t.getClass.getName + " - " + t.getMessage, "Error", JOptionPane.ERROR_MESSAGE)
    t.printStackTrace
  }

  protected def createDNDHelper: DNDHelper = {
    new DNDHelper(true, true) {
      protected def view: DrawingView = StandardDrawingView.this

      protected def editor: DrawingEditor = StandardDrawingView.this.editor
    }
  }

  protected def getDNDHelper: DNDHelper = {
    if (dndh == null) {
      dndh = createDNDHelper
    }
    dndh
  }

  def getDragSourceListener: DragSourceListener = getDNDHelper.getDragSourceListener

  def DNDInitialize(dgl: DragGestureListener) {
    getDNDHelper.initialize(dgl)
  }

  def DNDDeinitialize {
    getDNDHelper.deinitialize
  }

  /**
   * Asks whether the drawing view is in read-only mode. If so, the user can't
   * modify it using mouse or keyboard actions. Yet, it can still be modified
   * from inside the program.
   */
  def isReadOnly: Boolean = myIsReadOnly

  /**
   * Determines whether the drawing view is in read-only mode. If so, the user can't
   * modify it using mouse or keyboard actions. Yet, it can still be modified
   * from inside the program.
   */
  def setReadOnly(newIsReadOnly: Boolean) {
    if (newIsReadOnly != isReadOnly) {
      if (newIsReadOnly) {
        removeMouseListener(myMouseListener)
        removeMouseMotionListener(motionListener)
        removeKeyListener(myKeyListener)
      } else {
        addMouseListener(myMouseListener)
        addMouseMotionListener(motionListener)
        addKeyListener(myKeyListener)
      }
      myIsReadOnly = newIsReadOnly
    }
  }

  /**
   * @see DrawingView#setCursor(Cursor)
   * @see java.awt.Component#setCursor(java.awt.Cursor)
   */
  override def setCursor(cursor: Cursor): Unit = cursor match {
    case c: java.awt.Cursor => this.asInstanceOf[java.awt.Component].setCursor(c) 
    case _ =>
  }

  /**
   * Gets the minimum dimension of the drawing.<br />
   * Fixed version (JHotDraw version has a bug).
   * @see StandardDrawingView#getMinimumSize()
   * @see java.awt.Component#getMinimumSize()
   */
  override def getMinimumSize: Dimension = {
    val r: Rectangle = new Rectangle
    drawing.figures foreach { f =>
      r.add(f.displayBox)
    }
    new Dimension(r.width, r.height)
  }


  private[standard] class ASH(margin: Int) extends AutoscrollHelper(margin) {

    def getSize: Dimension = StandardDrawingView.this.getSize

    def getVisibleRect: Rectangle = StandardDrawingView.this.getVisibleRect

    def scrollRectToVisible(aRect: Rectangle) {
      StandardDrawingView.this.scrollRectToVisible(aRect)
    }
  }

  class DrawingViewMouseListener extends MouseAdapter {
    /**
     * Handles mouse down events. The event is delegated to the
     * currently active tool.
     */
    override def mousePressed(e: MouseEvent) {
      try {
        requestFocus
        val p: Point = constrainPoint(new Point(e.getX, e.getY))
        setLastClick(new Point(e.getX, e.getY))
        tool.mouseDown(e, p.x, p.y)
        checkDamage
      } catch {
        case t: Throwable => {
          handleMouseEventException(t)
        }
      }
    }

    /**
     * Handles mouse up events. The event is delegated to the
     * currently active tool.
     */
    override def mouseReleased(e: MouseEvent) {
      try {
        val p: Point = constrainPoint(new Point(e.getX, e.getY))
        tool.mouseUp(e, p.x, p.y)
        checkDamage
      } catch {
        case t: Throwable => {
          handleMouseEventException(t)
        }
      }
    }
  }

  class DrawingViewMouseMotionListener extends MouseMotionListener {
    /**
     * Handles mouse drag events. The event is delegated to the
     * currently active tool.
     */
    def mouseDragged(e: MouseEvent) {
      try {
        val p: Point = constrainPoint(new Point(e.getX, e.getY))
        tool.mouseDrag(e, p.x, p.y)
        checkDamage
      } catch {
        case t: Throwable => {
          handleMouseEventException(t)
        }
      }
    }

    /**
     * Handles mouse move events. The event is delegated to the
     * currently active tool.
     */
    def mouseMoved(e: MouseEvent) {
      try {
        tool.mouseMove(e, e.getX, e.getY)
      } catch {
        case t: Throwable => {
          handleMouseEventException(t)
        }
      }
    }
  }

  class DrawingViewKeyListener extends KeyListener {
    
    /**
     * Handles key down events. Cursor keys are handled
     * by the view the other key events are delegated to the
     * currently active tool.
     */
    def keyPressed(e: KeyEvent) {
      val code: Int = e.getKeyCode
      val modifiers: Int = e.getModifiers
      if (modifiers == 0 && ((code == KeyEvent.VK_BACK_SPACE) || (code == KeyEvent.VK_DELETE))) {
        if (deleteCmd.isExecutable) {
          deleteCmd.execute
        }
      } else if (modifiers == 0 && ((code == KeyEvent.VK_DOWN) || (code == KeyEvent.VK_UP) || (code == KeyEvent.VK_RIGHT) || (code == KeyEvent.VK_LEFT))) {
        handleCursorKey(code)
      } else {
        tool.keyDown(e, code)
      }
      checkDamage
    }

    /**
     * Handles cursor keys by moving all the selected figures
     * one grid point in the cursor direction.
     */
    protected def handleCursorKey(key: Int) {
      var dx: Int = 0
      var dy: Int = 0
      var stepX: Int = 1
      var stepY: Int = 1
      if (fConstrainer != null) {
        stepX = fConstrainer.getStepX
        stepY = fConstrainer.getStepY
      }
      key match {
        case KeyEvent.VK_DOWN => dy = stepY
        case KeyEvent.VK_UP => dy = -stepY
        case KeyEvent.VK_RIGHT => dx = stepX
        case KeyEvent.VK_LEFT => dx = -stepX
      }
      moveSelection(dx, dy)
    }

    def keyTyped(event: KeyEvent) {}

    def keyReleased(event: KeyEvent) {}

    protected def createDeleteCommand: Command = new UndoableCommand(new DeleteCommand("Delete", editor))

    private var deleteCmd: Command = createDeleteCommand
  }
}