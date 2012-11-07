/*
 * @(#)ConnectionTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.event.MouseEvent
import java.awt._

import org.shotdraw.framework._
import org.shotdraw.util.Geom
import org.shotdraw.util.Undoable
import org.shotdraw.util.UndoableAdapter

/**
 * A tool that can be used to connect figures, to split
 * connections, and to join two segments of a connection.
 * ConnectionTools turns the visibility of the Connectors
 * on when it enters a figure.
 * The connection object to be created is specified by a prototype.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld029.htm>Prototype</a></b><br>
 * ConnectionTools creates the connection by cloning a prototype.
 * <hr>
 *
 * @see ConnectionFigure
 * @see Object#clone
 *
 * @version <$CURRENT_VERSION$>
 */
object ConnectionTool {

  class UndoActivity(newDrawingView: DrawingView, newConnection: ConnectionFigure) extends UndoableAdapter(newDrawingView) {

    setConnection(newConnection)
    myStartConnector = getConnection.getStartConnector
    myEndConnector = getConnection.getEndConnector
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      getConnection.disconnectStart
      getConnection.disconnectEnd
      getAffectedFigures foreach (getDrawingView.drawing.orphan(_))
      getDrawingView.clearSelection
      true
    }

    override def redo: Boolean = {
      if (!super.redo) {
        return false
      }
      getConnection.connectStart(myStartConnector)
      getConnection.connectEnd(myEndConnector)
      getConnection.updateConnection
      getDrawingView.insertFigures(getAffectedFigures, 0, 0, false)
      true
    }

    protected def setConnection(newConnection: ConnectionFigure) {
      myConnection = newConnection
    }

    /**
     * Gets the currently created figure
     */
    protected def getConnection: ConnectionFigure = myConnection

    private var myConnection: ConnectionFigure = null
    private var myStartConnector: Connector = null
    private var myEndConnector: Connector = null
  }

}

class ConnectionTool(newDrawingEditor: DrawingEditor, fPrototype: ConnectionFigure) extends AbstractTool(newDrawingEditor) {

  /**
   * the anchor point of the interaction
   */
  private var myStartConnector: Connector = null
  private var myEndConnector: Connector = null
  private var myTargetConnector: Connector = null
  private var myTarget: Figure = null
  /**
   * the currently created figure
   */
  private var myConnection: ConnectionFigure = null
  /**
   * the currently manipulated connection point
   */
  private var fSplitPoint: Int = 0
  /**
   * the currently edited connection
   */
  private var fEditedConnection: ConnectionFigure = null
  /**
   * the figure that was actually added
   * Note, this can be a different figure from the one which has been created.
   */
  private var myAddedFigure: Figure = null
  /**
   * Handles mouse move events in the drawing view.
   */
  override def mouseMove(e: MouseEvent, x: Int, y: Int) {
    trackConnectors(e, x, y)
  }

  /**
   * Manipulates connections in a context dependent way. If the
   * mouse down hits a figure start a new connection. If the mousedown
   * hits a connection split a segment or join two segments.
   */
  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    super.mouseDown(e, x, y)
    val ex: Int = e.getX
    val ey: Int = e.getY
    val connection: ConnectionFigure = findConnection(ex, ey, drawing)
    if (connection != null) {
      if (!connection.joinSegments(ex, ey)) {
        fSplitPoint = connection.splitSegment(ex, ey)
        fEditedConnection = connection
      } else {
        fEditedConnection = null
      }
    } else {
      setTargetFigure(findConnectionStart(ex, ey, drawing))
      if (getTargetFigure != null) {
        setStartConnector(findConnector(ex, ey, getTargetFigure))
        if (getStartConnector != null) {
          setConnection(createConnection)
          getConnection.startPoint(ex, ey)
          getConnection.endPoint(ex, ey)
          setAddedFigure(view.add(getConnection))
        }
      }
    }
  }

  /**
   * Adjust the created connection or split segment.
   */
  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {
    var p: Point = new Point(e.getX, e.getY)
    if (getConnection != null) {
      trackConnectors(e, x, y)
      if (getTargetConnector != null) {
        p = Geom.center(getTargetConnector.displayBox)
      }
      getConnection.endPoint(p.x, p.y)
    } else if (fEditedConnection != null) {
      val pp: Point = new Point(x, y)
      fEditedConnection.setPointAt(pp, fSplitPoint)
    }
  }

  /**
   * Connects the figures if the mouse is released over another
   * figure.
   */
  override def mouseUp(e: MouseEvent, x: Int, y: Int) {
    var c: Figure = null
    if (getStartConnector != null) {
      c = findTarget(e.getX, e.getY, drawing)
    }
    if (c != null) {
      setEndConnector(findConnector(e.getX, e.getY, c))
      if (getEndConnector != null) {
        getConnection.connectStart(getStartConnector)
        getConnection.connectEnd(getEndConnector)
        getConnection.updateConnection
        setUndoActivity(createUndoActivity)
        getUndoActivity.setAffectedFigures(Seq[Figure](getAddedFigure))
      }
    }
    else if (getConnection != null) {
      view.remove(getConnection)
    }
    setConnection(null)
    setStartConnector(null)
    setEndConnector(null)
    setAddedFigure(null)
    editor.toolDone
  }

  override def deactivate {
    super.deactivate
    if (getTargetFigure != null) {
      getTargetFigure.connectorVisibility(false, null)
    }
  }

  /**
   * Creates the ConnectionFigure. By default the figure prototype is
   * cloned.
   */
  protected def createConnection: ConnectionFigure = fPrototype.clone.asInstanceOf[ConnectionFigure]

  /**
   * Finds a connectable figure target.
   */
  protected def findSource(x: Int, y: Int, drawing: Drawing): Figure = findConnectableFigure(x, y, drawing)

  /**
   * Finds a connectable figure target at the current mouse location that can
   * 1.  Connect to things
   * 2.  Is not already connected to the current Connection (no self connection)
   * 3.  The current Connection can make a connection between it and the start
   * figure.
   */
  protected def findTarget(x: Int, y: Int, drawing: Drawing): Figure = {
    val target: Figure = findConnectableFigure(x, y, drawing)
    val start: Figure = getStartConnector.owner
    if (target != null && getConnection != null && target.canConnect && !target.includes(start) && getConnection.canConnect(start, target)) {
      target
    } else null
  }

  /**
   * Finds an existing connection figure.
   */
  protected def findConnection(x: Int, y: Int, drawing: Drawing): ConnectionFigure = {
    drawing.figuresReverse foreach ( f => f.findFigureInside(x, y) match {
      case cf: ConnectionFigure => return cf
      case _ =>
    })
    null
  }

  protected def setConnection(newConnection: ConnectionFigure) {
    myConnection = newConnection
  }

  /**
   * Gets the connection which is created by this tool
   */
  protected def getConnection: ConnectionFigure = myConnection

  /**
   * Attempts to set the Connector to be connected to based on the current
   * location of the mouse.
   */
  protected def trackConnectors(e: MouseEvent, x: Int, y: Int) {
    var c: Figure = null
    if (getStartConnector == null) {
      c = findSource(x, y, getActiveDrawing)
    } else {
      c = findTarget(x, y, getActiveDrawing)
    }
    if (c ne getTargetFigure) {
      if (getTargetFigure != null) {
        getTargetFigure.connectorVisibility(false, null)
      }
      setTargetFigure(c)
      if (getTargetFigure != null) {
        getTargetFigure.connectorVisibility(true, getConnection)
      }
    }
    var cc: Connector = null
    if (c != null) {
      cc = findConnector(e.getX, e.getY, c)
    }
    if (cc ne getTargetConnector) {
      setTargetConnector(cc)
    }
    getActiveView.checkDamage
  }

  protected def findConnector(x: Int, y: Int, f: Figure): Connector = f.connectorAt(x, y)

  /**
   * Finds a connection start figure.
   */
  protected def findConnectionStart(x: Int, y: Int, drawing: Drawing): Figure = {
    val target: Figure = findConnectableFigure(x, y, drawing)
    if ((target != null) && target.canConnect) target
    else null
  }

  /**
   * Returns the topmost? figure that can connect and is at the current mouse
   * location.
   */
  protected def findConnectableFigure(x: Int, y: Int, drawing: Drawing): Figure = {
    drawing.figuresReverse.find(figure => !figure.includes(getConnection) && figure.canConnect && figure.containsPoint(x, y)) match {
      case Some(fig) => fig
      case _ => null
    }
  }

  protected def setStartConnector(newStartConnector: Connector) {
    myStartConnector = newStartConnector
  }

  protected def getStartConnector: Connector = myStartConnector

  protected def setEndConnector(newEndConnector: Connector) {
    myEndConnector = newEndConnector
  }

  protected def getEndConnector: Connector = myEndConnector

  protected def setTargetConnector(newTargetConnector: Connector) {
    myTargetConnector = newTargetConnector
  }

  protected def getTargetConnector: Connector = myTargetConnector

  protected def setTargetFigure(newTarget: Figure) {
    myTarget = newTarget
  }

  protected def getTargetFigure: Figure = myTarget

  /**
   * Gets the figure that was actually added
   * Note, this can be a different figure from the one which has been created.
   */
  protected def getAddedFigure: Figure = myAddedFigure

  protected def setAddedFigure(newAddedFigure: Figure) {
    myAddedFigure = newAddedFigure
  }

  /**
   * Factory method for undo activity
   */
  protected def createUndoActivity: Undoable = new ConnectionTool.UndoActivity(view, getConnection)
}

