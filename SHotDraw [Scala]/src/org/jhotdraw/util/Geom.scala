/*
 * @(#)Geom.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import java.awt.Rectangle
import java.awt.Point
import java.awt.geom.Line2D

/**
 * Some geometric utilities.
 *
 * @version <$CURRENT_VERSION$>
 */
object Geom {
  /**
   * Tests if a point is on a line.
   */
  def lineContainsPoint(x1: Int, y1: Int, x2: Int, y2: Int, px: Int, py: Int): Boolean = {
    val r: Rectangle = new Rectangle(new Point(x1, y1))
    r.add(x2, y2)
    r.grow(2, 2)
    if (!r.contains(px, py)) {
      return false
    }
    var a: Double = .0
    var b: Double = .0
    var x: Double = .0
    var y: Double = .0
    if (x1 == x2) {
      return (Math.abs(px - x1) < 3)
    }
    if (y1 == y2) {
      return (Math.abs(py - y1) < 3)
    }
    a = (y1 - y2) / (x1 - x2)
    b = y1 - a * x1
    x = (py - b) / a
    y = a * px + b
    (Math.min(Math.abs(x - px), Math.abs(y - py)) < 4)
  }

  /**
   * Returns the direction NORTH, SOUTH, WEST, EAST from
   * one point to another one.
   */
  def direction(x1: Int, y1: Int, x2: Int, y2: Int): Int = {
    var direction: Int = 0
    val vx: Int = x2 - x1
    val vy: Int = y2 - y1
    if (vy < vx && vx > -vy) {
      direction = EAST
    }
    else if (vy > vx && vy > -vx) {
      direction = NORTH
    }
    else if (vx < vy && vx < -vy) {
      direction = WEST
    }
    else {
      direction = SOUTH
    }
    direction
  }

  def south(r: Rectangle): Point = new Point(r.x + r.width / 2, r.y + r.height)

  def center(r: Rectangle): Point = new Point(r.x + r.width / 2, r.y + r.height / 2)

  def west(r: Rectangle): Point = new Point(r.x, r.y + r.height / 2)

  def east(r: Rectangle): Point = new Point(r.x + r.width, r.y + r.height / 2)

  def north(r: Rectangle): Point = new Point(r.x + r.width / 2, r.y)

  /**
   * Returns the corner (bottom right) of the rectangle
   *
   * @param r  the rectangle
   * @return   the corner
   */
  def corner(r: Rectangle): Point = new Point(r.getMaxX.toInt, r.getMaxY.toInt)

  /**
   * Returns the top left corner of the rectangle
   *
   * @param r  the rectangle
   * @return   the corner
   */
  def topLeftCorner(r: Rectangle): Point = r.getLocation

  /**
   * Returns the top right corner of the rectangle
   *
   * @param r  the rectangle
   * @return   the corner
   */
  def topRightCorner(r: Rectangle): Point = new Point(r.getMaxX.toInt, r.getMinY.toInt)

  /**
   * Returns the bottom left corner of the rectangle
   *
   * @param r  the rectangle
   * @return   the corner
   */
  def bottomLeftCorner(r: Rectangle): Point = new Point(r.getMinX.toInt, r.getMaxY.toInt)

  /**
   * Returns the bottom right corner of the rectangle.
   * Same as corner, added for naming coherence with the other
   * corner extracting methods
   *
   * @param r  the rectangle
   * @return   the corner
   */
  def bottomRightCorner(r: Rectangle): Point = corner(r)

  /**
   * Constains a value to the given range.
   * @return the constrained value
   */
  def range(min: Int, max: Int, value: Int): Int = {
    if (value < min) {
      min
    }
    if (value > max) {
      max
    }
    value
  }

  /**
   * Gets the square distance between two points.
   */
  def length2(x1: Int, y1: Int, x2: Int, y2: Int): Long = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)

  /**
   * Gets the distance between to points
   */
  def length(x1: Int, y1: Int, x2: Int, y2: Int): Long = Math.sqrt(length2(x1, y1, x2, y2)).asInstanceOf[Long]

  /**
   * Gets the angle of a point relative to a rectangle.
   */
  def pointToAngle(r: Rectangle, p: Point): Double = {
    val px: Int = p.x - (r.x + r.width / 2)
    val py: Int = p.y - (r.y + r.height / 2)
    Math.atan2(py * r.width, px * r.height)
  }

  /**
   * Gets the point on a rectangle that corresponds to the given angle.
   */
  def angleToPoint(r: Rectangle, angle: Double): Point = {
    val si: Double = Math.sin(angle)
    val co: Double = Math.cos(angle)
    val e: Double = 0.0001
    var x: Int = 0
    var y: Int = 0
    if (Math.abs(si) > e) {
      x = ((1.0 + co / Math.abs(si)) / 2.0 * r.width).asInstanceOf[Int]
      x = range(0, r.width, x)
    }
    else if (co >= 0.0) {
      x = r.width
    }
    if (Math.abs(co) > e) {
      y = ((1.0 + si / Math.abs(co)) / 2.0 * r.height).asInstanceOf[Int]
      y = range(0, r.height, y)
    }
    else if (si >= 0.0) {
      y = r.height
    }
    new Point(r.x + x, r.y + y)
  }

  /**
   * Converts a polar to a point
   */
  def polarToPoint(angle: Double, fx: Double, fy: Double): Point = {
    val si: Double = Math.sin(angle)
    val co: Double = Math.cos(angle)
    new Point((fx * co + 0.5).asInstanceOf[Int], (fy * si + 0.5).asInstanceOf[Int])
  }

  /**
   * Gets the point on an oval that corresponds to the given angle.
   */
  def ovalAngleToPoint(r: Rectangle, angle: Double): Point = {
    val center: Point = Geom.center(r)
    val p: Point = Geom.polarToPoint(angle, r.width / 2, r.height / 2)
    new Point(center.x + p.x, center.y + p.y)
  }

  /**
   * Standard line intersection algorithm
   * Return the point of intersection if it exists, else null
   **/
  def intersect(xa: Int, ya: Int, xb: Int, yb: Int, xc: Int, yc: Int, xd: Int, yd: Int): Point = {
    val denom: Double = ((xb - xa) * (yd - yc) - (yb - ya) * (xd - xc))
    val rnum: Double = ((ya - yc) * (xd - xc) - (xa - xc) * (yd - yc))
    if (denom == 0.0) {
      if (rnum == 0.0) {
        if ((xa < xb && (xb < xc || xb < xd)) || (xa > xb && (xb > xc || xb > xd))) {
          return new Point(xb, yb)
        }
        else {
          return new Point(xa, ya)
        }
      }
      else {
        return null
      }
    }
    val r: Double = rnum / denom
    val snum: Double = ((ya - yc) * (xb - xa) - (xa - xc) * (yb - ya))
    val s: Double = snum / denom
    if (0.0 <= r && r <= 1.0 && 0.0 <= s && s <= 1.0) {
      val px: Int = (xa + (xb - xa) * r).asInstanceOf[Int]
      val py: Int = (ya + (yb - ya) * r).asInstanceOf[Int]
      return new Point(px, py)
    }
    else {
      return null
    }
  }

  /**
   * compute distance of point from line segment, or
   * Double.MAX_VALUE if perpendicular projection is outside segment; or
   * If pts on line are same, return distance from point
   **/
  def distanceFromLine(xa: Int, ya: Int, xb: Int, yb: Int, xc: Int, yc: Int): Double = {
    val xdiff: Int = xb - xa
    val ydiff: Int = yb - ya
    val l2: Long = xdiff * xdiff + ydiff * ydiff
    if (l2 == 0) {
      return Geom.length(xa, ya, xc, yc)
    }
    val rnum: Double = (ya - yc) * (ya - yb) - (xa - xc) * (xb - xa)
    val r: Double = rnum / l2
    if (r < 0.0 || r > 1.0) {
      return java.lang.Double.MAX_VALUE
    }
    val xi: Double = xa + r * xdiff
    val yi: Double = ya + r * ydiff
    val xd: Double = xc - xi
    val yd: Double = yc - yi
    Math.sqrt(xd * xd + yd * yd)
  }

  /**
   * compute distance of point from line segment.<br>
   * Uses AWT Line2D utility methods
   */
  def distanceFromLine2D(xa: Int, ya: Int, xb: Int, yb: Int, xc: Int, yc: Int): Double = {
    val line: Line2D.Double = new Line2D.Double(xa, xb, ya, yb)
    line.ptSegDist(xc, yc)
  }

  val NORTH: Int = 1
  val SOUTH: Int = 2
  val WEST: Int = 3
  val EAST: Int = 4
}

