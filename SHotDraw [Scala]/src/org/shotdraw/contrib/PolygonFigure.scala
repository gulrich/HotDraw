/*
 * @(#)PolygonFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.contrib

import java.awt.Graphics
import java.awt.Point
import java.awt.Polygon
import java.awt.Rectangle
import java.io.IOException
import org.shotdraw.framework.Connector
import org.shotdraw.framework.Figure
import org.shotdraw.framework.Handle
import org.shotdraw.framework.Locator
import org.shotdraw.standard.AbstractLocator
import org.shotdraw.util.Geom
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput
import scala.collection.mutable.ArrayBuffer
import org.shotdraw.standard.AbstractFigure

/**
 * A scalable, rotatable polygon with an arbitrary number of points
 * Based on PolyLineFigure
 *
 * @author Doug Lea  (dl at gee, Fri Feb 28 07:47:05 1997)
 * @version <$CURRENT_VERSION$>
 */
object PolygonFigure {
  /**
   * Creates a locator for the point with the given index.
   */
  def locator(pointIndex: Int): Locator = new AbstractLocator {
    def locate(owner: Figure): Point = {
      val plf = owner.asInstanceOf[PolygonFigure]
      if (pointIndex < plf.pointCount) {
        return (owner.asInstanceOf[PolygonFigure]).pointAt(pointIndex)
      }
      new Point(-1, -1)
    }
  }

  /**
   * replacement for builtin Polygon.getBounds that doesn't always update?
   */
  def bounds(p: Polygon): Rectangle = {
    var minx: Int = Integer.MAX_VALUE
    var miny: Int = Integer.MAX_VALUE
    var maxx: Int = Integer.MIN_VALUE
    var maxy: Int = Integer.MIN_VALUE
    for(x <- p.xpoints; y <- p.ypoints) {
      if (x > maxx) {
        maxx = x
      }
      if (x < minx) {
        minx = x
      }
      if (y > maxy) {
        maxy = y
      }
      if (y < miny) {
        miny = y
      }
    }
    new Rectangle(minx, miny, maxx - minx, maxy - miny)
  }

  def center(p: Polygon): Point = {
    var sx: Long = p.xpoints.foldLeft(0)((x,y) => x+y)
    var sy: Long = p.ypoints.foldLeft(0)((x,y) => x+y)
    val n = p.npoints
    new Point((sx / n).asInstanceOf[Int], (sy / n).asInstanceOf[Int])
  }

  def chop(poly: Polygon, p: Point): Point = {
    val ctr = center(poly)
    var cx: Int = -1
    var cy: Int = -1
    var len: Long = Long.MaxValue
    for(i <- 0 to poly.npoints - 1) {
      val nxt = (i + 1) % poly.npoints
      val chop = Geom.intersect(poly.xpoints(i), poly.ypoints(i), poly.xpoints(nxt), poly.ypoints(nxt), p.x, p.y, ctr.x, ctr.y)
      if (chop != null) {
        val cl = Geom.length2(chop.x, chop.y, p.x, p.y)
        if (cl < len) {
          len = cl
          cx = chop.x
          cy = chop.y
        }
      }
    }
    for(i <- 0 to poly.npoints - 1) {
      val l = Geom.length2(poly.xpoints(i), poly.ypoints(i), p.x, p.y)
      if (l < len) {
        len = l
        cx = poly.xpoints(i)
        cy = poly.ypoints(i)
      }
    }
    new Point(cx, cy)
  }

  /**
   * Distance threshold for smoothing away or locating points
   **/
  private[contrib] final val TOO_CLOSE = 2
}

class PolygonFigure extends AbstractFigure {
  import PolygonFigure._
  private var fPoly: Polygon = new Polygon
  
  def this(x: Int, y: Int) {
    this()
    getInternalPolygon.addPoint(x, y)
  }

  def this(p: Polygon) {
    this()
    setInternalPolygon(new Polygon(p.xpoints, p.ypoints, p.npoints))
  }

  def displayBox: Rectangle = bounds(getInternalPolygon)

  override def isEmpty: Boolean = ((pointCount < 3) || ((size.width < TOO_CLOSE) && (size.height < TOO_CLOSE)))

  def handles: Seq[Handle] = {
    var handles: ArrayBuffer[Handle] = ArrayBuffer[Handle]()
    for(i <- 0 to pointCount-1) {
      handles += new PolygonHandle(this, locator(i), i) 
    }
    handles += new PolygonScaleHandle(this)
    handles
  }

  def basicDisplayBox(origin: Point, corner: Point) {
    var r: Rectangle = displayBox
    val dx = origin.x - r.x
    val dy = origin.y - r.y
    getInternalPolygon.translate(dx, dy)
    r = displayBox
    val oldCorner = new Point(r.x + r.width, r.y + r.height)
    scaleRotate(oldCorner, getInternalPolygon, corner)
  }

  /**
   * @return a copy of the internal polygon
   **/
  def getPolygon: Polygon = new Polygon(fPoly.xpoints, fPoly.ypoints, fPoly.npoints)

  private[contrib] def setInternalPolygon(newPolygon: Polygon) {
    fPoly = newPolygon
  }

  def getInternalPolygon: Polygon = fPoly

  override def center: Point = PolygonFigure.center(getInternalPolygon)

  def points: Seq[Point] = {
    var pts: ArrayBuffer[Point] = ArrayBuffer[Point]()
    for(i <- 0 to pointCount-1) {
      pts += new Point(getInternalPolygon.xpoints(i), getInternalPolygon.ypoints(i))
    }
    pts
  }

  def pointCount: Int = getInternalPolygon.npoints

  def basicMoveBy(dx: Int, dy: Int) {
    getInternalPolygon.translate(dx, dy)
  }

  override def drawBackground(g: Graphics) {
    g.fillPolygon(getInternalPolygon)
  }

  override def drawFrame(g: Graphics) {
    g.drawPolygon(getInternalPolygon)
  }

  override def containsPoint(x: Int, y: Int): Boolean = getInternalPolygon.contains(x, y)

  override def connectorAt(x: Int, y: Int): Connector = new ChopPolygonConnector(this)

  /**
   * Adds a node to the list of points.
   */
  def addPoint(x: Int, y: Int) {
    getInternalPolygon.addPoint(x, y)
    changed
  }

  /**
   * Changes the position of a node.
   */
  def setPointAt(p: Point, i: Int) {
    willChange
    getInternalPolygon.xpoints(i) = p.x
    getInternalPolygon.ypoints(i) = p.y
    changed
  }

  /**
   * Insert a node at the given point.
   */
  def insertPointAt(p: Point, i: Int) {
    willChange
    val n = pointCount + 1
    val xs = new Array[Int](n)
    val ys = new Array[Int](n)
    
    for(j <- 0 to i - 1) {
      xs(j) = getInternalPolygon.xpoints(j)
      ys(j) = getInternalPolygon.ypoints(j)
    }
    xs(i) = p.x
    ys(i) = p.y
    for(j <- i to pointCount - 1) {
      xs(j + 1) = getInternalPolygon.xpoints(j)
      ys(j + 1) = getInternalPolygon.ypoints(j)
    }
    setInternalPolygon(new Polygon(xs, ys, n))
    changed
  }

  def removePointAt(i: Int) {
    willChange
    val n = pointCount - 1
    val xs = new Array[Int](n)
    val ys = new Array[Int](n)
    
    for(j <- 0 to i-1) {
      xs(j) = getInternalPolygon.xpoints(j)
      ys(j) = getInternalPolygon.ypoints(j)
    }
    for(j <- i to n-1) {
      xs(j) = getInternalPolygon.xpoints(j + 1)
      ys(j) = getInternalPolygon.ypoints(j + 1)
    }
    setInternalPolygon(new Polygon(xs, ys, n))
    changed
  }

  /**
   * Scale and rotate relative to anchor
   **/
  def scaleRotate(anchor: Point, originalPolygon: Polygon, p: Point) {
    willChange
    val ctr = PolygonFigure.center(originalPolygon)
    val anchorLen = Geom.length(ctr.x, ctr.y, anchor.x, anchor.y)
    if (anchorLen > 0.0) {
      val newLen = Geom.length(ctr.x, ctr.y, p.x, p.y)
      val ratio = newLen / anchorLen
      val anchorAngle = math.atan2(anchor.y - ctr.y, anchor.x - ctr.x)
      val newAngle = math.atan2(p.y - ctr.y, p.x - ctr.x)
      val rotation = newAngle - anchorAngle
      val n = originalPolygon.npoints
      val xs = new Array[Int](n)
      val ys = new Array[Int](n)
      
      for(i <- 0 to n-1) {
        val x = originalPolygon.xpoints(i)
        val y = originalPolygon.ypoints(i)
        val l = Geom.length(ctr.x, ctr.y, x, y) * ratio
        val a = math.atan2(y - ctr.y, x - ctr.x) + rotation
        xs(i) = (ctr.x + l * math.cos(a) + 0.5).toInt
        ys(i) = (ctr.y + l * math.sin(a) + 0.5).toInt
      }
      setInternalPolygon(new Polygon(xs, ys, n))
    }
    changed
  }

  /**
   * Remove points that are nearly colinear with others
   **/
  def smoothPoints {
    willChange
    var removed: Boolean = false
    var n: Int = pointCount
    do {
      removed = false
      var i: Int = 0
      while (i < n && n >= 3) {
        val nxt = (i + 1) % n
        val prv = (i - 1 + n) % n
        if ((Geom.distanceFromLine(getInternalPolygon.xpoints(prv), getInternalPolygon.ypoints(prv), getInternalPolygon.xpoints(nxt), getInternalPolygon.ypoints(nxt), getInternalPolygon.xpoints(i), getInternalPolygon.ypoints(i)) < TOO_CLOSE)) {
          removed = true
          n -= 1
          for(j <- i to n-1) {
            getInternalPolygon.xpoints(j) = getInternalPolygon.xpoints(j + 1)
            getInternalPolygon.ypoints(j) = getInternalPolygon.ypoints(j + 1)
          }
        } else {
          i += 1
        }
      }
    } while (removed)
    if (n != pointCount) {
      setInternalPolygon(new Polygon(getInternalPolygon.xpoints, getInternalPolygon.ypoints, n))
    }
    changed
  }

  /**
   * Splits the segment at the given point if a segment was hit.
   * @return the index of the segment or -1 if no segment was hit.
   */
  def splitSegment(x: Int, y: Int): Int = {
    val i = findSegment(x, y)
    if (i != -1) {
      insertPointAt(new Point(x, y), i + 1)
      i + 1
    } else -1
  }

  def pointAt(i: Int): Point = new Point(getInternalPolygon.xpoints(i), getInternalPolygon.ypoints(i))

  /**
   * Return the point on the polygon that is furthest from the center
   **/
  def outermostPoint: Point = {
    val ctr = center
    var outer: Int = 0
    var dist: Long = 0
    for(i <- 0 to pointCount - 1) {
      val d = Geom.length2(ctr.x, ctr.y, getInternalPolygon.xpoints(i), getInternalPolygon.ypoints(i))
      if (d > dist) {
        dist = d
        outer = i
      }
    }
    new Point(getInternalPolygon.xpoints(outer), getInternalPolygon.ypoints(outer))
  }

  /**
   * Gets the segment that is hit by the given point.
   * @return the index of the segment or -1 if no segment was hit.
   */
  def findSegment(x: Int, y: Int): Int = {
    var dist: Double = TOO_CLOSE
    var best: Int = -1
    for(i <- 0 to pointCount) {
      val n = (i + 1) % pointCount
      val d = Geom.distanceFromLine(getInternalPolygon.xpoints(i), getInternalPolygon.ypoints(i), getInternalPolygon.xpoints(n), getInternalPolygon.ypoints(n), x, y)
      if (d < dist) {
        dist = d
        best = i
      }
    }
    best
  }

  def chop(p: Point): Point = PolygonFigure.chop(getInternalPolygon, p)

  override def write(dw: StorableOutput) {
    super.write(dw)
    dw.writeInt(pointCount)
    (getInternalPolygon.xpoints zip getInternalPolygon.ypoints) foreach { case (x, y) =>      
      dw.writeInt(x)      
      dw.writeInt(y)
    }
  }

  override def read(dr: StorableInput) {
    super.read(dr)
    val size = dr.readInt
    val xs = new Array[Int](size)
    val ys = new Array[Int](size)
    for(i <- 0 to size-1) {
      xs(i) = dr.readInt
      ys(i) = dr.readInt
    }
    setInternalPolygon(new Polygon(xs, ys, size))
  }
}

