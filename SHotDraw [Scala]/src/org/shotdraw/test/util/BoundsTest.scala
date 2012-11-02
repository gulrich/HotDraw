/*
 * @(#)Test.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.test.util

import java.awt.Dimension
import java.awt.geom.Point2D
import org.shotdraw.util.Bounds
import junit.framework.TestCase
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach

// JUnitDoclet begin import
// JUnitDoclet end import
/*
* Generated by JUnitDoclet, a tool provided by
* ObjectFab GmbH under LGPL.
* Please see www.junitdoclet.org, www.gnu.org
* and www.objectfab.de for informations about
* the tool, the licence and the authors.
*/
// JUnitDoclet begin javadoc_class
/**
 * TestCase BoundsTest is generated by
 * JUnitDoclet to hold the tests for Bounds.
 * @see org.shotdraw.util.Bounds
 */
class BoundsTest(name: String) extends TestCase(name) {


  /**
   * Factory method for instances of the class to be tested.
   */
  def createInstance: Bounds = {
    new Bounds(new Dimension(100, 100))
  }

  /**
   * Method setUp is overwriting the framework method to
   * prepare an instance of this TestCase for a single test.
   * It's called from the JUnit framework only.
   */
  override protected def setUp {
    super.setUp
    bounds = createInstance
  }

  /**
   * Method tearDown is overwriting the framework method to
   * clean up after each single test of this TestCase.
   * It's called from the JUnit framework only.
   */
  override protected def tearDown {
    bounds = null
    super.tearDown
  }

  /**
   * Method testGetLesserX is testing getLesserX
   * @see org.shotdraw.util.Bounds#getLesserX()
   */
  def testGetLesserX {
  }

  /**
   * Method testGetGreaterX is testing getGreaterX
   * @see org.shotdraw.util.Bounds#getGreaterX()
   */
  def testGetGreaterX {
  }

  /**
   * Method testGetLesserY is testing getLesserY
   * @see org.shotdraw.util.Bounds#getLesserY()
   */
  def testGetLesserY {
  }

  /**
   * Method testGetGreaterY is testing getGreaterY
   * @see org.shotdraw.util.Bounds#getGreaterY()
   */
  def testGetGreaterY {
  }

  /**
   * Method testGetWest is testing getWest
   * @see org.shotdraw.util.Bounds#getWest()
   */
  def testGetWest {
  }

  /**
   * Method testGetEast is testing getEast
   * @see org.shotdraw.util.Bounds#getEast()
   */
  def testGetEast {
  }

  /**
   * Method testGetSouth is testing getSouth
   * @see org.shotdraw.util.Bounds#getSouth()
   */
  def testGetSouth {
  }

  /**
   * Method testGetNorth is testing getNorth
   * @see org.shotdraw.util.Bounds#getNorth()
   */
  def testGetNorth {
  }

  /**
   * Method testGetWidth is testing getWidth
   * @see org.shotdraw.util.Bounds#getWidth()
   */
  def testGetWidth {
  }

  /**
   * Method testGetHeight is testing getHeight
   * @see org.shotdraw.util.Bounds#getHeight()
   */
  def testGetHeight {
  }

  /**
   * Method testAsRectangle2D is testing asRectangle2D
   * @see org.shotdraw.util.Bounds#asRectangle2D()
   */
  def testAsRectangle2D {
  }

  /**
   * Method testSetGetCenter is testing setCenter
   * and getCenter together by setting some value
   * and verifying it by reading.
   * @see org.shotdraw.util.Bounds#setCenter(java.awt.geom.Point2D)
   * @see org.shotdraw.util.Bounds#getCenter()
   */
  def testSetGetCenter {
    val tests: List[Point2D] = List(new Point2D.Double(2.0, 3.0))
    tests foreach {e =>
      bounds.setCenter(e)
	  assert(e == bounds.getCenter)
    }
			
  }

  /**
   * Test a null argument to setCenter.  Expect an IllegalArgumentException
   *
   * @see org.shotdraw.util.Bounds#setCenter(java.awt.geom.Point2D)
   */
  def testSetNullCenter {
    val original: Point2D = bounds.getCenter
    try {
      bounds.setCenter(null)
      sys.error("IllegalArgumentException expected")
    }
    catch {
      case ok: IllegalArgumentException => {
        assert(original == bounds.getCenter)
      }
    }
  }

  /**
   * Method testZoomBy is testing zoomBy
   * @see org.shotdraw.util.Bounds#zoomBy(double)
   */
  def testZoomBy {
  }

  /**
   * Method testShiftBy is testing shiftBy
   * @see org.shotdraw.util.Bounds#shiftBy(int, int)
   */
  def testShiftBy {
  }

  /**
   * Method testOffset is testing offset
   * @see org.shotdraw.util.Bounds#offset(double, double)
   */
  def testOffset {
  }

  /**
   * Method testExpandToRatio is testing expandToRatio
   * @see org.shotdraw.util.Bounds#expandToRatio(double)
   */
  def testExpandToRatio {
  }

  /**
   * Method testIncludeXCoordinate is testing includeXCoordinate
   * @see org.shotdraw.util.Bounds#includeXCoordinate(double)
   */
  def testIncludeXCoordinate {
  }

  /**
   * Method testIncludeYCoordinate is testing includeYCoordinate
   * @see org.shotdraw.util.Bounds#includeYCoordinate(double)
   */
  def testIncludeYCoordinate {
  }

  /**
   * Method testIncludePoint is testing includePoint
   * @see org.shotdraw.util.Bounds#includePoint(double, double)
   */
  def testIncludePoint {
  }

  /**
   * Method testIncludeLine is testing includeLine
   * @see org.shotdraw.util.Bounds#includeLine(double, double, double, double)
   */
  def testIncludeLine {
  }

  /**
   * Method testIncludeBounds is testing includeBounds
   * @see org.shotdraw.util.Bounds#includeBounds(org.shotdraw.util.Bounds)
   */
  def testIncludeBounds {
  }

  /**
   * Method testIncludeRectangle2D is testing includeRectangle2D
   * @see org.shotdraw.util.Bounds#includeRectangle2D(java.awt.geom.Rectangle2D)
   */
  def testIncludeRectangle2D {
  }

  /**
   * Method testIntersect is testing intersect
   * @see org.shotdraw.util.Bounds#intersect(org.shotdraw.util.Bounds)
   */
  def testIntersect {
  }

  /**
   * Method testIntersectsPoint is testing intersectsPoint
   * @see org.shotdraw.util.Bounds#intersectsPoint(double, double)
   */
  def testIntersectsPoint {
  }

  /**
   * Method testIntersectsLine is testing intersectsLine
   * @see org.shotdraw.util.Bounds#intersectsLine(double, double, double, double)
   */
  def testIntersectsLine {
  }

  /**
   * Method testIntersectsBounds is testing intersectsBounds
   * @see org.shotdraw.util.Bounds#intersectsBounds(org.shotdraw.util.Bounds)
   */
  def testIntersectsBounds {
  }

  /**
   * Method testCompletelyContainsLine is testing completelyContainsLine
   * @see org.shotdraw.util.Bounds#completelyContainsLine(double, double, double, double)
   */
  def testCompletelyContainsLine {
  }

  /**
   * Method testIsCompletelyInside is testing isCompletelyInside
   * @see org.shotdraw.util.Bounds#isCompletelyInside(org.shotdraw.util.Bounds)
   */
  def testIsCompletelyInside {
  }

  /**
   * Method testCropLine is testing cropLine
   * @see org.shotdraw.util.Bounds#cropLine(double, double, double, double)
   */
  def testCropLine {
  }

  /**
   * Method testEquals is testing equals
   * @see org.shotdraw.util.Bounds#equals(java.lang.Object)
   */
  def testEquals {
  }

  /**
   * Method testHashCode is testing hashCode
   * @see org.shotdraw.util.Bounds#hashCode()
   */
  def testHashCode {
  }

  /**
   * Method testToString is testing toString
   * @see org.shotdraw.util.Bounds#toString()
   */
  def testToString {
  }

  /**
   * JUnitDoclet moves marker to this method, if there is not match
   * for them in the regenerated code and if the marker is not empty.
   * This way, no test gets lost when regenerating after renaming.
   * <b>Method testVault is supposed to be empty.</b>
   */
  def testVault {
  }

  private var bounds: Bounds = null
}

