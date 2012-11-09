/*
 * @(#)CommandMenu.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import org.shotdraw.framework.JHotDrawRuntimeException
import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.util._

import scala.collection.mutable.{Map => MutableMap}

/**
 * A Command enabled menu. Selecting a menu item
 * executes the corresponding command.
 *
 * @see Command
 *
 * @version <$CURRENT_VERSION$>
 */
class CommandMenu(name: String) extends JMenu(name) with ActionListener with CommandListener {

  /**
   * Adds a command to the menu. The item's label is
   * the command's name.
   */
  def add(command: Command) {
    addMenuItem(command, new JMenuItem(command.name))
  }

  /**
   * Adds a command with the given short cut to the menu. The item's label is
   * the command's name.
   */
  def add(command: Command, shortcut: MenuShortcut) {
    addMenuItem(command, new JMenuItem(command.name, shortcut.getKey))
  }

  /**
   * Adds a command with the given accelerator to the menu. The item's label is
   * the command's name.
   */
  def add(command: Command, ks: KeyStroke) {
    val item: JMenuItem = new JMenuItem(command.name)
    item.setAccelerator(ks)
    addMenuItem(command, item)
  }

  /**
   * Adds a command with the given short cut to the menu. The item's label is
   * the command's name.
   */
  def addCheckItem(command: Command) {
    addMenuItem(command, new JCheckBoxMenuItem(command.name))
  }

  protected def addMenuItem(command: Command, m: JMenuItem) {
    m.setName(command.name)
    m.addActionListener(this)
    add(m)
    command.addCommandListener(this)
    hm.put(m, command)
  }

  def remove(command: Command) {
    throw new JHotDrawRuntimeException("not implemented")
  }

  def remove(item: MenuItem) {
    throw new JHotDrawRuntimeException("not implemented")
  }

  /**
   * Changes the enabling/disabling state of a named menu item.
   */
  def enable(name: String, state: Boolean) {
    for((mItem, cmd) <- hm; if(mItem.getText == name)) {
      mItem setEnabled(state)
    }
  }

  def checkEnabled {
    for(i <- 0 to getMenuComponentCount-1) {
      val c: Component = getMenuComponent(i)
      c match {
        case menuItem: JMenuItem =>
          hm get(menuItem) match {
            case Some(cmd) => c setEnabled(cmd isExecutable)
            case None =>
          }
        case _ =>
      }
    }
  }

  /**
   * Executes the command.
   */
  def actionPerformed(e: ActionEvent) {
    val source = e.getSource
    hm foreach { case (mItem, cmd) =>
      if(source == mItem) {
        cmd execute
      } 
    }
  }

  def commandExecuted(commandEvent: EventObject) {
  }

  def commandExecutable(commandEvent: EventObject) {
  }

  def commandNotExecutable(commandEvent: EventObject) {
  }

  private val hm: MutableMap[JMenuItem, Command] = MutableMap[JMenuItem, Command]()
}



