/*
 * @(#)ChangeConnectionHandle.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.framework._
import org.shotdraw.util.Geom
import org.shotdraw.util.Undoable
import org.shotdraw.util.UndoableAdapter
import java.awt._

/**
 * ChangeConnectionHandle factors the common code for handles
 * that can be used to reconnect connections.
 *
 * @see ChangeConnectionEndHandle
 * @see ChangeConnectionStartHandle
 *
 * @version <$CURRENT_VERSION$>
 */
object ChangeConnectionHandle {

  abstract class UndoActivity(newView: DrawingView) extends UndoableAdapter(newView) {

    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = {
      if (!super.undo) {
        return false
      }
      swapConnectors
      true
    }

    override def redo: Boolean = {
      if (!isRedoable) {
        return false
      }
      swapConnectors
      true
    }

    private def swapConnectors {
      getAffectedFigures foreach (f => f match {
        case cf:ConnectionFigure =>
          setOldConnector(replaceConnector(cf))
          cf.updateConnection
        case _ =>
      })
    }

    protected def replaceConnector(connection: ConnectionFigure): Connector

    def setOldConnector(newOldConnector: Connector) {
      myOldConnector = newOldConnector
    }

    def getOldConnector: Connector =  myOldConnector

    private var myOldConnector: Connector = null
  }

}

abstract class ChangeConnectionHandle(owner: ConnectionFigure) extends AbstractHandle(owner) {
 
  private var fOriginalTarget: Connector = null
  private var myTarget: Figure = null
  private var myConnection: ConnectionFigure = owner
  
  /**
   * Returns the target connector of the change.
   */
  protected def target: Connector

  /**
   * Disconnects the connection.
   */
  protected def disconnect

  /**
   * Connect the connection with the given figure.
   */
  protected def connect(c: Connector)

  /**
   * Sets the location of the target point.
   */
  protected def setPoint(x: Int, y: Int)

  /**
   * Gets the side of the connection that is unaffected by
   * the change.
   */
  protected def source: Connector = {
    if (target eq getConnection.getStartConnector) getConnection.getEndConnector
    else getConnection.getStartConnector
  }

  /**
   * Disconnects the connection.
   */
  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    fOriginalTarget = target
    setUndoActivity(createUndoActivity(view))
    (getUndoActivity.asInstanceOf[ChangeConnectionHandle.UndoActivity]).setOldConnector(target)
    disconnect
  }

  /**
   * Finds a new target of the connection.
   */
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    var p: Point = new Point(x, y)
    val f: Figure = findConnectableFigure(x, y, view.drawing)
    if (f ne getTargetFigure) {
      if (getTargetFigure != null) {
        getTargetFigure.connectorVisibility(false, null)
      }
      setTargetFigure(f)
      if (getTargetFigure != null) {
        getTargetFigure.connectorVisibility(true, getConnection)
      }
    }
    val target: Connector = findConnectionTarget(p.x, p.y, view.drawing)
    if (target != null) {
      p = Geom.center(target.displayBox)
    }
    setPoint(p.x, p.y)
  }

  /**
   * Connects the figure to the new target. If there is no
   * new target the connection reverts to its original one.
   */
  override def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    var target: Connector = findConnectionTarget(x, y, view.drawing)
    if (target == null) {
      target = fOriginalTarget
    }
    setPoint(x, y)
    connect(target)
    getConnection.updateConnection
    val oldConnector: Connector = (getUndoActivity.asInstanceOf[ChangeConnectionHandle.UndoActivity]).getOldConnector
    if ((oldConnector == null) || (target == null) || (oldConnector.owner eq target.owner)) {
      setUndoActivity(null)
    } else {
      getUndoActivity.setAffectedFigures(Seq[Figure](getConnection))
    }
    if (getTargetFigure != null) {
      getTargetFigure.connectorVisibility(false, null)
      setTargetFigure(null)
    }
  }

  private def findConnectionTarget(x: Int, y: Int, drawing: Drawing): Connector = {
    val target: Figure = findConnectableFigure(x, y, drawing)
    if ((target != null) && target.canConnect && target != fOriginalTarget && !target.includes(owner) && canConnectTo(target)) findConnector(x, y, target)
    else null
  }

  /**
   * Called to check whether this end of the connection can connect to the
   * given target figure. Needs to be overriden by the start and end changers
   * to take the connection's direction into account during the check. JHD 5.4
   * beta and before did not do this.
   */
  protected def canConnectTo(figure: Figure): Boolean

  protected def findConnector(x: Int, y: Int, f: Figure): Connector = f.connectorAt(x, y)

  /**
   * Draws this handle.
   */
  override def draw(g: Graphics) {
    val r: Rectangle = displayBox
    g.setColor(Color.green)
    g.fillRect(r.x, r.y, r.width, r.height)
    g.setColor(Color.black)
    g.drawRect(r.x, r.y, r.width, r.height)
  }

  private def findConnectableFigure(x: Int, y: Int, drawing: Drawing): Figure = {
    drawing.figuresReverse.find(f => !f.includes(getConnection) && f.canConnect && f.containsPoint(x, y)) match {
      case Some(fig) => fig
      case _ => null
    }
  }

  protected def setConnection(newConnection: ConnectionFigure) {
    myConnection = newConnection
  }

  protected def getConnection: ConnectionFigure =  myConnection

  protected def setTargetFigure(newTarget: Figure) {
    myTarget = newTarget
  }

  protected def getTargetFigure: Figure = myTarget

  /**
   * Factory method for undo activity. To be overriden by subclasses.
   */
  protected def createUndoActivity(newView: DrawingView): Undoable

}

