/*
 * @(#)Bounds.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import java.awt.Dimension
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.io.Serializable

/**
 * This class is a rectangle with floating point
 * dimensions and location.  This class provides
 * many convenient geometrical methods related to
 * rectangles.  Basically, this class is like
 * java.awt.geom.Rectangle2D with some extra
 * functionality.
 *
 * @author WMG (28.02.1999)
 * @version <$CURRENT_VERSION$>
 */
class Bounds(var x1: Double, var y1: Double, var x2: Double, var y2: Double) extends Serializable {

  def this(x: Double, y: Double) {
    this(x, y, x, y)
  }
  
  def this(aPoint2D: Point2D) {
    this(aPoint2D.getX, aPoint2D.getY)
  }

  def this(firstPoint2D: Point2D, secondPoint2D: Point2D) {
    this(firstPoint2D.getX, firstPoint2D.getY, secondPoint2D.getX, secondPoint2D.getY)
  }

  def this(aBounds: Bounds) {
    this(aBounds.getLesserX, aBounds.getLesserY, aBounds.getGreaterX, aBounds.getGreaterY)
  }

  def this(aRectangle2D: Rectangle2D) {
    this(aRectangle2D.getMinX, aRectangle2D.getMinY, aRectangle2D.getMaxX, aRectangle2D.getMaxY)
  }

  def this(centerPoint2D: Point2D, dWidth: Double, dHeight: Double) {
    this(centerPoint2D.getX - (dWidth / 2.0), centerPoint2D.getY - (dHeight / 2.0), centerPoint2D.getX - (dWidth / 2.0), centerPoint2D.getY + (dHeight / 2.0))
  }

  def this(aDimension: Dimension) {
    this(0, 0, aDimension.width, aDimension.height)
  }

  def getLesserX: Double = math.min(x1, x2)

  def getGreaterX: Double = math.max(x1, x2)

  def getLesserY: Double = math.min(y1, y2)

  def getGreaterY: Double = math.max(y1, y2)

  def getWest: Double = getLesserX

  def getEast: Double = getGreaterX

  def getSouth: Double = getLesserY

  def getNorth: Double = getGreaterY

  def getWidth: Double = getGreaterX - getLesserX

  def getHeight: Double = getGreaterY - getLesserY

  def asRectangle2D: Rectangle2D = new Rectangle2D.Double(getLesserX, getLesserY, getWidth, getHeight)

  def setCenter(centerPoint2D: Point2D) {
    if (centerPoint2D == null) {
      throw new IllegalArgumentException
    }
    val currentCenterPoint2D = getCenter
    val dDeltaX = centerPoint2D.getX - currentCenterPoint2D.getX
    val dDeltaY = centerPoint2D.getY - currentCenterPoint2D.getY
    offset(dDeltaX, dDeltaY)
  }

  def getCenter: Point2D = new Point2D.Double((x1 + this.x2) / 2.0, (this.y1 + this.y2) / 2.0)

  def zoomBy(dRatio: Double) {
    val dWidth = this.x2 - this.x1
    val dHeight = this.y2 - this.y1
    val dNewWidth = (dWidth * dRatio)
    val dNewHeight = (dHeight * dRatio)
    val centerPoint2D = getCenter
    this.x1 = centerPoint2D.getX - (dNewWidth / 2.0)
    this.y1 = centerPoint2D.getY - (dNewHeight / 2.0)
    this.x2 = centerPoint2D.getX + (dNewWidth / 2.0)
    this.y2 = centerPoint2D.getY + (dNewHeight / 2.0)
  }

  def shiftBy(nXPercentage: Int, nYPercentage: Int) {
    val dWidth = this.x2 - this.x1
    val dHeight = this.y2 - this.y1
    val dDeltaX = (dWidth * nXPercentage) / 100.0
    val dDeltaY = (dHeight * nYPercentage) / 100.0
    offset(dDeltaX, dDeltaY)
  }

  def offset(dDeltaX: Double, dDeltaY: Double) {
    this.x1 += dDeltaX
    this.x2 += dDeltaX
    this.y1 += dDeltaY
    this.y2 += dDeltaY
  }

  /**
   * This will cause the bounds to grow until the given ratio
   * is satisfied. The Ration is calculated by
   * <code> getWidth() / getHeight() </code>
   **/
  def expandToRatio(dRatio: Double) {
    val dCurrentRatio = getWidth / getHeight
    if (dCurrentRatio < dRatio) {
      val dNewWidth = dRatio * getHeight
      val dCenterX = (this.x1 + this.x2) / 2.0
      val dDelta = dNewWidth / 2.0
      this.x1 = dCenterX - dDelta
      this.x2 = dCenterX + dDelta
    }
    if (dCurrentRatio > dRatio) {
      val dNewHeight = getWidth / dRatio
      val dCenterY = (this.y1 + this.y2) / 2.0
      val dDelta = dNewHeight / 2.0
      this.y1 = dCenterY - dDelta
      this.y2 = dCenterY + dDelta
    }
  }

  def includeXCoordinate(x: Double) {
    this.x1 = min(this.x1, this.x2, x)
    this.x2 = max(this.x1, this.x2, x)
  }

  def includeYCoordinate(y: Double) {
    this.y1 = min(this.y1, this.y2, y)
    this.y2 = max(this.y1, this.y2, y)
  }

  def includePoint(x: Double, y: Double) {
    includeXCoordinate(x)
    includeYCoordinate(y)
  }

  def includePoint(aPoint2D: Point2D) {
    includePoint(aPoint2D.getX, aPoint2D.getY)
  }

  def includeLine(x1: Double, y1: Double, x2: Double, y2: Double) {
    includePoint(x1, y1)
    includePoint(x2, y2)
  }

  def includeLine(onePoint2D: Point2D, twoPoint2D: Point2D) {
    includeLine(onePoint2D.getX, onePoint2D.getY, twoPoint2D.getX, twoPoint2D.getY)
  }

  def includeBounds(aBounds: Bounds) {
    includeXCoordinate(aBounds.getLesserX)
    includeXCoordinate(aBounds.getGreaterX)
    includeYCoordinate(aBounds.getLesserY)
    includeYCoordinate(aBounds.getGreaterY)
  }

  def includeRectangle2D(aRectangle2D: Rectangle2D) {
    includeXCoordinate(aRectangle2D.getMinX)
    includeXCoordinate(aRectangle2D.getMaxX)
    includeYCoordinate(aRectangle2D.getMinY)
    includeYCoordinate(aRectangle2D.getMaxY)
  }

  def intersect(aBounds: Bounds) {
    this.x1 = math.max(this.x1, aBounds.getLesserX)
    this.y1 = math.max(this.y1, aBounds.getLesserY)
    this.x2 = math.min(this.x2, aBounds.getGreaterX)
    this.y2 = math.min(this.y2, aBounds.getGreaterY)
    if (this.x1 > this.x2) {
      this.x1 = this.x2
    }
    if (this.y1 > this.y2) {
      this.y1 = this.y2
    }
  }

  def intersectsPoint(x: Double, y: Double): Boolean = ((this.x1 <= x) && (x <= this.x2) && (this.y1 <= y) && (y <= this.y2))

  def intersectsPoint(aPoint2D: Point2D): Boolean = intersectsPoint(aPoint2D.getX, aPoint2D.getY)

  def intersectsLine(x1: Double, y1: Double, x2: Double, y2: Double): Boolean = {
    if (intersectsPoint(x1, y1)) {
      return true
    }
    if (intersectsPoint(x2, y2)) {
      return true
    }
    if ((x1 < this.x1) && (x2 < this.x1)) {
      return false
    }
    if ((x1 > this.x2) && (x2 > this.x2)) {
      return false
    }
    if ((y1 < this.y1) && (y2 < this.y1)) {
      return false
    }
    if ((y1 > this.y2) && (y2 > this.y2)) {
      return false
    }
    if (((this.x1 <= x1) && (x1 <= this.x2)) && ((this.x1 <= x2) && (x2 <= this.x2))) {
      return true
    }
    if (((this.y1 <= y1) && (y1 <= this.y2)) && ((this.y1 <= y2) && (y2 <= this.y2))) {
      return true
    }
    val dSlope = (y2 - y1) / (x2 - x1)
    val _dYIntersectionAtX1 = dSlope * (this.x1 - x1) + y1
    val _dYIntersectionAtX2 = dSlope * (this.x2 - x1) + y1
    val _dXIntersectionAtY1 = (this.y1 - y1) / dSlope + x1
    val _dXIntersectionAtY2 = (this.y2 - y1) / dSlope + x1
    (intersectsPoint(this.x1, _dYIntersectionAtX1)) || (intersectsPoint(this.x2, _dYIntersectionAtX2)) || (intersectsPoint(_dXIntersectionAtY1, this.y1)) || (intersectsPoint(_dXIntersectionAtY2, this.y2))
  }

  def intersectsLine(onePoint2D: Point2D, twoPoint2D: Point2D): Boolean = intersectsLine(onePoint2D.getX, onePoint2D.getY, twoPoint2D.getX, twoPoint2D.getY)

  def intersectsBounds(aBounds: Bounds): Boolean = {
    val dLesserX = aBounds.getLesserX
    val dGreaterX = aBounds.getGreaterX
    val dLesserY = aBounds.getLesserY
    val dGreaterY = aBounds.getGreaterY
    if (dLesserX < this.x1) {
      if (dLesserY < this.y1) {
        return ((dGreaterX >= this.x1) && (dGreaterY >= this.y1))
      }
      else {
        return ((dGreaterX >= this.x1) && (dLesserY <= this.y2))
      }
    }
    else {
      if (dLesserY < this.y1) {
        return ((dLesserX <= this.x2) && (dGreaterY >= this.y1))
      }
      else {
        return ((dLesserX <= this.x2) && (dLesserY <= this.y2))
      }
    }
  }

  def completelyContainsLine(x1: Double, y1: Double, x2: Double, y2: Double): Boolean = (this.x1 > math.min(x1, x2)) && (this.x2 < math.max(x1, x2)) && (this.y1 > math.min(y1, y2)) && (this.y2 < math.max(y1, y2))

  def isCompletelyInside(aBounds: Bounds): Boolean = (this.x1 > aBounds.getLesserX) && (this.x2 < aBounds.getGreaterX) && (this.y1 > aBounds.getLesserY) && (this.y2 < aBounds.getGreaterY)

  def cropLine(x1: Double, y1: Double, x2: Double, y2: Double): Array[Point2D] = {
    if (!intersectsLine(x1, y1, x2, y2)) {
      return Array()
    }
    val resultLine = new Array[Point2D](2)
    val beginPoint2D = new Point2D.Double(x1, y1)
    val endPoint2D = new Point2D.Double(x2, y2)
    if (beginPoint2D.getX == endPoint2D.getX) {
      if (beginPoint2D.getY > y2) {
        beginPoint2D.setLocation(beginPoint2D.getX, this.y2)
      }
      if (endPoint2D.getY > this.y2) {
        endPoint2D.setLocation(endPoint2D.getX, this.y2)
      }
      if (beginPoint2D.getY < this.y1) {
        beginPoint2D.setLocation(beginPoint2D.getX, this.y1)
      }
      if (endPoint2D.getY < this.y1) {
        endPoint2D.setLocation(endPoint2D.getX, this.y1)
      }
    }
    else if (beginPoint2D.getY == endPoint2D.getY) {
      if (beginPoint2D.getX > this.x2) {
        beginPoint2D.setLocation(this.x2, beginPoint2D.getY)
      }
      if (endPoint2D.getX > this.x2) {
        endPoint2D.setLocation(this.x2, endPoint2D.getY)
      }
      if (beginPoint2D.getX < this.x1) {
        beginPoint2D.setLocation(this.x1, beginPoint2D.getY)
      }
      if (endPoint2D.getX < this.x1) {
        endPoint2D.setLocation(this.x1, endPoint2D.getY)
      }
    }
    else {
      val dSlope = (beginPoint2D.getY - endPoint2D.getY) / (beginPoint2D.getX - endPoint2D.getX)
      if (!intersectsPoint(beginPoint2D)) {
        if (beginPoint2D.getY > this.y2) {
          val x = ((this.y2 - beginPoint2D.getY) / dSlope) + beginPoint2D.getX
          if ((x >= this.x1) && (x <= this.x2)) {
            beginPoint2D.setLocation(x, beginPoint2D.getY)
            beginPoint2D.setLocation(beginPoint2D.getX, this.y2)
          }
        }
        if (beginPoint2D.getY < this.y1) {
          val x = ((this.y1 - beginPoint2D.getY) / dSlope) + beginPoint2D.getX
          if ((x >= this.x1) && (x <= this.x2)) {
            beginPoint2D.setLocation(x, beginPoint2D.getY)
            beginPoint2D.setLocation(beginPoint2D.getX, this.y1)
          }
        }
        if (beginPoint2D.getX > this.x2) {
          val y = dSlope * (this.x2 - beginPoint2D.getX) + beginPoint2D.getY
          if ((y >= this.y1) && (y <= this.y2)) {
            beginPoint2D.setLocation(this.x2, beginPoint2D.getY)
            beginPoint2D.setLocation(beginPoint2D.getX, y)
          }
        }
        if (beginPoint2D.getX < this.x1) {
          val y = dSlope * (this.x1 - beginPoint2D.getX) + beginPoint2D.getY
          if ((y >= this.y1) && (y <= this.y2)) {
            beginPoint2D.setLocation(this.x1, beginPoint2D.getY)
            beginPoint2D.setLocation(beginPoint2D.getX, y)
          }
        }
      }
      if (!intersectsPoint(endPoint2D)) {
        if (endPoint2D.getY > this.y2) {
          val x = ((this.y2 - beginPoint2D.getY) / dSlope) + beginPoint2D.getX
          if ((x >= this.x1) && (x <= this.x2)) {
            endPoint2D.setLocation(x, endPoint2D.getY)
            endPoint2D.setLocation(endPoint2D.getX, this.y2)
          }
        }
        if (endPoint2D.getY < this.y1) {
          val x = ((this.y1 - beginPoint2D.getY) / dSlope) + beginPoint2D.getX
          if ((x >= this.x1) && (x <= this.x2)) {
            endPoint2D.setLocation(x, endPoint2D.getY)
            endPoint2D.setLocation(endPoint2D.getX, this.y1)
          }
        }
        if (endPoint2D.getX > this.x2) {
          val y = dSlope * (this.x2 - beginPoint2D.getX) + beginPoint2D.getY
          if ((y >= this.y1) && (y <= this.y2)) {
            endPoint2D.setLocation(this.x2, endPoint2D.getY)
            endPoint2D.setLocation(endPoint2D.getX, y)
          }
        }
        if (endPoint2D.getX < this.x1) {
          val y = dSlope * (this.x1 - beginPoint2D.getX) + beginPoint2D.getY
          if ((y >= this.y1) && (y <= this.y2)) {
            endPoint2D.setLocation(this.x1, endPoint2D.getY)
            endPoint2D.setLocation(endPoint2D.getX, y)
          }
        }
      }
    }
    resultLine(0) = beginPoint2D
    resultLine(1) = endPoint2D
    resultLine
  }

  override def equals(anObject: Any): Boolean = anObject match {
    case b: Bounds => (x1 == b.getLesserX) && (x2 == b.getGreaterX) && (y1 == b.getLesserY) && (y2 == b.getGreaterY) 
    case _ => false
  }

  override def hashCode: Int = {
    var temp = math.abs(x1 + x2 + y1 + y2)
    while ((temp != 0) && (temp < 1)) {
      temp *= 4
    }
    return temp.asInstanceOf[Int]
  }

  override def toString: String = x1 + " " + y1 + " " + x2 + " " + y2

  private def min(x1: Double, x2: Double, x3: Double): Double = math.min(math.min(x1, x2), x3)

  private def max(x1: Double, x2: Double, x3: Double): Double = math.max(math.max(x1, x2), x3)

}

