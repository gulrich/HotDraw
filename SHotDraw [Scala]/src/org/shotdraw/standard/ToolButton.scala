/*
 * @(#)ToolButton.java
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
import javax.swing._
import java.awt._
import java.util.EventObject


/**
 * A PaletteButton that is associated with a tool.
 *
 * @see Tool
 *
 * @version <$CURRENT_VERSION$>
 */
class ToolButton(listener: PaletteListener, iconName: String, myName: String, var myTool: Tool) extends PaletteButton(listener) with ToolListener {
  private var myIcon: PaletteIcon = null

  myTool.addToolListener(this)
  setEnabled(tool.isUsable)
  setName(myName)
  val kit = Iconkit.instance
  if (kit == null) {
    throw new JHotDrawRuntimeException("Iconkit instance isn't set")
  }
  val im = new Array[Image](3)
  im(0) = kit.loadImageResource(iconName + "1.gif")
  im(1) = kit.loadImageResource(iconName + "2.gif")
  im(2) = kit.loadImageResource(iconName + "3.gif")
  val tracker = new MediaTracker(this)
  for(i <- 0 to 2) {
    tracker.addImage(im(i),i)
  }
  try {
    tracker.waitForAll
  } catch {
    case e: Exception => {
    }
  }
  setPaletteIcon(new PaletteIcon(new Dimension(24, 24), im(0), im(1), im(2)))

  if (im(0)!= NoImage) setIcon(new ImageIcon(im(0)))
  if (im(1)!= NoImage) setIcon(new ImageIcon(im(1)))
  if (im(2)!= NoImage) setIcon(new ImageIcon(im(2)))
  setToolTipText(name)

  def tool: Tool = myTool

  override def name: String = getName

  def attributeValue: Any = tool

  override def getMinimumSize: Dimension = new Dimension(getPaletteIcon.getWidth, getPaletteIcon.getHeight)

  override def getPreferredSize: Dimension = new Dimension(getPaletteIcon.getWidth, getPaletteIcon.getHeight)

  override def getMaximumSize: Dimension = new Dimension(getPaletteIcon.getWidth, getPaletteIcon.getHeight)

  def paintSelected(g: Graphics) {
    if (getPaletteIcon.selected != null) {
      g.drawImage(getPaletteIcon.selected, 0, 0, this)
    }
  }

  override def paint(g: Graphics) {
    if (isSelected) {
      paintSelected(g)
    } else {
      super.paint(g)
    }
  }

  def toolUsable(toolEvent: EventObject) {
    setEnabled(true)
  }

  def toolUnusable(toolEvent: EventObject) {
    setEnabled(false)
    setSelected(false)
  }

  def toolActivated(toolEvent: EventObject) {}

  def toolDeactivated(toolEvent: EventObject) {}

  def toolEnabled(toolEvent: EventObject) {
    setEnabled(true)
  }

  def toolDisabled(toolEvent: EventObject) {
    setEnabled(false)
  }

  protected def getPaletteIcon: PaletteIcon = myIcon

  private def setPaletteIcon(newIcon: PaletteIcon) {
    myIcon = newIcon
  }

  private def setTool(newTool: Tool) {
    myTool = newTool
  }
}

