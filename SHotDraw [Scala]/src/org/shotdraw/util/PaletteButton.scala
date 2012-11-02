/*
 * @(#)PaletteButton.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import javax.swing._
import java.awt.event._
import java.lang.Object

/**
 * A palette button is a three state button. The states are normal
 * pressed and selected. It uses to the palette listener interface
 * to notify about state changes.
 *
 * @see PaletteListener
 * @see PaletteLayout
 *
 * @version <$CURRENT_VERSION$>
 */
object PaletteButton {
  private final val serialVersionUID: Long = -218921317221815794L
  protected final val NORMAL: Int = 1
  protected final val PRESSED: Int = 2
  protected final val SELECTED: Int = 3
}

abstract class PaletteButton extends JButton with MouseListener with MouseMotionListener {
  import PaletteButton._
  /**
   * Constructs a PaletteButton.
   * @param listener the listener to be notified.
   */
  def this(listener: PaletteListener) {
    this()
    fListener = listener
    fState = NORMAL
    fOldState = NORMAL
    addMouseListener(this)
    addMouseMotionListener(this)
  }

  def value: Any = null

  def name: String = ""

  def reset {
    if (isEnabled) {
      fState = NORMAL
      setSelected(false)
      repaint()
    }
  }

  def select {
    if (isEnabled) {
      fState = SELECTED
      setSelected(true)
      repaint()
    }
  }

  def mousePressed(e: MouseEvent) {
    if (isEnabled) {
      fOldState = fState
      fState = PRESSED
      repaint()
    }
  }

  def mouseDragged(e: MouseEvent) {
    if (isEnabled) {
      if (contains(e.getX, e.getY)) {
        fState = PRESSED
      }
      else {
        fState = fOldState
      }
      repaint()
    }
  }

  def mouseReleased(e: MouseEvent) {
    if (isEnabled) {
      fState = fOldState
      repaint()
      if (contains(e.getX, e.getY)) {
        fListener.paletteUserSelected(this)
      }
    }
  }

  def mouseMoved(e: MouseEvent) {
    fListener.paletteUserOver(this, true)
  }

  def mouseExited(e: MouseEvent) {
    if (fState == PRESSED) {
      mouseDragged(e)
    }
    fListener.paletteUserOver(this, false)
  }

  def mouseClicked(e: MouseEvent) {
  }

  def mouseEntered(e: MouseEvent) {
  }

  private var fState: Int = 0
  private var fOldState: Int = 0
  private var fListener: PaletteListener = null
}

