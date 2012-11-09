/*
 * @(#)ChangeConnectionEndHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.Point

import org.shotdraw.framework.ConnectionFigure
import org.shotdraw.framework.Connector
import org.shotdraw.framework.DrawingView
import org.shotdraw.framework.Figure
import org.shotdraw.util.Undoable

/**
 * A handle to reconnect the end point of
 * a connection to another figure.
 *
 * @version <$CURRENT_VERSION$>
 */
object ChangeConnectionEndHandle {

  class UndoActivity(newView: DrawingView) extends ChangeConnectionHandle.UndoActivity(newView) {

    protected def replaceConnector(connection: ConnectionFigure): Connector = {
      val tempEndConnector = connection.getEndConnector
      connection.connectEnd(getOldConnector)
      tempEndConnector
    }
  }

}

class ChangeConnectionEndHandle(owner: ConnectionFigure) extends ChangeConnectionHandle(owner) {

  /**
   * Gets the end figure of a connection.
   */
  protected def target: Connector = getConnection.getEndConnector

  /**
   * Disconnects the end figure.
   */
  protected def disconnect {
    getConnection.disconnectEnd
  }

  /**
   * Sets the end of the connection.
   */
  protected def connect(c: Connector) {
    getConnection.connectEnd(c)
  }

  /**
   * Sets the end point of the connection.
   */
  protected def setPoint(x: Int, y: Int) {
    getConnection.endPoint(x, y)
  }

  /**
   * Returns the end point of the connection.
   */
  def locate: Point = getConnection.endPoint

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity(newView: DrawingView): Undoable = new ChangeConnectionEndHandle.UndoActivity(newView)

  protected def canConnectTo(figure: Figure): Boolean = getConnection.canConnect(source.owner, figure)
}

