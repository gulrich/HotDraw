/*
 * @(#)SelectAreaTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import java.awt._
import java.awt.event.MouseEvent
import org.shotdraw.framework._

/**
 * SelectAreaTracker implements a rubberband selection of an area.
 *
 * @version <$CURRENT_VERSION$>
 */
class SelectAreaTracker(newDrawingEditor: DrawingEditor, fRubberBandColor: Color) extends AbstractTool(newDrawingEditor) {
  /** Selected rectangle in physical coordinates space */
  private var fSelectGroup: Rectangle = null
  
  def this(newDrawingEditor: DrawingEditor) {
    this(newDrawingEditor, Color.black)
  }

  override def mouseDown(e: MouseEvent, x: Int, y: Int) {
    super.mouseDown(e, e.getX, e.getY)
    rubberBand(getAnchorX, getAnchorY, getAnchorX, getAnchorY)
  }

  override def mouseDrag(e: MouseEvent, x: Int, y: Int) {
    super.mouseDrag(e, x, y)
    eraseRubberBand()
    rubberBand(getAnchorX, getAnchorY, x, y)
  }

  override def mouseUp(e: MouseEvent, x: Int, y: Int) {
    eraseRubberBand()
    selectGroup(e.isShiftDown)
    super.mouseUp(e, x, y)
  }

  private def rubberBand(x1: Int, y1: Int, x2: Int, y2: Int) {
    fSelectGroup = new Rectangle(new Point(x1, y1))
    fSelectGroup.add(new Point(x2, y2))
    drawXORRect(fSelectGroup)
  }

  private def eraseRubberBand() {
    drawXORRect(fSelectGroup)
  }

  private def drawXORRect(r: Rectangle) {
    val g = view.getGraphics
    if (g != null) {
      try {
        g match {
          case g2d: Graphics2D =>
            val dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, Array[Float](5f, 5f, 5f, 5f), 5.0f)
            g2d.setStroke(dashedStroke)
          case _ =>
        }
        g.setXORMode(view.getBackground)
        g.setColor(fRubberBandColor)
        g.drawRect(r.x, r.y, r.width, r.height)
      } finally {
        g.dispose()
      }
    }
  }

  private def selectGroup(toggle: Boolean) {
    drawing.figuresReverse foreach { f =>
      val r2 = f.displayBox
      if (fSelectGroup.contains(r2.x, r2.y) && fSelectGroup.contains(r2.x + r2.width, r2.y + r2.height)) {
        if (toggle) {
          view.toggleSelection(f)
        } else {
          view.addToSelection(f)
        }
      }
    }
  }

}

