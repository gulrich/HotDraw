/*
 * @(#)FrameworkSuite
 *
 * Project:     JHotdraw - a GUI framework for technical drawings
 *              http://www.jhotdraw.org
 *              http://jhotdraw.sourceforge.net
 * Copyright:   � by the original author(s) and all contributors
 * License:     Lesser GNU Public License (LGPL)
 *              http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.test.framework

import junit.framework.TestSuite
import junit.framework.Test

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
 * TestSuite FrameworkSuite
 */
object FrameworkSuite {
  def suite: Test = {
    var suite: TestSuite = null
    suite = new TestSuite("org.jhotdraw.test.framework")
    suite.addTestSuite(classOf[FigureChangeEventTest])
    suite.addTestSuite(classOf[DrawingChangeEventTest])
    suite
  }

  /**
   * Method to execute the TestSuite from command line
   * using JUnit's textui.TestRunner .
   */
  def main(args: Array[String]) {
    junit.textui.TestRunner.run(suite)
  }
}


