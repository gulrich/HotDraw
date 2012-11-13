/*
 * @(#)CompositeFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.util._
import org.shotdraw.framework._
import java.io._
import java.util.Collections
import java.awt.Rectangle
import java.awt.Graphics
import scala.collection.mutable.ArrayBuffer
import java.awt.Color
import org.shotdraw.figures.PolyLineFigure.ArrowType
import java.awt.Graphics2D
import java.awt.RenderingHints

/**
 * A Figure that is composed of several figures. A CompositeFigure
 * doesn't define any layout behavior. It is up to subclassers to
 * arrange the contained figures.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld012.htm>Composite</a></b><br>
 * CompositeFigure enables to treat a composition of figures like
 * a single figure.<br>
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */
abstract class CompositeFigure extends AbstractFigure with FigureChangeListener {

  /**
   * Adds a figure to the list of figures. Initializes the
   * the figure's container.
   *
   * @param figure to be added to the drawing
   * @return the figure that was inserted (might be different from the figure specified).
   */
  def add(figure: Figure): Figure = {
    if (!containsFigure(figure)) {
      _nHighestZ += 1
      figure.setZValue(_nHighestZ)
      fFigures += figure
      figure.addToContainer(this)
      _addToQuadTree(figure)
    }
    figure
  }

  /**
   * Adds a list of figures.
   *
   * @see #add
   * @deprecated use addAll(Seq[Figure]) instead
   */
  def addAll(newFigures: List[Figure]) {
    addAll(newFigures)
  }

  /**
   * Adds a Seq[Figure] of figures.
   *
   * @see #add
   * @param fe (unused) enumeration containing all figures to be added
   */
  def addAll(fe: Seq[Figure]) {
    fe foreach (add(_))
  }

  /**
   * Removes a figure from the composite.
   *
   * @param figure that is part of the drawing and should be removed
   * @return the figure that has been removed (might be different from the figure specified)
   * @see #removeAll
   */
  def remove(figure: Figure): Figure = {
    val orphanedFigure = orphan(figure)
    if (orphanedFigure != null) {
      orphanedFigure.release
    }
    orphanedFigure
  }

  /**
   * Removes a list of figures.
   *
   * @see #remove
   * @deprecated use removeAll(Seq[Figure]) instead
   */
  def removeAll(figures: List[Figure]) {
    removeAll(figures)
  }

  /**
   * Removes a Seq[Figure] of figures.
   * @see #remove
   */
  def removeAll(fe: Seq[Figure]) {
    fe foreach(remove(_))
  }

  /**
   * Removes all children.
   * @see #remove
   */
  def removeAll {
    figures foreach (_.removeFromContainer(this))
    fFigures = ArrayBuffer()
    _clearQuadTree
    _nLowestZ = 0
    _nHighestZ = 0
  }

  /**
   * Removes a figure from the figure list, but
   * doesn't release it. Use this method to temporarily
   * manipulate a figure outside of the drawing.
   *
   * @param figure that is part of the drawing and should be added
   */
  def orphan(figure: Figure): Figure = {
    figure.removeFromContainer(this)
    fFigures = fFigures diff List(figure)
    _removeFromQuadTree(figure)
    figure
  }

  /**
   * Removes a list of figures from the figure's list
   * without releasing the figures.
   *
   * @see #orphan
   * @deprecated use orphanAll(Seq[Figure]) instead
   */
  def orphanAll(newFigures: List[Figure]) {
    orphanAll(newFigures)
  }

  def orphanAll(fe: Seq[Figure]) {
    fe foreach(orphan(_))
  }

  /**
   * Replaces a figure in the drawing without
   * removing it from the drawing.
   *
   * @param figure figure to be replaced
   * @param replacement figure that should replace the specified figure
   * @return the figure that has been inserted (might be different from the figure specified)
   */
  def replace(figure: Figure, replacement: Figure): Figure = {
    val index = fFigures.indexOf(figure)
    if (index != -1) {
      replacement.setZValue(figure.getZValue)
      replacement.addToContainer(this)
      figure.removeFromContainer(this)
      fFigures = fFigures.updated(index, replacement)
      figure.changed
      replacement.changed
    }
    replacement
  }

  /**
   * Sends a figure to the back of the drawing.
   *
   * @param figure that is part of the drawing
   */
  def sendToBack(figure: Figure) {
    if (containsFigure(figure)) {
      fFigures -= figure
      fFigures = ArrayBuffer(figure) ++ fFigures
      _nLowestZ -= 1
      figure.setZValue(_nLowestZ)
      figure.changed
    }
  }

  /**
   * Brings a figure to the front.
   *
   * @param figure that is part of the drawing
   */
  def bringToFront(figure: Figure) {
    if (containsFigure(figure)) {
      fFigures = fFigures diff List(figure)
      fFigures += figure
      _nHighestZ += 1
      figure.setZValue(_nHighestZ)
      figure.changed
    }
  }

  /**
   * Sends a figure to a certain layer within a drawing. Each figure
   * lays in a unique layer and the layering order decides which
   * figure is drawn on top of another figure. Figures with a higher
   * layer number have usually been added later and may overlay
   * figures in lower layers. Layers are counted from to (the number
   * of figures - 1).
   * The figure is removed from its current layer (if it has been already
   * part of this drawing) and is transferred to the specified layers after
   * all figures between the original layer and the new layer are shifted to
   * one layer below to fill the layer sequence. It is not possible to skip a
   * layer number and if the figure is sent to a layer beyond the latest layer
   * it will be added as the last figure to the drawing and its layer number
   * will be set to the be the one beyond the latest layer so far.
   *
   * @param figure figure to be sent to a certain layer
   * @param layerNr target layer of the figure
   */
  def sendToLayer(figure: Figure, layerNr: Int) {
    var nr = layerNr
    if (containsFigure(figure)) {
      if (nr >= fFigures.size) {
        nr = fFigures.size - 1
      }
      val layerFigure = getFigureFromLayer(nr)
      val layerFigureZValue = layerFigure.getZValue
      val figureLayer = getLayer(figure)
      if (figureLayer < nr) {
        assignFiguresToPredecessorZValue(figureLayer + 1, nr)
      } else if (figureLayer > nr) {
        assignFiguresToSuccessorZValue(nr, figureLayer - 1)
      }
      fFigures -= figure
      fFigures.insert(nr, figure) 
      figure.setZValue(layerFigureZValue)
      figure.changed
    }
  }

  private def assignFiguresToPredecessorZValue(lowerBound: Int, upperBound: Int) {
    var up = upperBound
    if (up >= fFigures.size) {
      up = fFigures.size - 1
    }
    for(i <- up to lowerBound) {
      val currentFigure = fFigures(i)
      val predecessorFigure = fFigures(i - 1)
      currentFigure.setZValue(predecessorFigure.getZValue)
    }
  }

  private def assignFiguresToSuccessorZValue(lowerBound: Int, upperBound: Int) {
    var up = upperBound
    if (up >= fFigures.size) {
      up = fFigures.size - 1
    }
    for(i <- up to lowerBound) {
      val currentFigure = fFigures(i)
      val successorFigure = fFigures(i + 1)
      currentFigure.setZValue(successorFigure.getZValue)
    }
  }

  /**
   * Gets the layer for a certain figure (first occurrence). The number
   * returned is the number of the layer in which the figure is placed.
   *
   * @param figure figure to be queried for its layering place
   * @return number of the layer in which the figure is placed and -1 if the
   *         figure could not be found.
   * @see #sendToLayer
   */
  def getLayer(figure: Figure): Int = {
    if (!containsFigure(figure)) -1
    else fFigures.indexOf(figure)
  }

  /**
   * Gets the figure from a certain layer.
   *
   * @param layerNr number of the layer which figure should be returned
   * @return figure from the layer specified, null, if the layer nr was outside
   *         the number of possible layer (0...(number of figures - 1))
   * @see #sendToLayer
   */
  def getFigureFromLayer(layerNr: Int): Figure = {
    if ((layerNr >= 0) && (layerNr < fFigures.size)) fFigures(layerNr)
    else null
  }

  /**
   * Draws all the contained figures
   * @see Figure#draw
   */
  override def draw(g: Graphics): Unit = g match {
    case g2d: Graphics2D => 
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
      draw(g2d, figures)
    case g => draw(g, figures)
  }

  /**
   * Draws only the given figures
   * @see Figure#draw
   */
  def draw(g: Graphics, fe: Seq[Figure]) {
    fe foreach(_.draw(g))
  }

  /**
   * Gets a figure at the given index.
   */
  def figureAt(i: Int): Figure = fFigures(i)

  /**
   * Returns an Enumeration for accessing the contained figures.
   * The enumeration is a snapshot of the current contained figures
   * and is not a "live" enumeration and does not take subsequent
   * changes of the CompositeFigure into account.
   * The figures are returned in the drawing order.
   */
  override def figures: Seq[Figure] = fFigures  

  /**
   * Returns an enumeration to iterate in
   * Z-order back to front over the figures
   * that lie within the given bounds.
   */
  def figures(viewRectangle: Rectangle): Seq[Figure] = {
    if (_theQuadTree != null) {
      val fe = _theQuadTree.getAllWithin(new Bounds(viewRectangle).asRectangle2D)
      var l2: List[OrderedFigureElement] = List()
      fe foreach(f => l2 ::= new OrderedFigureElement(f, f.getZValue))
      l2 = l2.sortWith((e1, e2) => e1.compareTo(e2) < 0)
      val l3 = l2.foldLeft(ArrayBuffer[Figure]())((x,y) => x+=y.getFigure)
      l3
    } else figures
  }

  /**
   * Gets number of child figures.
   */
  def figureCount: Int = fFigures.size

  /**
   * Check whether a given figure is a child figure of this CompositeFigure.
   */
  def containsFigure(checkFigure: Figure): Boolean = fFigures.contains(checkFigure)

  /**
   * Returns an Enumeration for accessing the contained figures
   * in the reverse drawing order.
   */
  final def figuresReverse: Seq[Figure] = fFigures.reverse

  /**
   * Finds a top level Figure. Use this call for hit detection that
   * should not descend into the figure's children.
   */
  def findFigure(x: Int, y: Int): Figure = {
    figuresReverse find(f => f.containsPoint(x, y)) match {
      case Some(figure) => figure
      case None => null
    }
  }

  /**
   * Finds a top level Figure that intersects the given rectangle.
   */
  def findFigure(r: Rectangle): Figure = {
    figuresReverse find(f => r.intersects(f.displayBox)) match {
      case Some(figure) => figure
      case None => null
    }
  }

  /**
   * Finds a top level Figure, but supresses the passed
   * in figure. Use this method to ignore a figure
   * that is temporarily inserted into the drawing.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param without the figure to be ignored during
   *                the find.
   */
  def findFigureWithout(x: Int, y: Int, without: Figure): Figure = {
    if (without == null) {
      return findFigure(x, y)
    }
    figuresReverse find(f => (f.containsPoint(x, y) && !f.includes(without))) match {
      case Some(figure) => figure
      case None => null
    }
  }

  /**
   * Finds a top level Figure that intersects the given rectangle.
   * It supresses the passed
   * in figure. Use this method to ignore a figure
   * that is temporarily inserted into the drawing.
   */
  def findFigure(r: Rectangle, without: Figure): Figure = {
    if (without == null) {
      return findFigure(r)
    }
    figuresReverse find(f => (r.intersects(f.displayBox) && !f.includes(without))) match {
      case Some(figure) => figure
      case None => null
    }
  }

  /**
   * Finds a figure but descends into a figure's
   * children. Use this method to implement <i>click-through</i>
   * hit detection, that is, you want to detect the inner most
   * figure containing the given point.
   */
  override def findFigureInside(x: Int, y: Int): Figure = {
    figuresReverse find(f => (f.findFigureInside(x, y) != null)) match {
      case Some(figure) => figure
      case None =>
        if (containsPoint(x, y)) this
        else null
    }
  }

  /**
   * Finds a figure but descends into a figure's
   * children. It supresses the passed
   * in figure. Use this method to ignore a figure
   * that is temporarily inserted into the drawing.
   */
  def findFigureInsideWithout(x: Int, y: Int, without: Figure): Figure = {
    if (without == null) {
      return findFigureInside(x, y)
    }
    figuresReverse find(f => (f != without && f.findFigureInside(x, y) != null && !f.includes(without))) match {
      case Some(figure) => figure
      case None =>
        if (containsPoint(x, y)) this
        else null
    }
  }

  /**
   * Checks if the composite figure has the argument as one of
   * its children.
   * @return true if the figure is part of this CompositeFigure, else otherwise
   */
  override def includes(figure: Figure): Boolean = {
    if (super.includes(figure)) {
      return true
    }
    figures.find(f => f.includes(figure)).isDefined
  }

  /**
   * Moves all the given figures by x and y. Doesn't announce
   * any changes. Subclassers override
   * basicMoveBy. Clients usually call moveBy.
   * @see #moveBy
   */
  protected def basicMoveBy(x: Int, y: Int) {
    figures foreach(_.moveBy(x, y))
  }

  /**
   * Releases the figure and all its children.
   */
  override def release {
    figures foreach (_.release)
    super.release
  }

  /**
   * Propagates the figureInvalidated event to my listener.
   * @see FigureChangeListener
   */
  def figureInvalidated(e: FigureChangeEvent) {
    if (listener != null) {
      listener.figureInvalidated(e)
    }
  }

  /**
   * Propagates the removeFromDrawing request up to the container.
   * @see FigureChangeListener
   */
  def figureRequestRemove(e: FigureChangeEvent) {
    if (listener != null) {
      listener.figureRequestRemove(new FigureChangeEvent(this))
    }
  }

  /**
   * Propagates the requestUpdate request up to the container.
   * @see FigureChangeListener
   */
  def figureRequestUpdate(e: FigureChangeEvent) {
    if (listener != null) {
      listener.figureRequestUpdate(e)
    }
  }

  def figureChanged(e: FigureChangeEvent) {
    _removeFromQuadTree(e.getFigure)
    _addToQuadTree(e.getFigure)
  }

  def figureRemoved(e: FigureChangeEvent) {
    if (listener != null) {
      listener.figureRemoved(e)
    }
  }

  /**
   * Writes the contained figures to the StorableOutput.
   */
  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeInt(figureCount)
    figures foreach(dw.writeStorable(_))
  }

  /**
   * Reads the contained figures from StorableInput.
   */
  override def read(dr: StorableInput) {
    super.read(dr)
    val size = dr.readInt
    fFigures = ArrayBuffer()
    for(i <- 0 to size-1) {add(dr.readStorable.asInstanceOf[Figure])}
    init(displayBox)
  }

  private def readObject(s: ObjectInputStream) {
    s.defaultReadObject
    figures foreach(_.addToContainer(this))
    val fe = figures
    init(new Rectangle(0, 0))
  }

  /**
   * Used to optimize rendering.  Rendering of many objects may
   * be slow until this method is called.  The view rectangle
   * should at least approximately enclose the CompositeFigure.
   * If the view rectangle is too small or too large, performance
   * may suffer.
   *
   * Don't forget to call this after loading or creating a
   * new CompositeFigure.  If you forget, drawing performance may
   * suffer.
   */
  def init(viewRectangle: Rectangle) {
    _theQuadTree = new QuadTree(new Bounds(viewRectangle).asRectangle2D)
    figures foreach{_addToQuadTree(_)}
  }

  private def _addToQuadTree(f: Figure) {
    if (_theQuadTree != null) {
      val r = f.displayBox
      if (r.height == 0) {
        r.grow(0, 1)
      } 
      if (r.width == 0) {
        r.grow(1, 0)
      }
      _theQuadTree.add(f, new Bounds(r).asRectangle2D)
    }
  }

  private def _removeFromQuadTree(f: Figure) {
    if (_theQuadTree != null) {
      _theQuadTree.remove(f)
    }
  }

  private def _clearQuadTree {
    if (_theQuadTree != null) {
      _theQuadTree.clear
    }
  }
  
  override def fillColor_=(value: Color) {
    super.fillColor = value
    fFigures foreach { _.fillColor=value }
  }
  
  override def frameColor_=(value: Color) {
    super.frameColor = value
    fFigures foreach { _.frameColor = value }
  }
  
  override def textColor_=(value: Color) {
    super.textColor = value
    fFigures foreach { _.textColor = value }
  }
  
  override def fontSize_=(value: Int) {
    super.fontSize = value
    fFigures foreach { _.fontSize = value }
  }
  
  override def fontName_=(value: String) {
    super.fontName = value
    fFigures foreach { _.fontName = value }
  }
  
  override def fontStyle_=(value: Int) {
    super.fontStyle = value
    fFigures foreach { _.fontStyle = value }
  }
  
  override def arrowMode_=(value: ArrowType) {
    super.arrowMode = value
    fFigures foreach { _.arrowMode = value }
  }

  /**
   * The figures that this figure is composed of
   * @see #add
   * @see #remove
   */
  protected var fFigures: ArrayBuffer[Figure] = ArrayBuffer()
  @transient
  private var _theQuadTree: QuadTree = null
  protected var _nLowestZ: Int = 0
  protected var _nHighestZ: Int = 0
}

