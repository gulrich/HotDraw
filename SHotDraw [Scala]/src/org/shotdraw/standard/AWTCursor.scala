/*
 * @(#)AWTCursor.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.Cursor

/**
 * Default implementation of the {@link org.shotdraw.framework.Cursor} interface
 * for AWT/Swing.
 *
 * <p>Created on: 08/05/2003.</p>
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:ricardo_padilha@users.sourceforge.net">Ricardo 
 *         Sangoi Padilha</a>
 * @see org.shotdraw.framework.Cursor
 */
class AWTCursor(tpe: Int) extends Cursor(tpe) with org.shotdraw.framework.Cursor {}