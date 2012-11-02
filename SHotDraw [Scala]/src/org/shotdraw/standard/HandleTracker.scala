/*
 * @(#)HandleTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt.event.MouseEvent
import org.shotdraw.framework._

/**
 * HandleTracker implements interactions with the handles of a Figure.
 *
 * @see SelectionTool
 *
 * @version <$CURRENT_VERSION$>
 */
class HandleTracker(newDrawingEditor: DrawingEditor, fAnchorHandle: Handle) extends AbstractTool(newDrawingEditor) {

  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    super.mouseDown(e, x, y)
    fAnchorHandle.invokeStart(x, y, view)
  }

  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {
    super.mouseDrag(e, x, y)
    fAnchorHandle.invokeStep(x, y, getAnchorX, getAnchorY, view)
  }

  override def mouseUp(e: MouseEvent, x: Int, y: Int) {
    super.mouseUp(e, x, y)
    fAnchorHandle.invokeEnd(x, y, getAnchorX, getAnchorY, view)
  }

  override def activate {}
}

