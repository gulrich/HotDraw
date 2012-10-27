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
package org.jhotdraw.test

import junit.framework.Test
import junit.framework.TestSuite

/**
 * @author <a href="mailto:mtnygard@charter.net">Michael T. Nygard</a>
 * @version $Revision: 1.3 $
 */
object AllTests {
  def main(args: Array[String]) {
    junit.textui.TestRunner.run(suite)
  }

  def suite: Test = {
    val suite: TestSuite = new TestSuite("Test for org.jhotdraw.test")
    suite.addTest(org.jhotdraw.test.contrib.AllTests.suite)
    suite.addTest(org.jhotdraw.test.figures.AllTests.suite)
    suite.addTest(org.jhotdraw.test.framework.AllTests.suite)
    suite.addTest(org.jhotdraw.test.application.AllTests.suite)
    suite.addTest(org.jhotdraw.test.standard.AllTests.suite)
    suite.addTest(org.jhotdraw.test.util.AllTests.suite)
    suite
  }
}


