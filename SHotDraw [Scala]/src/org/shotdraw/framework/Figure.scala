/*
 * @(#)Figure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

import java.awt.Rectangle
import org.shotdraw.util.Storable
import java.awt.Point
import java.awt.Insets
import java.awt.Graphics
import org.shotdraw.standard.TextHolder
import java.awt.Dimension
import org.shotdraw.figures.FigureAttributes

/**
 * The interface of a graphical figure. A figure knows
 * its display box and can draw itself. A figure can be
 * composed of several figures. To interact and manipulate
 * with a figure it can provide Handles and Connectors.<p>
 * A figure has a set of handles to manipulate its shape or attributes.
 * A figure has one or more connectors that define how
 * to locate a connection point.<p>
 * Figures can have an open ended set of attributes.
 * An attribute is identified by a string.<p>
 * Default implementations for the Figure interface are provided
 * by AbstractFigure.<p>
 *
 * Figures can have <a name="dependent_figure">dependent figure</a>s. The existence od dependent
 * figures depend on another figure. This is the case for figures
 * such as ConnectedTextFigures and LineDecoration. Thus, they are
 * "externally" dependent on a figure in contrast to (internally)
 * contained figures. This means, "normal" figures (figures that
 * are not containers) can still have dependent figures. Dependent
 * figures are especially important if the figure which the depend
 * on is deleted because they should be removed as well (cascading delete).
 *
 * @see Handle
 * @see Connector
 * @see org.shotdraw.standard.AbstractFigure
 *
 * @version <$CURRENT_VERSION$>
 */
trait Figure extends FigureAttributes with Storable with Cloneable {
  /**
   * Moves the Figure to a new location.
   * @param dx the x delta
   * @param dy the y delta
   */
  def moveBy(dx: Int, dy: Int)

  /**
   * Changes the display box of a figure. This method is
   * always implemented in figure subclasses. It only changes
   * the displaybox and does not announce any changes. It
   * is usually not called by the client. Clients typically call
   * displayBox to change the display box.
   * @param origin the new origin
   * @param corner the new corner
   * @see #displayBox
   */
  def basicDisplayBox(origin: Point, corner: Point)

  /**
   * Changes the display box of a figure. Clients usually
   * invoke this method. It changes the display box
   * and announces the corresponding changes.
   * @param origin the new origin
   * @param corner the new corner
   * @see #displayBox
   */
  def displayBox(origin: Point, corner: Point)

  /**
   * Gets the display box of a figure
   * @see #basicDisplayBox
   */
  def displayBox: Rectangle

  /**
   * Draws the figure.
   * @param g the Graphics to draw into
   */
  def draw(g: Graphics)

  /**
   * Returns the handles used to manipulate
   * the figure. Handles is a Factory Method for
   * creating handle objects.
   *
   * @return an type-safe iterator of handles
   * @see Handle
   */
  def handles: Seq[Handle]

  /**
   * Gets the size of the figure
   */
  def size: Dimension

  /**
   * Gets the figure's center
   */
  def center: Point

  /**
   * Checks if the Figure should be considered as empty.
   */
  def isEmpty: Boolean

  /**
   * Returns an Enumeration of the figures contained in this figure
   */
  def figures: Seq[Figure]

  /**
   * Returns the figure that contains the given point.
   */
  def findFigureInside(x: Int, y: Int): Figure

  /**
   * Checks if a point is inside the figure.
   */
  def containsPoint(x: Int, y: Int): Boolean

  /**
   * Returns a Clone of this figure
   */
  override def clone: java.lang.Object = this

  /**
   * Changes the display box of a figure. This is a
   * convenience method. Implementors should only
   * have to override basicDisplayBox
   * @see #displayBox
   */
  def displayBox(r: Rectangle)

  /**
   * Checks whether the given figure is contained in this figure.
   */
  def includes(figure: Figure): Boolean

  /**
   * Decomposes a figure into its parts. A figure is considered
   * as a part of itself.
   */
  def decompose: Seq[Figure]

  /**
   * Sets the Figure's container and registers the container
   * as a figure change listener. A figure's container can be
   * any kind of FigureChangeListener. A figure is not restricted
   * to have a single container.
   */
  def addToContainer(c: FigureChangeListener)

  /**
   * Removes a figure from the given container and unregisters
   * it as a change listener.
   */
  def removeFromContainer(c: FigureChangeListener)

  /**
   * Add a <a href="#dependent_figure">dependent figure</a>.
   */
  def addDependendFigure(newDependendFigure: Figure)

  /**
   * Remove a <a href="#dependent_figure">dependent figure</a>.
   */
  def removeDependendFigure(oldDependendFigure: Figure)

  /**
   * Get an enumeration of all <a href="#dependent_figure">dependent figures</a>.
   */
  def getDependendFigures: Seq[Figure]

  /**
   * Gets the Figure's listeners.
   */
  def listener: FigureChangeListener

  /**
   * Adds a listener for this figure.
   */
  def addFigureChangeListener(l: FigureChangeListener)

  /**
   * Removes a listener for this figure.
   */
  def removeFigureChangeListener(l: FigureChangeListener)

  /**
   * Releases a figure's resources. Release is called when
   * a figure is removed from a drawing. Informs the listeners that
   * the figure is removed by calling figureRemoved.
   */
  def release()

  /**
   * Invalidates the figure. This method informs its listeners
   * that its current display box is invalid and should be
   * refreshed.
   */
  def invalidate()

  /**
   * Informes that a figure is about to change such that its
   * display box is affected.
   * Here is an example of how it is used together with changed()
   * <pre>
   * public void move(int x, int y) {
   * willChange();
   * // change the figure's location
   * changed();
   * }
   * </pre>
   * @see #invalidate
   * @see #changed
   */
  def willChange()

  /**
   * Informes that a figure has changed its display box.
   * This method also triggers an update call for its
   * registered observers.
   * @see #invalidate
   * @see #willChange
   *
   */
  def changed()

  /**
   * Checks if this figure can be connected
   */
  def canConnect: Boolean

  /**
   * Gets a connector for this figure at the given location.
   * A figure can have different connectors at different locations.
   */
  def connectorAt(x: Int, y: Int): Connector

  /**
   * Sets whether the connectors should be visible.
   * Connectors can be optionally visible. Implement
   * this method and react on isVisible to turn the
   * connectors on or off.
   */
  def connectorVisibility(isVisible: Boolean, connection: ConnectionFigure)

  /**
   * Returns the connection inset. This is only a hint that
   * connectors can use to determine the connection location.
   * The inset defines the area where the display box of a
   * figure should not be connected.
   *
   */
  def connectionInsets: Insets

  /**
   * Returns the locator used to located connected text.
   */
  def connectedTextLocator(text: Figure): Locator

  /**
   * Gets the z value (back-to-front ordering) of this figure.
   * Z values are not guaranteed to not skip numbers.
   */
  def getZValue: Int

  /**
   * Sets the z value (back-to-front ordering) of this figure.
   * Z values are not guaranteed to not skip numbers.
   */
  def setZValue(z: Int)

  def visit(visitor: FigureVisitor)

  /**
   * Some figures have the ability to hold text. This method returns
   * the adjunctant TextHolder.
   * @return
   */
  def getTextHolder: TextHolder

  /**
   * Get the underlying figure in case the figure has been decorated.
   * If the figure has not been decorated the figure itself is returned.
   * The DecoratorFigure does not release the the decorated figure but
   * just returns it (in contrast to {@link org.shotdraw.standard.DecoratorFigure.peelDecoration}).
   *
   * @return underlying, "real" without DecoratorFigure
   * @see org.shotdraw.standard.DecoratorFigure
   */
  def getDecoratedFigure: Figure
}

