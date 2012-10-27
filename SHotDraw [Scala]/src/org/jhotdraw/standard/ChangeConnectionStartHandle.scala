/*
 * @(#)ChangeConnectionStartHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import org.jhotdraw.framework._
import org.jhotdraw.util.Undoable
import java.awt.Point

/**
 * Handle to reconnect the
 * start of a connection to another figure.
 *
 * @version <$CURRENT_VERSION$>
 */
object ChangeConnectionStartHandle {

  class UndoActivity(newView: DrawingView) extends ChangeConnectionHandle.UndoActivity(newView) {

    protected def replaceConnector(connection: ConnectionFigure): Connector = {
      val tempStartConnector: Connector = connection.getStartConnector
      connection.connectStart(getOldConnector)
      tempStartConnector
    }
  }

}

class ChangeConnectionStartHandle(owner: ConnectionFigure) extends ChangeConnectionHandle(owner) {

  /**
   * Gets the start figure of a connection.
   */
  protected def target: Connector = getConnection.getStartConnector

  /**
   * Disconnects the start figure.
   */
  protected def disconnect {
    getConnection.disconnectStart
  }

  /**
   * Sets the start of the connection.
   */
  protected def connect(c: Connector) {
    getConnection.connectStart(c)
  }

  /**
   * Sets the start point of the connection.
   */
  protected def setPoint(x: Int, y: Int) {
    getConnection.startPoint(x, y)
  }

  /**
   * Returns the start point of the connection.
   */
  def locate: Point = getConnection.startPoint

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity(newView: DrawingView): Undoable = new ChangeConnectionStartHandle.UndoActivity(newView)

  protected def canConnectTo(figure: Figure): Boolean = getConnection.canConnect(figure, source.owner)
}

