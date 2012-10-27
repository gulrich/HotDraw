/*
 * @(#)AbstractConnector.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import java.awt._
import java.io.IOException
import org.jhotdraw.framework._
import org.jhotdraw.util._

/**
 * AbstractConnector provides default implementation for
 * the Connector interface.
 *
 * @see Connector
 *
 * @version <$CURRENT_VERSION$>
 */
object AbstractConnector {
  private final val serialVersionUID: Long = -5170007865562687545L
}

class AbstractConnector(var fOwner: Figure) extends Connector {
  /**
   * Constructs a connector that has no owner. It is only
   * used internally to resurrect a connectors from a
   * StorableOutput. It should never be called directly.
   */
  def this() {
    this(null)
  }

  /**
   * Gets the connector's owner.
   * Which is the figure this is attached too and NOT the connection it may be
   * attached too.
   *
   */
  def owner: Figure = fOwner

  def findStart(connection: ConnectionFigure): Point = findPoint(connection)

  def findEnd(connection: ConnectionFigure): Point = findPoint(connection)

  /**
   * Gets the connection point. Override when the connector
   * does not need to distinguish between the start and end
   * point of a connection.
   */
  protected def findPoint(connection: ConnectionFigure): Point = Geom.center(displayBox)
  
  /**
   * Gets the display box of the connector.
   */
  def displayBox: Rectangle = owner.displayBox

  /**
   * Tests if a point is contained in the connector.
   */
  def containsPoint(x: Int, y: Int): Boolean = owner.containsPoint(x, y)

  /**
   * Draws this connector. By default connectors are invisible.
   */
  def draw(g: Graphics) {}

  /**
   * Stores the connector and its owner to a StorableOutput.
   */
  def write(dw: StorableOutput) {
    dw.writeStorable(owner)
  }

  /**
   * Reads the connector and its owner from a StorableInput.
   */
  def read(dr: StorableInput): Unit = dr.readStorable match {
    case f: Figure => fOwner = f
    case _ =>
  }

  /**
   * Requests that the connector should show itself or hide itself.  The
   * ConnectionFigure which desires to connect to this Connector is passed in.
   * It a connector should show itself it should do so when draw is called, if
   * so desired.
   */
  def connectorVisibility(isVisible: Boolean, courtingConnection: ConnectionFigure) {}
}

