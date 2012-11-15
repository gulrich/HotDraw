/*
 * @(#)Layouter.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.contrib

import org.shotdraw.util.Storable
import java.io.Serializable
import java.awt._

/**
 * A Layouter encapsulates a algorithm to layout()
 * a figure. It is passed on to a figure which delegates the 
 * layout task to the Layouter's layout method. 
 * The Layouter might need access to some information 
 * specific to a certain figure in order to layout it out properly.
 *
 * Note: Currently, only the GraphicalCompositeFigure uses
 * such a Layouter to layout its child components.
 *
 * @see		GraphicalCompositeFigure
 *
 * @author	Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
trait Layouter extends Serializable with Storable {
  /**
   * Calculate the layout for the figure and all its
   * subelements. The layout is not actually performed but just
   * its dimensions are calculated.
   *
   * @param origin start point for the layout()
   * @param corner minimum corner point for the layout()
   * @return size including space for insets
   */
  def calculateLayout(origin: Point, corner: Point): Rectangle

  /**
   * Method which lays out a figure. It is called by the figure
   * if a layout task is to be performed. Implementing classes
   * specify a certain layout algorithm in this method.
   *
   * @param origin start point for the layout()
   * @param corner minimum corner point for the layout()
   */
  def layout(origin: Point, corner: Point): Rectangle

  /**
   * Set the insets for spacing between the figure and its subfigures
   *
   * @param newInsets new spacing dimensions
   */
  def setInsets(newInsets: Insets)

  /**
   * Get the insets for spacing between the figure and its subfigures
   *
   * @return spacing dimensions
   */
  def getInsets: Insets

  /**
   * Create a new instance of this type and sets the layoutable
   */
  def create(newLayoutable: Layoutable): Layouter
}

