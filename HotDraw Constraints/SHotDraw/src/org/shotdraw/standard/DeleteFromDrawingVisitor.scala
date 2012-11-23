/*
 * @(#)DeleteFromDrawingVisitor.java
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
class DeleteFromDrawingVisitor(newDrawing: Drawing) extends FigureVisitor {
  private var myDeletedFigures = Set[Figure]()
  private var myDrawing = newDrawing

  private def setDrawing(newDrawing: Drawing) {
    myDrawing = newDrawing
  }

  protected def getDrawing: Drawing = myDrawing

  def visitFigure(hostFigure: Figure) {
    if (!myDeletedFigures.contains(hostFigure) && getDrawing.containsFigure(hostFigure)) {
      val orphanedFigure = getDrawing.orphan(hostFigure)
      myDeletedFigures += orphanedFigure
    }
  }

  def visitHandle(hostHandle: Handle) {}

  def visitFigureChangeListener(hostFigureChangeListener: FigureChangeListener) {}

  def getDeletedFigures: Seq[Figure] = myDeletedFigures.foldLeft(List[Figure]())((x,y) => y::x)  
}

