/*
 * @(#)JHotDrawRuntimeException.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.framework

/**
 * A JHotDraw RuntimeException.
 *
 * @version <$CURRENT_VERSION$>
 */
class JHotDrawRuntimeException(msg: String) extends RuntimeException(msg) {

  def this(nestedException: Exception) {
    this(nestedException.getLocalizedMessage)
    setNestedException(nestedException)
    nestedException.fillInStackTrace
  }

  protected def setNestedException(newNestedException: Exception) {
    myNestedException = newNestedException
  }

  def getNestedException: Exception = {
    return myNestedException
  }

  private var myNestedException: Exception = null
}

