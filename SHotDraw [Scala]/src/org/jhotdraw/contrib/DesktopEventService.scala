/*
 * @(#)DesktopEventService.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.contrib

import java.awt.Component
import java.awt.Container
import java.awt.event.ContainerAdapter
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import org.jhotdraw.framework.DrawingView
import org.jhotdraw.standard.NullDrawingView
import scala.collection.mutable.ArrayBuffer

/**
 * @author  Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class DesktopEventService(var myDesktop: Desktop, var myContainer: Container) {
  getContainer.addContainerListener(createComponentListener)

  private def setDesktop(newDesktop: Desktop) {
    myDesktop = newDesktop
  }

  protected def getDesktop: Desktop = myDesktop

  private def setContainer(newContainer: Container) {
    myContainer = newContainer
  }

  protected def getContainer: Container = myContainer

  def addComponent(newComponent: Component) {
    getContainer.add(newComponent)
  }

  def removeComponent(dv: DrawingView) {
    getContainer.getComponents.find(c => dv == Helper.getDrawingView(c)) match {
      case Some(comp) => getContainer.remove(comp)
      case _ =>
    }
  }

  def removeAllComponents {
    getContainer.removeAll
  }

  def addDesktopListener(dpl: DesktopListener) {
    listeners += dpl
  }

  def removeDesktopListener(dpl: DesktopListener) {
    listeners = listeners diff List(dpl)
  }

  protected def fireDrawingViewAddedEvent(dv: DrawingView) {
    val dpe: DesktopEvent = createDesktopEvent(getActiveDrawingView, dv)
    listeners.reverse.foreach {dpl =>
      dpl.drawingViewAdded(dpe)
    }
  }

  protected def fireDrawingViewRemovedEvent(dv: DrawingView) {
    val dpe: DesktopEvent = createDesktopEvent(getActiveDrawingView, dv)
    listeners.reverse.foreach { dpl =>
      dpl.drawingViewRemoved(dpe)
    }
  }

  /**
   * This method is only called if the selected drawingView has actually changed
   */
  protected def fireDrawingViewSelectedEvent(oldView: DrawingView, newView: DrawingView) {
    val dpe: DesktopEvent = createDesktopEvent(oldView, newView)
    listeners.reverse.foreach{ dpl =>
      dpl.drawingViewSelected(dpe)
    }
  }

  /**
   * @param oldView previous active drawing view (may be null because not all events require this information)
   */
  protected def createDesktopEvent(oldView: DrawingView, newView: DrawingView): DesktopEvent = new DesktopEvent(getDesktop, newView, oldView)

  def getDrawingViews(comps: List[Component]): List[DrawingView] = {
    var al: List[DrawingView] = List[DrawingView]()
    comps foreach {c =>
      val dv: DrawingView = Helper.getDrawingView(c)
      if(dv != null) al ::= dv
    }
    al
  }

  def getActiveDrawingView: DrawingView = mySelectedView

  protected def setActiveDrawingView(newActiveDrawingView: DrawingView) {
    mySelectedView = newActiveDrawingView
  }

  protected def createComponentListener: ContainerListener = new ContainerAdapter {
      /**
       * If the dv is null assert
       * @todo does adding a component always make it the selected view?
       *       Yes so far because this is only being used on single view Desktops.
       *       If it is to work on multipleView desktops, the we need to think further.
       */
      override def componentAdded(e: ContainerEvent) {
        val dv: DrawingView = Helper.getDrawingView(e.getChild.asInstanceOf[Container])
        val oldView: DrawingView = getActiveDrawingView
        if (dv != null) {
          fireDrawingViewAddedEvent(dv)
          setActiveDrawingView(dv)
          fireDrawingViewSelectedEvent(oldView, getActiveDrawingView)
        }
      }

      /**
       * If dv is null assert
       * dv will only be null if something thats not a drawingView was
       * added to the desktop.  it would be simpler if we forbade that.
       */
      override def componentRemoved(e: ContainerEvent) {
        val dv: DrawingView = Helper.getDrawingView(e.getChild.asInstanceOf[Container])
        if (dv != null) {
          val oldView: DrawingView = getActiveDrawingView
          setActiveDrawingView(NullDrawingView.getManagedDrawingView(oldView.editor))
          fireDrawingViewSelectedEvent(oldView, getActiveDrawingView)
          fireDrawingViewRemovedEvent(dv)
        }
      }
  }

  /**
   * Current usage of this List is not thread safe, nor should it need to be.
   * If it ever does we can synchronize on the List itself to provide safety.
   */
  private var listeners: ArrayBuffer[DesktopListener] = ArrayBuffer()
  private var mySelectedView: DrawingView = null
}

