/*
 * @(#)TextHolder.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import java.awt._
import org.jhotdraw.framework._

/**
 * The interface of a figure that has some editable text contents.
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */
abstract trait TextHolder {
  def textDisplayBox: Rectangle

  /**
   * Gets the text shown by the text figure.
   */
  def getText: String

  /**
   * Sets the text shown by the text figure.
   */
  def setText(newText: String)

  /**
   * Tests whether the figure accepts typing.
   */
  def acceptsTyping: Boolean

  /**
   * Gets the number of columns to be overlaid when the figure is edited.
   */
  def overlayColumns: Int

  /**
   * Connects a text holder to another figure.
   */
  def connect(connectedFigure: Figure)

  /**
   * Disconnects a text holder from a connect figure.
   */
  def disconnect(disconnectFigure: Figure)

  /**
   * Gets the font.
   */
  def getFont: Font

  /**
   * Usually, a TextHolders is implemented by a Figure subclass. To avoid casting
   * a TextHolder to a Figure this method can be used for polymorphism (in this
   * case, let the (same) object appear to be of another type).
   * Note, that the figure returned is not the figure to which the TextHolder is
   * (and its representing figure) connected.
   * @return figure responsible for representing the content of this TextHolder
   */
  def getRepresentingFigure: Figure
}

