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

import org.shotdraw.util.SerializationStorageFormat
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
 * TestCase SerializationStorageFormatTest is generated by
 * JUnitDoclet to hold the tests for SerializationStorageFormat.
 * @see org.shotdraw.util.SerializationStorageFormat
 */
class SerializationStorageFormatTest(name: String) extends TestCase(name) {

  /**
   * Factory method for instances of the class to be tested.
   */
  def createInstance: SerializationStorageFormat = {
    new SerializationStorageFormat
  }

  /**
   * Method setUp is overwriting the framework method to
   * prepare an instance of this TestCase for a single test.
   * It's called from the JUnit framework only.
   */
  override protected def setUp {
    super.setUp
    serializationstorageformat = createInstance
  }

  /**
   * Method tearDown is overwriting the framework method to
   * clean up after each single test of this TestCase.
   * It's called from the JUnit framework only.
   */
  override protected def tearDown {
    serializationstorageformat = null
    super.tearDown
  }

  /**
   * Method testCreateFileDescription is testing createFileDescription
   * @see org.shotdraw.util.SerializationStorageFormat#createFileDescription()
   */
  def testCreateFileDescription {
  }

  /**
   * Method testStore is testing store
   * @see org.shotdraw.util.SerializationStorageFormat#store(java.lang.String, org.shotdraw.framework.Drawing)
   */
  def testStore {
  }

  /**
   * Method testRestore is testing restore
   * @see org.shotdraw.util.SerializationStorageFormat#restore(java.lang.String)
   */
  def testRestore {
  }

  /**
   * JUnitDoclet moves marker to this method, if there is not match
   * for them in the regenerated code and if the marker is not empty.
   * This way, no test gets lost when regenerating after renaming.
   * <b>Method testVault is supposed to be empty.</b>
   */
  def testVault {
  }

  private var serializationstorageformat: SerializationStorageFormat = null
}
