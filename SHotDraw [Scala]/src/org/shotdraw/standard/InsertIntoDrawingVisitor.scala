/*
 * @(#)InsertIntoDrawingVisitor.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.framework.Drawing
import org.shotdraw.framework.Figure
import org.shotdraw.framework.FigureChangeListener
import org.shotdraw.framework.FigureVisitor
import org.shotdraw.framework.Handle

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class InsertIntoDrawingVisitor(var myDrawing: Drawing) extends FigureVisitor {

  private def setDrawing(newDrawing: Drawing) {
    myDrawing = newDrawing
  }

  protected def getDrawing: Drawing = myDrawing

  def visitFigure(hostFigure: Figure) {
    if (!myInsertedFigures.contains(hostFigure) && !getDrawing.includes(hostFigure)) {
      val addedFigure = getDrawing.add(hostFigure)
      myInsertedFigures += addedFigure
    }
  }

  def visitHandle(hostHandle: Handle) {}

  def visitFigureChangeListener(hostFigureChangeListener: FigureChangeListener) {}

  def getInsertedFigures: Seq[Figure] = myInsertedFigures.foldLeft(List[Figure]())((x,y) => y::x)

  private var myInsertedFigures: Set[Figure] = Set()
}

