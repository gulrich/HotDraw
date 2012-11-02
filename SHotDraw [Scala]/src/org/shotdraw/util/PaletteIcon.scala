/*
 * @(#)PaletteIcon.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import java.awt._

/**
 * A three state icon that can be used in Palettes.
 *
 * @see PaletteButton
 *
 * @version <$CURRENT_VERSION$>
 */
class PaletteIcon extends Object {
  def this(size: Dimension, normal: Image, pressed: Image, selected: Image) {
    this()
    fSize = size
    fNormal = normal
    fPressed = pressed
    fSelected = selected
  }

  def normal: Image = fNormal

  def pressed: Image = fPressed

  def selected: Image = fSelected

  def getWidth: Int = fSize.width
  
  def getHeight: Int = fSize.height

  private[util] var fNormal: Image = null
  private[util] var fPressed: Image = null
  private[util] var fSelected: Image = null
  private[util] var fSize: Dimension = null
}

