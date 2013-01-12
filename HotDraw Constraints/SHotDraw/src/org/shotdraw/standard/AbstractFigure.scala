/*
 * @(#)AbstractFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	��� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

import scala.collection.mutable.ArrayBuffer

import org.shotdraw.framework.ConnectionFigure
import org.shotdraw.framework.Connector
import org.shotdraw.framework.Figure
import org.shotdraw.framework.FigureChangeEvent
import org.shotdraw.framework.FigureChangeListener
import org.shotdraw.framework.FigureVisitor
import org.shotdraw.framework.Handle
import org.shotdraw.framework.Locator
import org.shotdraw.util.ColorMap
import org.shotdraw.util.Geom

/**
 * AbstractFigure provides default implementations for
 * the Figure interface.
 *
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld036.htm>Template Method</a></b><br>
 * Template Methods implement default and invariant behavior for
 * figure subclasses.
 * <hr>
 *
 * @see Figure
 * @see Handle
 *
 * @version <$CURRENT_VERSION$>
 */
abstract class AbstractFigure extends Figure {
    
  /**
   * Moves the figure by the given offset.
   */
  def moveBy(dx: Int, dy: Int) {
      willChange()
      basicMoveBy(dx, dy)
      changed()
  }

  /**
   * Moves the figure. This is the
   * method that subclassers override. Clients usually
   * call displayBox.
   * @see #moveBy
   */
  protected def basicMoveBy(dx: Int, dy: Int)

  /**
   * Changes the display box of a figure. Clients usually
   * call this method. It changes the display box
   * and announces the corresponding change.
   * @param origin the new origin
   * @param corner the new corner
   * @see #displayBox
   */
  def displayBox(origin: Point, corner: Point) {
    willChange()
    basicDisplayBox(origin, corner)
    changed()
  }

  /**
   * Sets the display box of a figure. This is the
   * method that subclassers override. Clients usually
   * call displayBox.
   * @see #displayBox
   */
  def basicDisplayBox(origin: Point, corner: Point)

  /**
   * Gets the display box of a figure.
   */
  def displayBox: Rectangle

  /**
   * Returns the handles of a Figure that can be used
   * to manipulate some of its attributes.
   * @return a type-safe iterator of handles
   * @see Handle
   */
  def handles: Seq[Handle]

  /**
   * Returns an Enumeration of the figures contained in this figure.
   * @see CompositeFigure
   */
  def figures: Seq[Figure] = Seq[Figure]()

  /**
   * Gets the size of the figure. A convenience method.
   */
  def size: Dimension = new Dimension(displayBox.width, displayBox.height)

  /**
   * Checks if the figure is empty. The default implementation returns
   * true if the width or height of its display box is < 3
   * @see Figure#isEmpty
   */
  def isEmpty: Boolean = (size.width < 3) || (size.height < 3)

  /**
   * Returns the figure that contains the given point.
   * In contrast to containsPoint it returns its
   * innermost figure that contains the point.
   *
   * @see #containsPoint
   */
  def findFigureInside(x: Int, y: Int): Figure = {
    if (containsPoint(x, y)) {
      return this
    }
    null
  }

  /**
   * Checks if a point is inside the figure.
   */
  def containsPoint(x: Int, y: Int): Boolean = displayBox.contains(x, y)

  /**
   * Changes the display box of a figure. This is a
   * convenience method. Implementors should only
   * have to override basicDisplayBox
   * @see #displayBox
   */
  def displayBox(r: Rectangle) {
    displayBox(new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height))
  }

  /**
   * Checks whether the given figure is contained in this figure.
   */
  def includes(figure: Figure): Boolean = figure == this

  /**
   * Decomposes a figure into its parts. It returns a Seq[Figure]
   * that contains itself.
   * @return an Enumeration with itself as the only element.
   */
  def decompose: Seq[Figure] = Seq(this)
  
  /**
   * Sets the Figure's container and registers the container
   * as a figure change listener. A figure's container can be
   * any kind of FigureChangeListener. A figure is not restricted
   * to have a single container.
   */
  def addToContainer(c: FigureChangeListener) {
    addFigureChangeListener(c)
    invalidate()
  }

  /**
   * Removes a figure from the given container and unregisters
   * it as a change listener.
   */
  def removeFromContainer(c: FigureChangeListener) {
    invalidate()
    removeFigureChangeListener(c)
  }

  /**
   * Adds a listener for this figure.
   */
  def addFigureChangeListener(l: FigureChangeListener) {
    fListener = FigureChangeEventMulticaster.add(listener, l)
  }

  /**
   * Removes a listener for this figure.
   */
  def removeFigureChangeListener(l: FigureChangeListener) {
    fListener = FigureChangeEventMulticaster.remove(listener, l)
  }

  /**
   * Gets the figure's listners.
   */
  def listener: FigureChangeListener = fListener

  /**
   * A figure is released from the drawing. You never call this
   * method directly. Release notifies its listeners.
   * @see Figure#release()
   */
  def release() {
    if (listener != null) {
      listener.figureRemoved(new FigureChangeEvent(this))
    }
  }

  /**
   * Invalidates the figure. This method informs the listeners
   * that the figure's current display box is invalid and should be
   * refreshed.
   */
  def invalidate() {
    if (listener != null) {
      val r = invalidateRectangle(displayBox)
      listener.figureInvalidated(new FigureChangeEvent(this, r))
    }
  }

  /**
   * Hook method to change the rectangle that will be invalidated
   */
  protected def invalidateRectangle(r: Rectangle): Rectangle = {
    r.grow(Handle.HANDLESIZE, Handle.HANDLESIZE)
    r
  }

  /**
   * Informes that a figure is about to change something that
   * affects the contents of its display box.
   *
   * @see Figure#willChange()
   */
  def willChange() {
    invalidate()
  }

  /**
   * Informs that a figure changed the area of its display box.
   *
   * @see FigureChangeEvent
   * @see Figure#changed()
   */
  def changed() {
    invalidate()
    if (listener != null) {
      listener.figureChanged(new FigureChangeEvent(this))
    }
  }

  /**
   * Gets the center of a figure. A convenice
   * method that is rarely overridden.
   */
  def center: Point = Geom.center(displayBox)

  /**
   * Checks if this figure can be connected. By default
   * AbstractFigures can be connected.
   */
  def canConnect: Boolean = true

  /**
   * Returns the connection inset. The connection inset
   * defines the area where the display box of a
   * figure can't be connected. By default the entire
   * display box can be connected.
   *
   */
  def connectionInsets: Insets = new Insets(0, 0, 0, 0)

  /**
   * Returns the Figures connector for the specified location.
   * By default a ChopBoxConnector is returned.
   * @see ChopBoxConnector
   */
  def connectorAt(x: Int, y: Int): Connector = new ChopBoxConnector(this)

  /**
   * Sets whether the connectors should be visible.
   * By default they are not visible
   */
  def connectorVisibility(isVisible: Boolean, connector: ConnectionFigure) {}

  /**
   * Returns the locator used to located connected text.
   */
  def connectedTextLocator(text: Figure): Locator = RelativeLocator.center

  /**
   * Clones a figure. Creates a clone by using the storable
   * mechanism to flatten the Figure to stream followed by
   * resurrecting it from the same stream.
   *
   * @see Figure#clone
   */
  override def clone: java.lang.Object = {
    var clone: java.lang.Object = null
    val output = new ByteArrayOutputStream(200)
    try {
      val writer = new ObjectOutputStream(output)
      writer.writeObject(this)
      writer.close()
    } catch {
      case e: IOException => {
        System.err.println("Class not found: " + e)
      }
    }
    val input = new ByteArrayInputStream(output.toByteArray)
    try {
      val reader = new ObjectInputStream(input)
      clone = reader.readObject
    } catch {
      case e: IOException => {
        System.err.println(e.toString)
      } case e: ClassNotFoundException => {
        System.err.println("Class not found: " + e)
      }
    }
    clone
  }

  /**
   * Gets the z value (back-to-front ordering) of this figure.
   */
  def getZValue: Int = _nZ

  /**
   * Sets the z value (back-to-front ordering) of this figure.
   */
  def setZValue(z: Int) {
    _nZ = z
  }

  def visit(visitor: FigureVisitor) {
    visitor.visitFigure(this)
    figures foreach { vf =>
      vf.visit(visitor)
    }
    //@TODO
//    handles foreach { vh =>
//      visitor.visitHandle(vh)
//    }
    getDependendFigures foreach { f => f
      f.visit(visitor)
    }
  }

  def getDependendFigures: Seq[Figure] = myDependendFigures

  def addDependendFigure(newDependendFigure: Figure) {
    myDependendFigures += newDependendFigure
  }

  def removeDependendFigure(oldDependendFigure: Figure) {
    myDependendFigures = myDependendFigures diff List(oldDependendFigure)
  }

  def getTextHolder: TextHolder = null

  def getDecoratedFigure: Figure = this
  
    /**
   * Draws the figure in the given graphics. Draw is a template
   * method calling drawBackground followed by drawFrame.
   */
  def draw(g: Graphics) {
    val fill = fillColor
    if (!ColorMap.isTransparent(fill)) {
      g.setColor(fill)
      drawBackground(g)
    }
    val frame = frameColor
    if (!ColorMap.isTransparent(frame)) {
      g.setColor(frame)
      drawFrame(g)
    }
  }

  /**
   * Draws the background of the figure.
   * @see #draw
   */
  protected def drawBackground(g: Graphics) {}

  /**
   * Draws the frame of the figure.
   * @see #draw
   */
  protected def drawFrame(g: Graphics) {}

  /**
   * The listeners for a figure's changes.
   * It is only one listener but this one can be a (chained) MultiCastFigureChangeListener
   * @see #invalidate()
   * @see #changed()
   * @see #willChange()
   */
  @transient
  private var fListener: FigureChangeListener = null
  /**
   * The dependend figures which have been added to this container.
   */
  private var myDependendFigures = ArrayBuffer[Figure]()
  private var _nZ = 0
}

