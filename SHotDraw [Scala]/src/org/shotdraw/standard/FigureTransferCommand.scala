/*
 * @(#)FigureTransferCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.util._
import org.shotdraw.framework._

/**
 * Common base clase for commands that transfer figures
 * between a drawing and the clipboard.
 *
 * @version <$CURRENT_VERSION$>
 */
class FigureTransferCommand(name: String, newDrawingEditor: DrawingEditor) extends AbstractCommand(name, newDrawingEditor) {

  /**
   * Deletes the selection from the drawing.
   */
  private[standard] def deleteFigures(fe: Seq[Figure]) {
    val deleteVisitor = new DeleteFromDrawingVisitor(view.drawing)
    fe foreach {_.visit(deleteVisitor)}
    view.clearSelection
  }

  /**
   * Copies the Seq[Figure] to the clipboard.
   */
  private[standard] def copyFigures(fe: Seq[Figure], figureCount: Int) {
    Clipboard.getClipboard.setContents(new StandardFigureSelection(fe, figureCount))
  }

  /**
   * Inserts an enumeration of figures and translates them by the
   * given offset.
   * @todo mrfloppy to investigate making this protected.  Looks like it would
   *       be no problem to me.  It was package scope.  I thought it safer to
   *       make it less restrictive just incase their was a reason for the
   *       package scope I didn't know about. dnoyeb.
   *       Bug - [ 673096 ] FigureTransferCommand has a wrong method
   */
  def insertFigures(fe: Seq[Figure], dx: Int, dy: Int): Seq[Figure] = view.insertFigures(fe, dx, dy, false)
}



