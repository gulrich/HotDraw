/*
 * @(#)StorageFormatManager.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter
import java.io.File
import scala.collection.mutable.ArrayBuffer

/**
 * The StorageFormatManager is a contains StorageFormats.
 * It is not a Singleton because it could be necessary to deal with different
 * format managers, e.g. one for importing Drawings, one for exporting Drawings.
 * If one StorageFormat matches the file extension of the Drawing file, then this
 * StorageFormat can be used to store or restore the Drawing.
 *
 * @see StorageFormat
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class StorageFormatManager {
  /**
   * List containing all registered storage formats
   */
  private var myStorageFormats: ArrayBuffer[StorageFormat] = ArrayBuffer[StorageFormat]()
  /**
   * Default storage format that should be selected in a javax.swing.JFileChooser
   */
  private var myDefaultStorageFormat: StorageFormat = null
  
  /**
   * Add a StorageFormat that should be supported by this StorageFormatManager.
   *
   * @param newStorageFormat new StorageFormat to be supported
   */
  def addStorageFormat(newStorageFormat: StorageFormat) {
    myStorageFormats += newStorageFormat
  }

  /**
   * Remove a StorageFormat that should no longer be supported by this StorageFormatManager.
   * The StorageFormat is excluded in when search for a StorageFormat.
   *
   * @param oldStorageFormat old StorageFormat no longer to be supported
   */
  def removeStorageFormat(oldStorageFormat: StorageFormat) {
    myStorageFormats = myStorageFormats diff List(oldStorageFormat)
  }

  /**
   * Test, whether a StorageFormat is supported by this StorageFormat
   */
  def containsStorageFormat(checkStorageFormat: StorageFormat): Boolean = myStorageFormats.contains(checkStorageFormat)

  /**
   * Set a StorageFormat as the default storage format which is selected in a
   * javax.swing.JFileChooser. The default storage format must be already
   * added with addStorageFormat. Setting the default storage format to null
   * does not automatically remove the StorageFormat from the list of
   * supported StorageFormats.
   *
   * @param newDefaultStorageFormat StorageFormat that should be selected in a JFileChooser
   */
  def setDefaultStorageFormat(newDefaultStorageFormat: StorageFormat) {
    myDefaultStorageFormat = newDefaultStorageFormat
  }

  /**
   * Return the StorageFormat which is used as selected file format in a javax.swing.JFileChooser
   *
   * @return default storage format
   */
  def getDefaultStorageFormat: StorageFormat = myDefaultStorageFormat

  /**
   * Register all FileFilters supported by StorageFormats
   *
   * @param fileChooser javax.swing.JFileChooser to which FileFilters are added
   */
  def registerFileFilters(fileChooser: JFileChooser) {
    if (fileChooser.getDialogType == JFileChooser.OPEN_DIALOG) {
      for(e <- myStorageFormats if e.isRestoreFormat) {
        fileChooser.addChoosableFileFilter(e.getFileFilter)
      }
      val sf = getDefaultStorageFormat
      if (sf != null && sf.isRestoreFormat) {
        fileChooser.setFileFilter(sf.getFileFilter)
      }
    } else if (fileChooser.getDialogType == JFileChooser.SAVE_DIALOG) {
      for(e <- myStorageFormats if e.isStoreFormat) {
        fileChooser.addChoosableFileFilter(e.getFileFilter)
      }
      val sf = getDefaultStorageFormat
      if (sf != null && sf.isStoreFormat) {
        fileChooser.setFileFilter(sf.getFileFilter)
      }
    } else {
      for(e <- myStorageFormats) {
        fileChooser.addChoosableFileFilter(e.getFileFilter)
      }
      val sf = getDefaultStorageFormat
      if (sf != null) {
        fileChooser.setFileFilter(sf.getFileFilter)
      }
    }
  }

  /**
   * Find a StorageFormat that can be used according to a FileFilter to store a Drawing
   * in a file or restore it from a file respectively.
   *
   * @param findFileFilter FileFilter used to identify a StorageFormat
   * @return StorageFormat, if a matching file extension could be found, false otherwise
   */
  def findStorageFormat(findFileFilter: FileFilter): StorageFormat = myStorageFormats.find(e => e.getFileFilter == findFileFilter) match {
    case Some(elem) => elem
    case None => null
  }

  /**
   * Find a StorageFormat that can be used according to a file object to store a
   * Drawing in a file or restore it from a file respectively.
   *
   * @param file a File object to be matched
   * @return StorageFormat, if a matching file extension could be found, <code>null</code>
   *         otherwise
   */
  def findStorageFormat(file: File): StorageFormat = myStorageFormats.find(e => e.getFileFilter.accept(file)) match {
    case Some(elem) => elem
    case None => null
  }
}