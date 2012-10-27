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
package org.jhotdraw.test.util

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
    val suite: TestSuite = new TestSuite("Test for org.jhotdraw.test.util")
    suite.addTest(new TestSuite(classOf[BoundsTest]))
    suite.addTest(new TestSuite(classOf[ClipboardTest]))
    suite.addTest(new TestSuite(classOf[CommandMenuTest]))
    suite.addTest(new TestSuite(classOf[FloatingTextFieldTest]))
    suite.addTest(new TestSuite(classOf[SerializationStorageFormatTest]))
    suite.addTest(new TestSuite(classOf[StandardStorageFormatTest]))
    suite.addTest(new TestSuite(classOf[StorableInputTest]))
    suite.addTest(new TestSuite(classOf[StorableOutputTest]))
    suite.addTest(new TestSuite(classOf[StorageFormatManagerTest]))
    suite.addTest(new TestSuite(classOf[UndoManagerTest]))
    suite
  }
}


