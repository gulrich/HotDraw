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
package org.jhotdraw.test.standard;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:mtnygard@charter.net">Michael T. Nygard</a>
 * @version $Revision: 1.3 $
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.jhotdraw.test.standard");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(BoxHandleKitTest.class));
		suite.addTest(new TestSuite(ChangeConnectionEndHandleTest.class));
		suite.addTest(new TestSuite(ChangeConnectionStartHandleTest.class));
		suite.addTest(new TestSuite(ChopBoxConnectorTest.class));
		suite.addTest(new TestSuite(FigureEnumeratorTest.class));
		suite.addTest(new TestSuite(NullHandleTest.class));
		suite.addTest(new TestSuite(OffsetLocatorTest.class));
		suite.addTest(new TestSuite(RelativeLocatorTest.class));
		suite.addTest(new TestSuite(ReverseFigureEnumeratorTest.class));
		suite.addTest(new TestSuite(SimpleUpdateStrategyTest.class));
		suite.addTest(new TestSuite(SingleFigureEnumeratorTest.class));
		suite.addTest(new TestSuite(StandardDrawingTest.class));
		suite.addTest(new TestSuite(StandardFigureSelectionTest.class));
		//$JUnit-END$
		return suite;
	}
}
