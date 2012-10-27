/*
 * @(#)Clipboard.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import java.lang.Object

/**
 * A temporary replacement for a global clipboard.
 * It is a singleton that can be used to store and
 * get the contents of the clipboard.
 *
 * @version <$CURRENT_VERSION$>
 */
object Clipboard {
  /**
   * Gets the clipboard.
   */
  def getClipboard: Clipboard = fgClipboard

  protected var fgClipboard: Clipboard = new Clipboard
}

class Clipboard {
  /**
   * Sets the contents of the clipboard.
   */
  def setContents(contents: Any) {
    fContents = contents
  }

  /**
   * Gets the contents of the clipboard.
   */
  def getContents: Any = fContents

  private var fContents: Any = null
}

