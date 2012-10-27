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
package org.jhotdraw.standard

import java.awt.Cursor

/**
 * Default implementation of the {@link org.jhotdraw.framework.Cursor} interface
 * for AWT/Swing.
 *
 * <p>Created on: 08/05/2003.</p>
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:ricardo_padilha@users.sourceforge.net">Ricardo 
 *         Sangoi Padilha</a>
 * @see org.jhotdraw.framework.Cursor
 */
object AWTCursor {  
  private final val serialVersionUID: Long = 1212601282967475415L
}

class AWTCursor(tpe: Int) extends Cursor(tpe) with org.jhotdraw.framework.Cursor {}

//class AWTCursor(newName: String) extends Cursor(newName) with org.jhotdraw.framework.Cursor {}

