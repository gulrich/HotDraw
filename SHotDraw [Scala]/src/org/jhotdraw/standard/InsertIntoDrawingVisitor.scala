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
package org.jhotdraw.standard

import org.jhotdraw.framework.Drawing
import org.jhotdraw.framework.Figure
import org.jhotdraw.framework.FigureChangeListener
import org.jhotdraw.framework.FigureEnumeration
import org.jhotdraw.framework.FigureVisitor
import org.jhotdraw.framework.Handle

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
      val addedFigure: Figure = getDrawing.add(hostFigure)
      myInsertedFigures += addedFigure
    }
  }

  def visitHandle(hostHandle: Handle) {}

  def visitFigureChangeListener(hostFigureChangeListener: FigureChangeListener) {}

  def getInsertedFigures: FigureEnumeration = new FigureEnumerator(myInsertedFigures)

  private var myInsertedFigures: Set[Figure] = Set()
}

