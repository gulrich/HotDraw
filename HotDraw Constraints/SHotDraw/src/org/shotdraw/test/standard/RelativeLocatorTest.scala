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
package org.shotdraw.test.standard

import org.shotdraw.standard.RelativeLocator
import junit.framework.TestCase

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
 * TestCase RelativeLocatorTest is generated by
 * JUnitDoclet to hold the tests for RelativeLocator.
 * @see org.shotdraw.standard.RelativeLocator
 */
class RelativeLocatorTest(name: String) extends TestCase(name) {

  /**
   * Factory method for instances of the class to be tested.
   */
  def createInstance: RelativeLocator = {
    new RelativeLocator
  }

  /**
   * Method setUp is overwriting the framework method to
   * prepare an instance of this TestCase for a single test.
   * It's called from the JUnit framework only.
   */
  override protected def setUp() {
    super.setUp
    relativelocator = createInstance
  }

  /**
   * Method tearDown is overwriting the framework method to
   * clean up after each single test of this TestCase.
   * It's called from the JUnit framework only.
   */
  override protected def tearDown() {
    relativelocator = null
    super.tearDown
  }

  /**
   * Method testEquals is testing equals
   * @see org.shotdraw.standard.RelativeLocator#equals(java.lang.Object)
   */
  def testEquals() {
  }

  /**
   * Method testLocate is testing locate
   * @see org.shotdraw.standard.RelativeLocator#locate(org.shotdraw.framework.Figure)
   */
  def testLocate() {
  }

  /**
   * Method testWrite is testing write
   * @see org.shotdraw.standard.RelativeLocator#write(org.shotdraw.util.StorableOutput)
   */
  def testWrite() {
  }

  /**
   * Method testRead is testing read
   * @see org.shotdraw.standard.RelativeLocator#read(org.shotdraw.util.StorableInput)
   */
  def testRead() {
  }

  /**
   * Method testEast is testing east
   * @see org.shotdraw.standard.RelativeLocator#east()
   */
  def testEast() {
  }

  /**
   * Method testNorth is testing north
   * @see org.shotdraw.standard.RelativeLocator#north()
   */
  def testNorth() {
  }

  /**
   * Method testWest is testing west
   * @see org.shotdraw.standard.RelativeLocator#west()
   */
  def testWest() {
  }

  /**
   * Method testNorthEast is testing northEast
   * @see org.shotdraw.standard.RelativeLocator#northEast()
   */
  def testNorthEast() {
  }

  /**
   * Method testNorthWest is testing northWest
   * @see org.shotdraw.standard.RelativeLocator#northWest()
   */
  def testNorthWest() {
  }

  /**
   * Method testSouth is testing south
   * @see org.shotdraw.standard.RelativeLocator#south()
   */
  def testSouth() {
  }

  /**
   * Method testSouthEast is testing southEast
   * @see org.shotdraw.standard.RelativeLocator#southEast()
   */
  def testSouthEast() {
  }

  /**
   * Method testSouthWest is testing southWest
   * @see org.shotdraw.standard.RelativeLocator#southWest()
   */
  def testSouthWest() {
  }

  /**
   * Method testCenter is testing center
   * @see org.shotdraw.standard.RelativeLocator#center()
   */
  def testCenter() {
  }

  /**
   * JUnitDoclet moves marker to this method, if there is not match
   * for them in the regenerated code and if the marker is not empty.
   * This way, no test gets lost when regenerating after renaming.
   * <b>Method testVault is supposed to be empty.</b>
   */
  def testVault() {
  }

  private var relativelocator: RelativeLocator = null
}
