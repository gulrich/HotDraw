/*
 * @(#)FloatingTextField.java
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
import java.awt._
import java.awt.event._

/**
 * A text field overlay that is used to edit a TextFigure.
 * A FloatingTextField requires a two step initialization:
 * In a first step the overlay is created and in a
 * second step it can be positioned.
 *
 * @see org.shotdraw.figures.TextFigure
 *
 * @version <$CURRENT_VERSION$>
 */
class FloatingTextField {

  /**
   * Creates the overlay for the given Component.
   */
  def createOverlay(container: Container) {
    createOverlay(container, null)
  }

  /**
   * Creates the overlay for the given Container using a
   * specific font.
   */
  def createOverlay(container: Container, font: Font) {
    container.add(fEditWidget, 0)
    if (font != null) {
      fEditWidget.setFont(font)
    }
    fContainer = container
  }

  /**
   * Adds an action listener
   */
  def addActionListener(listener: ActionListener) {
    fEditWidget.addActionListener(listener)
  }

  /**
   * Remove an action listener
   */
  def removeActionListener(listener: ActionListener) {
    fEditWidget.removeActionListener(listener)
  }

  /**
   * Positions the overlay.
   */
  def setBounds(r: Rectangle, text: String) {
    fEditWidget.setText(text)
    fEditWidget.setLocation(r.x, r.y)
    fEditWidget.setSize(r.width, r.height)
    fEditWidget.setVisible(true)
    fEditWidget.selectAll
    fEditWidget.requestFocus
  }

  /**
   * Gets the text contents of the overlay.
   */
  def getText: String = {
    return fEditWidget.getText
  }

  /**
   * Gets the preferred size of the overlay.
   */
  def getPreferredSize(cols: Int): Dimension = {
    fEditWidget.setColumns(cols)
    fEditWidget.getPreferredSize
  }

  /**
   * Removes the overlay.
   */
  def endOverlay {
    fContainer.requestFocus
    if (fEditWidget != null) {
      fEditWidget.setVisible(false)
      fContainer.remove(fEditWidget)
      val bounds: Rectangle = fEditWidget.getBounds
      fContainer.repaint(bounds.x, bounds.y, bounds.width, bounds.height)
    }
  }

  private var fEditWidget = new JTextField(20)
  private var fContainer: Container = null
}


