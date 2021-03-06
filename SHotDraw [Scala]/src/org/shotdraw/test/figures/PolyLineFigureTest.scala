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
package org.shotdraw.test.figures

import junit.framework.TestCase
import org.shotdraw.figures.ArrowTip
import org.shotdraw.figures.PolyLineFigure
import org.shotdraw.figures.LineDecoration

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
 * TestCase PolyLineFigureTest is generated by
 * JUnitDoclet to hold the tests for PolyLineFigure.
 * @see org.shotdraw.figures.PolyLineFigure
 */
class PolyLineFigureTest(name: String) extends TestCase(name) {

  /**
   * Factory method for instances of the class to be tested.
   */
  def createInstance: PolyLineFigure = {
    new PolyLineFigure
  }

  /**
   * Method setUp is overwriting the framework method to
   * prepare an instance of this TestCase for a single test.
   * It's called from the JUnit framework only.
   */
  override protected def setUp() {
    super.setUp
    polylinefigure = createInstance
  }

  /**
   * Method tearDown is overwriting the framework method to
   * clean up after each single test of this TestCase.
   * It's called from the JUnit framework only.
   */
  override protected def tearDown() {
    polylinefigure = null
    super.tearDown
  }

  /**
   * Method testDisplayBox is testing displayBox
   * @see org.shotdraw.figures.PolyLineFigure#displayBox()
   */
  def testDisplayBox() {
  }

  /**
   * Method testIsEmpty is testing isEmpty
   * @see org.shotdraw.figures.PolyLineFigure#isEmpty()
   */
  def testIsEmpty() {
  }

  /**
   * Method testHandles is testing handles
   * @see org.shotdraw.figures.PolyLineFigure#handles()
   */
  def testHandles() {
  }

  /**
   * Method testBasicDisplayBox is testing basicDisplayBox
   * @see org.shotdraw.figures.PolyLineFigure#basicDisplayBox(java.awt.Point, java.awt.Point)
   */
  def testBasicDisplayBox() {
  }

  /**
   * Method testAddPoint is testing addPoint
   * @see org.shotdraw.figures.PolyLineFigure#addPoint(int, int)
   */
  def testAddPoint() {
  }

  /**
   * Method testPoints is testing points
   * @see org.shotdraw.figures.PolyLineFigure#points()
   */
  def testPoints() {
  }

  /**
   * Method testPointCount is testing pointCount
   * @see org.shotdraw.figures.PolyLineFigure#pointCount()
   */
  def testPointCount() {
  }

  /**
   * Method testSetPointAt is testing setPointAt
   * @see org.shotdraw.figures.PolyLineFigure#setPointAt(java.awt.Point, int)
   */
  def testSetPointAt() {
  }

  /**
   * Method testInsertPointAt is testing insertPointAt
   * @see org.shotdraw.figures.PolyLineFigure#insertPointAt(java.awt.Point, int)
   */
  def testInsertPointAt() {
  }

  /**
   * Method testRemovePointAt is testing removePointAt
   * @see org.shotdraw.figures.PolyLineFigure#removePointAt(int)
   */
  def testRemovePointAt() {
  }

  /**
   * Method testSplitSegment is testing splitSegment
   * @see org.shotdraw.figures.PolyLineFigure#splitSegment(int, int)
   */
  def testSplitSegment() {
  }

  /**
   * Method testPointAt is testing pointAt
   * @see org.shotdraw.figures.PolyLineFigure#pointAt(int)
   */
  def testPointAt() {
  }

  /**
   * Method testJoinSegments is testing joinSegments
   * @see org.shotdraw.figures.PolyLineFigure#joinSegments(int, int)
   */
  def testJoinSegments() {
  }

  /**
   * Method testConnectorAt is testing connectorAt
   * @see org.shotdraw.figures.PolyLineFigure#connectorAt(int, int)
   */
  def testConnectorAt() {
  }

  /**
   * Method testSetGetStartDecoration is testing setStartDecoration
   * and getStartDecoration together by setting some value
   * and verifying it by reading.
   * @see org.shotdraw.figures.PolyLineFigure#setStartDecoration(org.shotdraw.figures.LineDecoration)
   * @see org.shotdraw.figures.PolyLineFigure#getStartDecoration()
   */
  def testSetGetStartDecoration() {
    val tests = List(new ArrowTip, null)
    tests foreach { e => 
      polylinefigure.setStartDecoration(e)
      assert(e == polylinefigure.getStartDecoration)  
    }
  }

  /**
   * Method testSetGetEndDecoration is testing setEndDecoration
   * and getEndDecoration together by setting some value
   * and verifying it by reading.
   * @see org.shotdraw.figures.PolyLineFigure#setEndDecoration(org.shotdraw.figures.LineDecoration)
   * @see org.shotdraw.figures.PolyLineFigure#getEndDecoration()
   */
  def testSetGetEndDecoration() {
    val tests = List(new ArrowTip, null)
    tests foreach { e => 
      polylinefigure.setEndDecoration(e)
      assert(e == polylinefigure.getEndDecoration)  
    }
  }

  /**
   * Method testDraw is testing draw
   * @see org.shotdraw.figures.PolyLineFigure#draw(java.awt.Graphics)
   */
  def testDraw() {
  }

  /**
   * Method testContainsPoint is testing containsPoint
   * @see org.shotdraw.figures.PolyLineFigure#containsPoint(int, int)
   */
  def testContainsPoint() {
  }

  /**
   * Method testFindSegment is testing findSegment
   * @see org.shotdraw.figures.PolyLineFigure#findSegment(int, int)
   */
  def testFindSegment() {
  }

  /**
   * Method testGetAttribute is testing getAttribute
   * @see org.shotdraw.figures.PolyLineFigure#getAttribute(java.lang.String)
   */
  def testGetAttribute() {
  }

  /**
   * Method testSetAttribute is testing setAttribute
   * @see org.shotdraw.figures.PolyLineFigure#setAttribute(java.lang.String, java.lang.Object)
   */
  def testSetAttribute() {
  }

  /**
   * Method testWrite is testing write
   * @see org.shotdraw.figures.PolyLineFigure#write(org.shotdraw.util.StorableOutput)
   */
  def testWrite() {
  }

  /**
   * Method testRead is testing read
   * @see org.shotdraw.figures.PolyLineFigure#read(org.shotdraw.util.StorableInput)
   */
  def testRead() {
  }

  /**
   * Method testLocator is testing locator
   * @see org.shotdraw.figures.PolyLineFigure#locator(int)
   */
  def testLocator() {
  }

  /**
   * JUnitDoclet moves marker to this method, if there is not match
   * for them in the regenerated code and if the marker is not empty.
   * This way, no test gets lost when regenerating after renaming.
   * <b>Method testVault is supposed to be empty.</b>
   */
  def testVault() {
  }

  private var polylinefigure: PolyLineFigure = null
}

