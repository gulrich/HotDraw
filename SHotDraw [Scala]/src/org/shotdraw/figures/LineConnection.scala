/*
 * @(#)LineConnection.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.io._
import org.shotdraw.framework._
import org.shotdraw.standard._
import org.shotdraw.util._
import java.awt.Point
import scala.collection.mutable.ArrayBuffer

/**
 * A LineConnection is a standard implementation of the
 * ConnectionFigure interface. The interface is implemented with PolyLineFigure.
 *
 * @see ConnectionFigure
 *
 * @version <$CURRENT_VERSION$>
 */
class LineConnection extends PolyLineFigure(4) with ConnectionFigure {

  protected var myStartConnector: Connector = null
  protected var myEndConnector: Connector = null
  
  arrowMode = arrowMode

  /**
   * Tests whether a figure can be a connection target.
   * ConnectionFigures cannot be connected and return false.
   */
  override def canConnect: Boolean = false
  
  /**
   * Ensures that a connection is updated if the connection
   * was moved.synthesia
   */
  protected override def basicMoveBy(dx: Int, dy: Int) {
    for(i <- 0 to fPoints.size - 2) pointAt(i).translate(dx, dy)
    updateConnection
  }

  /**
   * Sets the start figure of the connection.
   */
  def connectStart(newStartConnector: Connector) {
    setStartConnector(newStartConnector)
    if (newStartConnector != null) {
      startFigure.addDependendFigure(this)
      startFigure.addFigureChangeListener(this)
    }
  }

  /**
   * Sets the end figure of the connection.
   */
  def connectEnd(newEndConnector: Connector) {
    setEndConnector(newEndConnector)
    if (newEndConnector != null) {
      endFigure.addDependendFigure(this)
      endFigure.addFigureChangeListener(this)
      handleConnect(startFigure, endFigure)
    }
  }

  /**
   * Disconnects the start figure.
   */
  def disconnectStart {
    startFigure.removeFigureChangeListener(this)
    startFigure.removeDependendFigure(this)
    setStartConnector(null)
  }

  /**
   * Disconnects the end figure.
   */
  def disconnectEnd {
    handleDisconnect(startFigure, endFigure)
    endFigure.removeFigureChangeListener(this)
    endFigure.removeDependendFigure(this)
    setEndConnector(null)
  }

  /**
   * Tests whether a connection connects the same figures
   * as another ConnectionFigure.
   */
  def connectsSame(other: ConnectionFigure): Boolean = other.getStartConnector == getStartConnector && other.getEndConnector == getEndConnector

  /**
   * Handles the disconnection of a connection.
   * Override this method to handle this event.
   */
  protected def handleDisconnect(start: Figure, end: Figure) {}

  /**
   * Handles the connection of a connection.
   * Override this method to handle this event.
   */
  protected def handleConnect(start: Figure, end: Figure) {}

  /**
   * Gets the start figure of the connection.
   */
  def startFigure: Figure = {
    if (getStartConnector != null) getStartConnector.owner
    else null
  }

  /**
   * Gets the end figure of the connection.
   */
  def endFigure: Figure = {
    if (getEndConnector != null) getEndConnector.owner
    else null
  }

  protected def setStartConnector(newStartConnector: Connector) {
    myStartConnector = newStartConnector
  }

  /**
   * Gets the start figure of the connection.
   */
  def getStartConnector: Connector = myStartConnector

  protected def setEndConnector(newEndConnector: Connector) {
    myEndConnector = newEndConnector
  }

  /**
   * Gets the end figure of the connection.
   */
  def getEndConnector: Connector = myEndConnector

  /**
   * Tests whether two figures can be connected.
   */
  def canConnect(start: Figure, end: Figure): Boolean = true

  /**
   * Sets the start point.
   */
  def startPoint(x: Int, y: Int) {
    willChange
    if (fPoints.size == 0) fPoints += new Point(x, y)
    else fPoints = fPoints.updated(0, new Point(x, y))    
    changed
  }

  /**
   * Sets the end point.
   */
  def endPoint(x: Int, y: Int) {
    willChange
    if (fPoints.size < 2) fPoints += new Point(x, y)
    else fPoints = fPoints.updated(fPoints.size-1, new Point(x, y)) 
    changed
  }

  /**
   * Gets the start point.
   */
  def startPoint: Point = {
    val p: Point = pointAt(0)
    new Point(p.x, p.y)
  }

  /**
   * Gets the end point.
   */
  def endPoint: Point = {
    if (fPoints.size > 0) {
      val p: Point = pointAt(fPoints.size - 1)
      new Point(p.x, p.y)
    } else null
  }

  /**
   * Gets the handles of the figure. It returns the normal
   * PolyLineHandles but adds ChangeConnectionHandles at the
   * start and end.
   */
  override def handles: Seq[Handle] = {
    var handles: ArrayBuffer[Handle] = ArrayBuffer[Handle](new ChangeConnectionStartHandle(this))
    for(i <- 0 to fPoints.size - 2) {
      handles += new PolyLineHandle(this, PolyLineFigure.locator(i), i)
    }
    handles += new ChangeConnectionEndHandle(this)
    handles
  }

  /**
   * Sets the point and updates the connection.
   */
  override def setPointAt(p: Point, i: Int) {
    super.setPointAt(p, i)
    layoutConnection
  }

  /**
   * Inserts the point and updates the connection.
   */
  override def insertPointAt(p: Point, i: Int) {
    super.insertPointAt(p, i)
    layoutConnection
  }

  /**
   * Removes the point and updates the connection.
   */
  override def removePointAt(i: Int) {
    super.removePointAt(i)
    layoutConnection
  }

  /**
   * Updates the connection.
   */
  def updateConnection {
    if (getStartConnector != null) {
      val start: Point = getStartConnector.findStart(this)
      if (start != null) {
        startPoint(start.x, start.y)
      }
    }
    if (getEndConnector != null) {
      val end: Point = getEndConnector.findEnd(this)
      if (end != null) {
        endPoint(end.x, end.y)
      }
    }
  }

  /**
   * Lays out the connection. This is called when the connection
   * itself changes. By default the connection is recalculated
   */
  def layoutConnection {
    updateConnection
  }

  def figureChanged(e: FigureChangeEvent) {
    updateConnection
  }

  def figureRemoved(e: FigureChangeEvent) {
  }

  def figureRequestRemove(e: FigureChangeEvent) {
  }

  def figureInvalidated(e: FigureChangeEvent) {
  }

  def figureRequestUpdate(e: FigureChangeEvent) {
  }

  override def release {
    super.release
    handleDisconnect(startFigure, endFigure)
    if (getStartConnector != null) {
      startFigure.removeFigureChangeListener(this)
      startFigure.removeDependendFigure(this)
    }
    if (getEndConnector != null) {
      endFigure.removeFigureChangeListener(this)
      endFigure.removeDependendFigure(this)
    }
  }

  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeStorable(getStartConnector)
    dw.writeStorable(getEndConnector)
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    val start: Connector = dr.readStorable.asInstanceOf[Connector]
    if (start != null) {
      connectStart(start)
    }
    val end: Connector = dr.readStorable.asInstanceOf[Connector]
    if (end != null) {
      connectEnd(end)
    }
    if ((start != null) && (end != null)) {
      updateConnection
    }
  }

  private def readObject(s: ObjectInputStream) {
    s.defaultReadObject
    if (getStartConnector != null) {
      connectStart(getStartConnector)
    }
    if (getEndConnector != null) {
      connectEnd(getEndConnector)
    }
  }

  override def visit(visitor: FigureVisitor) {
    visitor.visitFigure(this)
  }

  /**
   * @see org.shotdraw.framework.Figure#removeFromContainer(org.shotdraw.framework.FigureChangeListener)
   */
  override def removeFromContainer(c: FigureChangeListener) {
    super.removeFromContainer(c)
    release
  }

}

