/*
 * @(#)EllipseFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	��� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.io.IOException
import org.shotdraw.util._
import org.shotdraw.framework._
import org.shotdraw.standard._
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Point
import java.awt.Graphics
import ch.epfl.lamp.cassowary.SimplexSolver

/**
 * An ellipse figure.
 *
 * @version <$CURRENT_VERSION$>
 */
class EllipseFigure(origin: Point, corner: Point, solver: SimplexSolver) extends RectangularFigure(origin, corner, solver) {
  def this(solver: SimplexSolver) {
    this(new Point(0, 0), new Point(0, 0), solver)
  }

  override def drawBackground(g: Graphics) {
    val r = displayBox
    g.fillOval(r.x, r.y, r.width, r.height)
  }

  override def drawFrame(g: Graphics) {
    val r = displayBox
    g.drawOval(r.x, r.y, r.width - 1, r.height - 1)
  }  

  override def connectionInsets: Insets = {
    val r = displayBox
    val cx = r.width / 2
    val cy = r.height / 2
    new Insets(cy, cx, cy, cx)
  }
  override def newFigure(origin: Point, corner: Point, solver: SimplexSolver) = new EllipseFigure(origin, corner, solver)

  override def connectorAt(x: Int, y: Int): Connector = new ChopEllipseConnector(this)
  
  override def toString: String = "Ellipse"
}

