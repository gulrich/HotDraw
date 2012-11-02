/*
 * @(#)CopyCommand.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.framework.DrawingEditor

/**
 * Copy the selection to the clipboard.
 *
 * @see Clipboard
 *
 * @version <$CURRENT_VERSION$>
 */
class CopyCommand(name: String, newDrawingEditor: DrawingEditor) extends FigureTransferCommand(name, newDrawingEditor) {

  override def execute {
    super.execute
    copyFigures(view.selection, view.selectionCount)
  }

  protected override def isExecutableWithView: Boolean = view.selectionCount > 0
}

