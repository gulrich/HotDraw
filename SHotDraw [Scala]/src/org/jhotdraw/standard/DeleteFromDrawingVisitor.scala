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
package org.jhotdraw.standard

import org.jhotdraw.framework.Drawing
import org.jhotdraw.framework.Figure
import org.jhotdraw.framework.FigureChangeListener
import org.jhotdraw.framework.FigureVisitor
import org.jhotdraw.framework.Handle

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class DeleteFromDrawingVisitor(newDrawing: Drawing) extends FigureVisitor {
  private var myDeletedFigures: Set[Figure] = Set[Figure]()
  private var myDrawing: Drawing = newDrawing

  private def setDrawing(newDrawing: Drawing) {
    myDrawing = newDrawing
  }

  protected def getDrawing: Drawing = myDrawing

  def visitFigure(hostFigure: Figure) {
    if (!myDeletedFigures.contains(hostFigure) && getDrawing.containsFigure(hostFigure)) {
      val orphanedFigure: Figure = getDrawing.orphan(hostFigure)
      myDeletedFigures += orphanedFigure
    }
  }

  def visitHandle(hostHandle: Handle) {}

  def visitFigureChangeListener(hostFigureChangeListener: FigureChangeListener) {}

  def getDeletedFigures: Seq[Figure] = myDeletedFigures.foldLeft(List[Figure]())((x,y) => y::x)  
}

