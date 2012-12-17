/*
 * @(#)RectangleFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	��� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.figures

import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import org.shotdraw.framework.Handle
import org.shotdraw.standard.AbstractFigure
import org.shotdraw.standard.BoxHandleKit
import org.shotdraw.util.StorableInput
import org.shotdraw.util.StorableOutput
import org.shotdraw.framework.CRectangle
import ch.epfl.lamp.cassowary.SimplexSolver
import ch.epfl.lamp.cassowary.Constraint

/**
 * A rectangle figure.
 *
 * @version <$CURRENT_VERSION$>
 */
class RectangleFigure(origin: Point, corner: Point, solver: SimplexSolver) extends RectangularFigure(origin, corner, solver: SimplexSolver) {
  def this(solver: SimplexSolver) {
    this(new Point(0, 0), new Point(0, 0), solver)
  }
  
  override def drawBackground(g: Graphics) {
    val r = displayBox
    g.fillRect(r.x, r.y, r.width, r.height)
  }

  override def drawFrame(g: Graphics) {
    val r = displayBox
    g.drawRect(r.x, r.y, r.width - 1, r.height - 1)
  }  
  
  override def newFigure(origin: Point, corner: Point, solver: SimplexSolver) = new RectangleFigure(origin, corner, solver)
}

