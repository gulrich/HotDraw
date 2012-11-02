/*
 * @(#)AbstractLocator.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.util._
import org.shotdraw.framework._
import java.io.IOException
import java.lang.Object

/**
 * AbstractLocator provides default implementations for
 * the Locator interface.
 *
 * @see Locator
 * @see Handle
 *
 * @version <$CURRENT_VERSION$>
 */
object AbstractLocator {
  private final val serialVersionUID: Long = -7742023180844048409L
}

abstract class AbstractLocator extends Locator with Storable with Cloneable {

  override def clone: java.lang.Object = {
    try {
      super.clone
    } catch {
      case e: CloneNotSupportedException => {
        throw new InternalError
      }
    }
  }

  /**
   * Stores the arrow tip to a StorableOutput.
   */
  def write(dw: StorableOutput) {}

  /**
   * Reads the arrow tip from a StorableInput.
   */
  def read(dr: StorableInput) {}
}



