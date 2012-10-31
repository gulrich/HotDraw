/*
 * @(#)PolyLineFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.figures

import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import java.io.IOException
import org.jhotdraw.framework.Connector
import org.jhotdraw.framework.FigureAttributeConstant
import org.jhotdraw.framework.Handle
import org.jhotdraw.framework.Locator
import org.jhotdraw.standard.AbstractFigure
import org.jhotdraw.util.Geom
import org.jhotdraw.util.StorableInput
import org.jhotdraw.util.StorableOutput
import java.lang.Object
import scala.collection.mutable.ArrayBuffer

/**
 * A poly line figure consists of a list of points.
 * It has an optional line decoration at the start and end.
 *
 * @see LineDecoration
 *
 * @version <$CURRENT_VERSION$>
 */
object PolyLineFigure {
  /**
   * Creates a locator for the point with the given index.
   */
  def locator(pointIndex: Int): Locator = {
    return new PolyLineLocator(pointIndex)
  }

  final val ARROW_TIP_NONE: Int = 0
  final val ARROW_TIP_START: Int = 1
  final val ARROW_TIP_END: Int = 2
  final val ARROW_TIP_BOTH: Int = 3
  private final val serialVersionUID: Long = -7951352179906577773L
}

class PolyLineFigure(fSize: Int) extends AbstractFigure {
  import PolyLineFigure._
  
  protected var fPoints: ArrayBuffer[Point] = ArrayBuffer()
  protected var fStartDecoration: LineDecoration = null
  protected var fEndDecoration: LineDecoration = null
  protected var fFrameColor: Color = Color.black
  
  def this() {
    this(4)
  }

  def this(x: Int, y: Int) {
    this()
    fPoints = ArrayBuffer(new Point(x, y))
  }

  def displayBox: Rectangle = {
    val iter: Iterator[Point] = points
    if (iter.hasNext) {
      val r: Rectangle = new Rectangle(iter.next)
      iter foreach {r.add(_)}
      r
    }
    else new Rectangle
  }

  override def isEmpty: Boolean = {
    return (size.width < 3) && (size.height < 3)
  }

  def handles: Seq[Handle] = {
    var handles: ArrayBuffer[Handle] = ArrayBuffer()
    for(i <- 0 to fPoints.size-1) handles += new PolyLineHandle(this, locator(i), i)
    handles
  }

  def basicDisplayBox(origin: Point, corner: Point) {
  }

  /**
   * Adds a node to the list of points.
   */
  def addPoint(x: Int, y: Int) {
    fPoints += new Point(x, y)
    changed
  }

  def points: Iterator[Point] = fPoints.iterator

  def pointCount: Int = fPoints.size

  protected def basicMoveBy(dx: Int, dy: Int) {
    points foreach {_.translate(dx, dy)}
  }

  /**
   * Changes the position of a node.
   */
  def setPointAt(p: Point, i: Int) {
    willChange
    fPoints = fPoints.updated(i, p)
    changed
  }

  /**
   * Insert a node at the given point.
   */
  def insertPointAt(p: Point, i: Int) {
    val (l, r) = fPoints.splitAt(i)
    fPoints = l + p ++ r
    changed
  }

  def removePointAt(i: Int) {
    willChange
    val (l, r) = fPoints.splitAt(i)
    fPoints = l ++ r.tail
    changed
  }

  /**
   * Splits the segment at the given point if a segment was hit.
   * @return the index of the segment or -1 if no segment was hit.
   */
  def splitSegment(x: Int, y: Int): Int = {
    val i: Int = findSegment(x, y)
    if (i != -1) {
      insertPointAt(new Point(x, y), i + 1)
    }
    i + 1
  }

  def pointAt(i: Int): Point = fPoints(i)

  /**
   * Joins to segments into one if the given point hits a node
   * of the polyline.
   * @return true if the two segments were joined.
   */
  def joinSegments(x: Int, y: Int): Boolean = {
    for(i <- 0 to fPoints.size - 2) {
      val p: Point = pointAt(i)
      if (Geom.length(x, y, p.x, p.y) < 3) {
        removePointAt(i)
        return true
      }
    }
    false
  }

  override def connectorAt(x: Int, y: Int): Connector = new PolyLineConnector(this)

  /**
   * Sets the start decoration.
   */
  def setStartDecoration(l: LineDecoration) {
    fStartDecoration = l
  }

  /**
   * Returns the start decoration.
   */
  def getStartDecoration: LineDecoration = fStartDecoration

  /**
   * Sets the end decoration.
   */
  def setEndDecoration(l: LineDecoration) {
    fEndDecoration = l
  }

  /**
   * Returns the end decoration.
   */
  def getEndDecoration: LineDecoration = fEndDecoration

  def draw(g: Graphics) {
    g.setColor(getFrameColor)
    var p1: Point = null
    var p2: Point = null
    for(i <- 0 to fPoints.size - 2) {
      p1 = pointAt(i)
      p2 = pointAt(i + 1)
      drawLine(g, p1.x, p1.y, p2.x, p2.y)
    }
    decorate(g)
  }

  /**
   * Can be overriden in subclasses to draw different types of lines
   * (e.g. dotted lines)
   */
  protected def drawLine(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int) {
    g.drawLine(x1, y1, x2, y2)
  }

  override def containsPoint(x: Int, y: Int): Boolean = {
    val bounds: Rectangle = displayBox
    bounds.grow(4, 4)
    if (!bounds.contains(x, y)) {
      return false
    }
    for(i <- 0 to fPoints.size - 2) {
      val p1: Point = pointAt(i)
      val p2: Point = pointAt(i + 1)
      if (Geom.lineContainsPoint(p1.x, p1.y, p2.x, p2.y, x, y)) {
        return true
      }
    }
    false
  }

  /**
   * Gets the segment of the polyline that is hit by
   * the given point.
   * @return the index of the segment or -1 if no segment was hit.
   */
  def findSegment(x: Int, y: Int): Int = {
    for(i <- 0 to fPoints.size - 2) {
      val p1: Point = pointAt(i)
      val p2: Point = pointAt(i + 1)
      if (Geom.lineContainsPoint(p1.x, p1.y, p2.x, p2.y, x, y)) {
        return i
      }
    }
    -1
  }

  private def decorate(g: Graphics) {
    if (getStartDecoration != null) {
      val p1: Point = pointAt(0)
      val p2: Point = pointAt(1)
      getStartDecoration.draw(g, p1.x, p1.y, p2.x, p2.y)
    }
    if (getEndDecoration != null) {
      val p3: Point = pointAt(fPoints.size - 2)
      val p4: Point = pointAt(fPoints.size - 1)
      getEndDecoration.draw(g, p4.x, p4.y, p3.x, p3.y)
    }
  }

  /**
   * Gets the attribute with the given name.
   * PolyLineFigure maps "ArrowMode"to a
   * line decoration.
   *
   * @deprecated use getAttribute(FigureAttributeConstant) instead
   */
  override def getAttribute(name: String): Any = getAttribute(FigureAttributeConstant.getConstant(name))

  /**
   * Gets the attribute with the given name.
   * PolyLineFigure maps "ArrowMode"to a
   * line decoration.
   */
  override def getAttribute(attributeConstant: FigureAttributeConstant): Any = {
    if (attributeConstant == FigureAttributeConstant.FRAME_COLOR) {
      return getFrameColor
    } else if (attributeConstant == FigureAttributeConstant.ARROW_MODE) {
      var value: Int = 0
      if (getStartDecoration != null) {
        value |= ARROW_TIP_START
      }
      if (getEndDecoration != null) {
        value |= ARROW_TIP_END
      }
      return value
    }
    return super.getAttribute(attributeConstant)
  }

  /**
   * Sets the attribute with the given name.
   * PolyLineFigure interprets "ArrowMode"to set
   * the line decoration.
   *
   * @deprecated use setAttribute(FigureAttributeConstant, Object) instead
   */
  override def setAttribute(name: String, value: Any) {
    setAttribute(FigureAttributeConstant.getConstant(name), value)
  }

  /**
   * Sets the attribute with the given name.
   * PolyLineFigure interprets "ArrowMode"to set
   * the line decoration.
   */
  override def setAttribute(attributeConstant: FigureAttributeConstant, value: Any) {
    if (attributeConstant == FigureAttributeConstant.FRAME_COLOR) {
      setFrameColor(value.asInstanceOf[Color])
      changed
    } else if (attributeConstant == FigureAttributeConstant.ARROW_MODE) {
      val intObj: Integer = value.asInstanceOf[Integer]
      if (intObj != null) {
        val decoration: Int = intObj.intValue
        if ((decoration & ARROW_TIP_START) != 0) {
          setStartDecoration(new ArrowTip)
        } else {
          setStartDecoration(null)
        }
        if ((decoration & ARROW_TIP_END) != 0) {
          setEndDecoration(new ArrowTip)
        } else {
          setEndDecoration(null)
        }
      }
      changed
    } else {
      super.setAttribute(attributeConstant, value)
    }
  }

  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeInt(fPoints.size)
    points foreach { p =>
      dw.writeInt(p.x)
      dw.writeInt(p.y)
    }
    dw.writeStorable(fStartDecoration)
    dw.writeStorable(fEndDecoration)
    dw.writeColor(fFrameColor)
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    val size: Int = dr.readInt
    fPoints = ArrayBuffer[Point]()
    for(i <- 0 to size-1) {
      val x: Int = dr.readInt
      val y: Int = dr.readInt
      fPoints += new Point(x, y)
    }
    setStartDecoration(dr.readStorable.asInstanceOf[LineDecoration])
    setEndDecoration(dr.readStorable.asInstanceOf[LineDecoration])
    fFrameColor = dr.readColor
  }

  protected def getFrameColor: Color = fFrameColor 

  protected def setFrameColor(c: Color) {
    fFrameColor = c
  }

  /**
   * Hook method to change the rectangle that will be invalidated
   */
  override protected def invalidateRectangle(r: Rectangle): Rectangle = {
    val parentR: Rectangle = super.invalidateRectangle(r)
    if (getStartDecoration != null) {
      parentR.add(getStartDecoration.displayBox)
    }
    if (getEndDecoration != null) {
      parentR.add(getEndDecoration.displayBox)
    }
    parentR
  }
}

