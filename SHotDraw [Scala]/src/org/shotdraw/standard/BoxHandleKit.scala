/*
 * @(#)BoxHandleKit.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.standard

import org.shotdraw.framework._
import org.shotdraw.util.Undoable
import org.shotdraw.util.UndoableAdapter
import java.awt.Rectangle
import java.awt.Point

/**
 * A set of utility methods to create Handles for the common
 * locations on a figure's display box.
 *
 * @see Handle
 *
 * @version <$CURRENT_VERSION$>
 */
object BoxHandleKit {
  /**
   * Fills the given collection with handles at each corner of a
   * figure.
   */
  def addCornerHandles(f: Figure, handles: List[Handle]): List[Handle] = handles ::: List(southEast(f), southWest(f), northEast(f), northWest(f))

  /**
   * Fills the given collection with handles at each corner
   * and the north, south, east, and west of the figure.
   */
  def addHandles(f: Figure, handles: List[Handle]): List[Handle] = addCornerHandles(f, handles) ::: List(south(f), north(f), east(f), west(f))

  def south(owner: Figure): Handle = new SouthHandle(owner)

  def southEast(owner: Figure): Handle = new SouthEastHandle(owner)

  def southWest(owner: Figure): Handle = new SouthWestHandle(owner)

  def north(owner: Figure): Handle = new NorthHandle(owner)

  def northEast(owner: Figure): Handle = new NorthEastHandle(owner)

  def northWest(owner: Figure): Handle = new NorthWestHandle(owner)

  def east(owner: Figure): Handle = new EastHandle(owner)

  def west(owner: Figure): Handle = new WestHandle(owner)
}

object ResizeHandle {

  class UndoActivity(newView: DrawingView) extends UndoableAdapter(newView) {
    setUndoable(true)
    setRedoable(true)

    override def undo: Boolean = super.undo && resetDisplayBox


    override def redo: Boolean = isRedoable && resetDisplayBox


    private def resetDisplayBox: Boolean = {
      val fe: Iterator[Figure] = getAffectedFigures.iterator
      if (!fe.hasNext) {
        return false
      }
      val currentFigure: Figure = fe.next
      val figureDisplayBox: Rectangle = currentFigure.displayBox
      currentFigure.displayBox(getOldDisplayBox)
      setOldDisplayBox(figureDisplayBox)
      true
    }

    private[standard] def setOldDisplayBox(newOldDisplayBox: Rectangle) {
      myOldDisplayBox = newOldDisplayBox
    }

    def getOldDisplayBox: Rectangle = myOldDisplayBox

    private var myOldDisplayBox: Rectangle = null
  }
}

class ResizeHandle(owner: Figure, loc: Locator) extends LocatorHandle(owner, loc) {

  override def invokeStart(x: Int, y: Int, view: DrawingView) {
    setUndoActivity(createUndoActivity(view))
    getUndoActivity.setAffectedFigures(Seq[Figure](owner))
    (getUndoActivity.asInstanceOf[ResizeHandle.UndoActivity]).setOldDisplayBox(owner.displayBox)
  }

  override def invokeEnd(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val oldDisplayBox: Rectangle = (getUndoActivity.asInstanceOf[ResizeHandle.UndoActivity]).getOldDisplayBox
    if (owner.displayBox == oldDisplayBox) {
      setUndoActivity(null)
    }
  }

  /**
   * Factory method for undo activity. To be overriden by subclasses.
   */
  private[standard] def createUndoActivity(view: DrawingView): Undoable = new ResizeHandle.UndoActivity(view)
}

class NorthEastHandle(owner: Figure) extends ResizeHandle(owner, RelativeLocator.northEast) {
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val r: Rectangle = owner.displayBox
    owner.displayBox(new Point(r.x, Math.min(r.y + r.height, y)), new Point(Math.max(r.x, x), r.y + r.height))
  }
}

class EastHandle(owner: Figure) extends ResizeHandle(owner, RelativeLocator.east) {
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val r: Rectangle = owner.displayBox
    owner.displayBox(new Point(r.x, r.y), new Point(Math.max(r.x, x), r.y + r.height))
  }
}

class NorthHandle(owner: Figure) extends ResizeHandle(owner, RelativeLocator.north) {
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val r: Rectangle = owner.displayBox
    owner.displayBox(new Point(r.x, Math.min(r.y + r.height, y)), new Point(r.x + r.width, r.y + r.height))
  }
}

class NorthWestHandle(owner: Figure) extends ResizeHandle(owner, RelativeLocator.northWest) {
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val r: Rectangle = owner.displayBox
    owner.displayBox(new Point(Math.min(r.x + r.width, x), Math.min(r.y + r.height, y)), new Point(r.x + r.width, r.y + r.height))
  }
}

class SouthEastHandle(owner: Figure) extends ResizeHandle(owner, RelativeLocator.southEast) {
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val r: Rectangle = owner.displayBox
    owner.displayBox(new Point(r.x, r.y), new Point(Math.max(r.x, x), Math.max(r.y, y)))
  }
}

class SouthHandle(owner: Figure) extends ResizeHandle(owner, RelativeLocator.south) {
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val r: Rectangle = owner.displayBox
    owner.displayBox(new Point(r.x, r.y), new Point(r.x + r.width, Math.max(r.y, y)))
  }
}

class SouthWestHandle(owner: Figure) extends ResizeHandle(owner, RelativeLocator.southWest) {
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val r: Rectangle = owner.displayBox
    owner.displayBox(new Point(Math.min(r.x + r.width, x), r.y), new Point(r.x + r.width, Math.max(r.y, y)))
  }
}

class WestHandle(owner: Figure) extends ResizeHandle(owner, RelativeLocator.west) {
  override def invokeStep(x: Int, y: Int, anchorX: Int, anchorY: Int, view: DrawingView) {
    val r: Rectangle = owner.displayBox
    owner.displayBox(new Point(Math.min(r.x + r.width, x), r.y), new Point(r.x + r.width, r.y + r.height))
  }
}

