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
    val suite: TestSuite = new TestSuite("Test for org.shotdraw.test.standard")
    suite.addTest(new TestSuite(classOf[ChangeConnectionEndHandleTest]))
    suite.addTest(new TestSuite(classOf[ChangeConnectionStartHandleTest]))
    suite.addTest(new TestSuite(classOf[ChopBoxConnectorTest]))
    suite.addTest(new TestSuite(classOf[NullHandleTest]))
    suite.addTest(new TestSuite(classOf[OffsetLocatorTest]))
    suite.addTest(new TestSuite(classOf[RelativeLocatorTest]))
    suite.addTest(new TestSuite(classOf[SimpleUpdateStrategyTest]))
    suite.addTest(new TestSuite(classOf[StandardDrawingTest]))
    suite
  }
}


