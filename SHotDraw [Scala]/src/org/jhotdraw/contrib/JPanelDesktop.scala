/*
 * @(#)JPanelDesktop.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.contrib

import javax.swing._
import java.awt.Component._
import org.jhotdraw.application._
import org.jhotdraw.framework.DrawingView
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Component

/**
 * @author  C.L.Gilbert <dnoyeb@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object JPanelDesktop {
  private final val serialVersionUID: Long = -8942268023126149074L
}

class JPanelDesktop extends JPanel with Desktop {
  def this(newDrawApplication: DrawApplication) {
    this()
    setDrawApplication(newDrawApplication)
    setDesktopEventService(createDesktopEventService)
    setAlignmentX(LEFT_ALIGNMENT)
    setLayout(new BorderLayout)
  }

  protected def createContents(dv: DrawingView): Component = {
    val sp: JScrollPane = new JScrollPane(dv.asInstanceOf[Component])
    sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)
    sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS)
    sp.setAlignmentX(LEFT_ALIGNMENT)
    var applicationTitle: String = null
    if (dv.drawing.getTitle == null) {
      applicationTitle = getDrawApplication.getApplicationName + " - " + getDrawApplication.getDefaultDrawingTitle
    } else {
      applicationTitle = getDrawApplication.getApplicationName + " - " + dv.drawing.getTitle
    }
    sp.setName(applicationTitle)
    sp
  }

  def getActiveDrawingView: DrawingView = getDesktopEventService.getActiveDrawingView

  def addToDesktop(dv: DrawingView, location: Int) {
    getDesktopEventService.addComponent(createContents(dv))
    container.validate
  }

  def removeFromDesktop(dv: DrawingView, location: Int) {
    getDesktopEventService.removeComponent(dv)
    container.validate
  }

  def removeAllFromDesktop(location: Int) {
    getDesktopEventService.removeAllComponents
    container.validate
  }

  def getAllFromDesktop(location: Int): List[DrawingView] = getDesktopEventService.getDrawingViews(getComponents.toList)

  def addDesktopListener(dpl: DesktopListener) {
    getDesktopEventService.addDesktopListener(dpl)
  }

  def removeDesktopListener(dpl: DesktopListener) {
    getDesktopEventService.removeDesktopListener(dpl)
  }

  private def container: Container = this

  protected def getDesktopEventService: DesktopEventService = myDesktopEventService

  private def setDesktopEventService(newDesktopEventService: DesktopEventService) {
    myDesktopEventService = newDesktopEventService
  }

  protected def createDesktopEventService: DesktopEventService = new DesktopEventService(this, container)

  private def setDrawApplication(newDrawApplication: DrawApplication) {
    myDrawApplication = newDrawApplication
  }

  protected def getDrawApplication: DrawApplication = myDrawApplication

  def updateTitle(newDrawingTitle: String) {
    setName(newDrawingTitle)
  }

  private var myDesktopEventService: DesktopEventService = null
  private var myDrawApplication: DrawApplication = null
}