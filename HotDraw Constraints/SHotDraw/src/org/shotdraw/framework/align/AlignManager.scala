package org.shotdraw.framework.align
import scala.collection.mutable.ArrayBuffer
import javax.swing.JPanel
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JCheckBox
import java.awt.event.ActionListener
import javax.swing.JFrame
import javax.swing.JScrollPane
import java.awt.event.ActionEvent
import javax.swing.JList
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import org.shotdraw.framework.align.alignments.Align

object AlignManager {

  private val constraints = ArrayBuffer[Align]()
  
  def add(a: Align) {
    constraints += a
    a.enable()
  }
  
  def remove(a: Align) {
    constraints -= a
    a.disable()
  }
  
  def enableAll() {
    constraints foreach (_.enable())
  }
  
  def disableAll() {
    constraints foreach (_.disable())
  }
  
  def show {
    val frame = new JFrame
    frame.setSize(200,100)
    val panel = new JPanel
    panel.setLayout(new GridBagLayout)
    var gbc = new GridBagConstraints(0,GridBagConstraints.RELATIVE,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1),0 ,0)
    constraints foreach { c => panel.add(createPanel(c),gbc) }
    val pane = new JScrollPane(panel)
    frame.add(pane)
    frame.setVisible(true)
  }
  
  private def createPanel(a: Align) = {
    val frame = new JFrame
    frame.setSize(200,100)
    val pan = new JPanel
    pan.setLayout(new BorderLayout)
    val label = new JLabel(a.name + " alignment")
    label.setToolTipText(a.toString())
    pan.add(label, BorderLayout.WEST)
    val box = new JCheckBox
    box.setSelected(a.enabled);
    box.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        if(box.isSelected) a.enable()
        else a.disable()
      }
    })
    pan.add(box, BorderLayout.EAST)
    pan
  } 
  
  
}