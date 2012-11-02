/*
 * @(#)StorageFormat.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import javax.swing.filechooser.FileFilter
import java.io.IOException
import org.shotdraw.framework.Drawing

/**
 * Interface to define a storage format. A StorageFormat is a strategy that knows how to store
 * and restore a Drawing according to a specific encoding. Typically it can be recognized by
 * a file extension. To identify a valid file format for a Drawing an appropriate FileFilter
 * for a javax.swing.JFileChooser component can be requested.
 *
 * @see Drawing
 * @see StorageFormatManager
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
abstract trait StorageFormat {
  /**
   * Return a FileFilter that can be used to identify files which can be stored and restored
   * with this Storage Format. Typically, each storage format has its own recognizable file
   * extension.
   *
   * @return FileFilter to be used with a javax.swing.JFileChooser
   */
  def getFileFilter: FileFilter

  /**
   * Every format has to identify itself as able to store and/or restore from
   * the format it represents. If the storage format can save to the format, it
   * should return true in this method.
   * @return boolean <code>true</code> if the format can save
   */
  def isStoreFormat: Boolean

  /**
   * Every format has to identify itself as able to store and/or restore from
   * the format it represents. If the storage format can load from the format,
   * it should return true in this method.
   * @return boolean <code>true</code> if the format can load
   */
  def isRestoreFormat: Boolean

  /**
   * Store a Drawing under a given name.
   *
   * @param fileName file name of the Drawing under which it should be stored
   * @param saveDrawing drawing to be saved
   * @return file name with correct file extension
   */
  def store(fileName: String, saveDrawing: Drawing): String

  /**
   * Restore a Drawing from a file with a given name.
   *
   * @param fileName of the file in which the Drawing has been saved
   * @return restored Drawing
   */
  def restore(fileName: String): Drawing
}

