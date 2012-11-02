/*
 * @(#)DNDFiguresTransferable.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.contrib.dnd

import java.awt.datatransfer._
import java.io._
import java.lang.Object

/**
 * @author  C.L.Gilbert <dnoyeb@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object DNDFiguresTransferable {
  var DNDFiguresFlavor: DataFlavor = new DataFlavor(classOf[DNDFigures], "DNDFigures")
}

class DNDFiguresTransferable(o: Any) extends Transferable with Serializable {
  import DNDFiguresTransferable._

  def getTransferDataFlavors: Array[DataFlavor] = Array[DataFlavor](DNDFiguresFlavor)

  def isDataFlavorSupported(flavor: DataFlavor): Boolean = flavor == DNDFiguresFlavor

  def getTransferData(flavor: DataFlavor): AnyRef = {
    if (!isDataFlavorSupported(flavor)) {
      throw new UnsupportedFlavorException(flavor)
    }
    return o
  }

  private var o: AnyRef = null
}