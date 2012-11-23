/*
 * @(#)DeleteCommand.java
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
import org.shotdraw.framework.Figure
import org.shotdraw.util.Undoable
import org.shotdraw.util.UndoableAdapter
import scala.collection.mutable.ArrayBuffer
import javax.swing.JOptionPane
import org.shotdraw.application.DrawApplication
import org.shotdraw.figures.RectangularFigure
import java.awt.Point
import javax.swing.JDialog
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JTextField
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JScrollPane
import javax.swing.JButton
import javax.swing.JCheckBox
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.awt.Dialog
import ch.epfl.lamp.cassowary.SimplexSolver
import ch.epfl.lamp.cassowary.Constraint
import ch.epfl.lamp.cassowary.CVar
import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.event.FocusListener
import java.awt.event.FocusEvent

/**
 * Command to delete the selection.
 *
 * @version <$CURRENT_VERSION$>
 */
object ResizeCommand {

  class UndoActivity(myCommand: FigureTransferCommand) extends UndoableAdapter(myCommand.view) {
    setUndoable(true)
    setRedoable(true)

    /**
     * @see org.shotdraw.util.Undoable#undo()
     */
    override def undo: Boolean = {
      if (super.undo && !getAffectedFigures.isEmpty) {
        getDrawingView.clearSelection()
        setAffectedFigures(myCommand.insertFigures(getAffectedFiguresReversed, 0, 0))
        true
      } else false
    }

    /**
     * @see org.shotdraw.util.Undoable#redo()
     */
    override def redo: Boolean = {
      if (isRedoable) {
        myCommand.deleteFigures(getAffectedFigures)
        getDrawingView.clearSelection()
        true
      } else false
    }
  }

}

class ResizeCommand(name: String, newDrawingEditor: DrawApplication) extends FigureTransferCommand(name, newDrawingEditor) {
  /**
   * @see org.shotdraw.util.Command#execute()
   */
  override def execute() {
    super.execute()
    setUndoActivity(createUndoActivity)
    var fe = view.selection
    fe match {
      case Seq(f: AbstractFigure) =>
        getUndoActivity.setAffectedFigures(Seq(f))
        val origin = new Point(f.displayBox.x,f.displayBox.y)
        val newPoint = new ResizeDialog(origin, f.displayBox.width,f.displayBox.height).showDialog
        if(newPoint != null) {
          println(newPoint)
          f.displayBox(origin, newPoint)
        }
        view.checkDamage()
      case _ => JOptionPane.showMessageDialog(newDrawingEditor,"Cannot resize more than one figure at a time.")
    }
  }

  /**
   * @see org.shotdraw.standard.AbstractCommand#isExecutableWithView()
   */
  protected override def isExecutableWithView: Boolean = view.selectionCount > 0

  /**
   * Factory method for undo activity
   * @return Undoable
   */
  protected def createUndoActivity: Undoable = new ResizeCommand.UndoActivity(this)
}

class ResizeDialog(origin: Point, width: Int, height: Int) {
  var point = new Point(width,height)
  private var solver = new SimplexSolver
  
  private val cwidth = CVar(width)
  private val cheight = CVar(height)
  private val pwidth = CVar(100)
  private val pheight = CVar(100)
  private val ratio = pwidth :== pheight
  
  solver.addStay(pwidth)
  solver.addStay(pheight)
  ensure(cwidth :== pwidth*width/100)
  ensure(cheight :== pheight*height/100)
  ensure(ratio)
  
  private val perW = new JTextField()
  private val perH = new JTextField()
  private val pxW = new JTextField()
  private val pxH = new JTextField()
  
  def refresh() {
    perW.setText("%.2f" format pwidth.value)
    perH.setText("%.2f" format pheight.value)
    pxW.setText("%.2f" format cwidth.value)
    pxH.setText("%.2f" format cheight.value)
  } 
  
  def focusListener(tf: JTextField, cvar: CVar) {
    tf.addFocusListener(new FocusListener {
      def focusGained(e: FocusEvent) {}
      def focusLost(e:FocusEvent) {
        solver.addEditVar(cvar).beginEdit
        solver.suggestValue(cvar, tf.getText.toDouble).resolve
        solver.endEdit
        refresh
      }
    })
  }
  
  def ensure(c: Constraint) {
    solver.addConstraint(c)
  }

  def showDialog: Point = {
    val dialog: JDialog = new JDialog
    dialog.setTitle("Resize")
    dialog.setSize(500,120)
    dialog.setLayout(new GridBagLayout)
    dialog.setResizable(false)
    
    val wini = new JLabel("Initial width")
    val hini = new JLabel("Initial height")
    val wval = new JLabel(width+"px")
    val hval = new JLabel(height+"px")
    
    val wpx = new JLabel("Absolute width")
    val hpx = new JLabel("Absolute height")
    val wper = new JLabel("Relative width")
    val hper = new JLabel("Relative height")
    
    refresh
    
    focusListener(perW,pwidth)
    focusListener(perH,pheight)
    focusListener(pxW,cwidth)
    focusListener(pxH,cheight)
    
    val ok = new JButton("OK")
    ok.addActionListener(new ActionListener{
      override def actionPerformed(e: ActionEvent) {
        point = new Point(origin.x+cwidth.value.toInt,origin.y+cheight.value.toInt)
        dialog.dispose()
      }
    })
    
    val cancel = new JButton("Cancel")
    cancel.addActionListener(new ActionListener{
      override def actionPerformed(e: ActionEvent) {
        point = null
        dialog.dispose
      }
    })
    
    val ratioCb = new JCheckBox("Preserve ratio")
    ratioCb.addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent) {
        if(ratioCb.isSelected) {
          solver.removeConstraint(ratio)
          //Question here
        }
        else ensure(ratio)
      }
    })
    ratioCb.setSelected(true)
    
    var gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(wini, gbc)
	
	gbc = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(wval, gbc)
	
	gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(hini, gbc)
    
	gbc = new GridBagConstraints(3,0,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(hval, gbc)
	
    //First line
    gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(wpx, gbc)
	
	gbc = new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(pxW, gbc)
	
	gbc = new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(hpx, gbc)
    
	gbc = new GridBagConstraints(3,1,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(pxH, gbc)
	
	//Second line
    gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(wper, gbc)
	
	gbc = new GridBagConstraints(1,2,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(perW, gbc)
	
	gbc = new GridBagConstraints(2,2,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(hper, gbc)
    
	gbc = new GridBagConstraints(3,2,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(perH, gbc)
	
	//Third line
	gbc = new GridBagConstraints(0,3,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(ratioCb, gbc)
	
	gbc = new GridBagConstraints(2,3,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(cancel, gbc)
    
	gbc = new GridBagConstraints(3,3,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
	dialog.add(ok, gbc)
	
    dialog.setLocationRelativeTo(null)
    dialog.setModal(true)
    dialog.setVisible(true)
    point
  }
  
}

