/*
 * @(#)StandardStorageFormat.java
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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File
import org.shotdraw.framework.Drawing

/**
 * A StandardStorageFormat is an internal file format to store and restore
 * Drawings. It uses its own descriptive syntax ands write classes and attributes
 * as plain text in a text file. The StandardStorageFormat has the file extension
 * "draw" (e.g. my_picasso.draw).
 *
 * @author Wolfram Kaiser <mrfloppy@users.sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
class StandardStorageFormat extends StorageFormat {
  /**
   * Test whether the file name has the correct file extension
   *
   * @return true, if the file has the correct extension, false otherwise
   */
  protected def hasCorrectFileExtension(testFileName: String): Boolean = testFileName.endsWith("." + getFileExtension)
  /**
   * File extension
   */
  private var myFileExtension = createFileExtension
  /**
   * Description of the file type when displaying the FileFilter
   */
  private var myFileDescription = createFileDescription
  /**
   * FileFilter for a javax.swing.JFileChooser which recognizes files with the
   * extension "draw"
   */
  private var myFileFilter = createFileFilter
  /**
   * Factory method to create the file extension recognized by the FileFilter for this
   * StandardStorageFormat. The StandardStorageFormat has the file extension "draw"
   * (e.g. my_picasso.draw).
   *
   * @return new file extension
   */
  protected def createFileExtension: String = "draw"

  /**
   * Set the file extension for the storage format
   *
   * @param newFileExtension extension
   */
  def setFileExtension(newFileExtension: String) {
    myFileExtension = newFileExtension
  }

  /**
   * Return the file extension for the storage format
   *
   * @return file extension
   */
  def getFileExtension: String = myFileExtension

  /**
   * Factory method to create a file description for the file type when displaying the
   * associated FileFilter.
   *
   * @return new file description
   */
  def createFileDescription: String = "Internal Format (" + getFileExtension + ")"

  /**
   * Set the file description for the file type of the storage format
   *
   * @param newFileDescription description of the file type
   */
  def setFileDescription(newFileDescription: String) {
    myFileDescription = newFileDescription
  }

  /**
   * Return the file description for the file type of the storage format
   *
   * @return description of the file type
   */
  def getFileDescription: String = myFileDescription

  /**
   * Factory method to create a FileFilter that accepts file with the appropriate
   * file exention used by a javax.swing.JFileChooser. Subclasses can override this
   * method to provide their own file filters.
   *
   * @return FileFilter for this StorageFormat
   */
  protected def createFileFilter: FileFilter = {
    new FileFilter {
      def accept(checkFile: File): Boolean = checkFile.isDirectory || checkFile.getName.endsWith("." + getFileExtension)

      def getDescription: String = getFileDescription
    }
  }

  /**
   * Set the FileFilter used to identify Drawing files with the correct file
   * extension for this StorageFormat.
   *
   * @param newFileFilter FileFilter for this StorageFormat
   */
  def setFileFilter(newFileFilter: FileFilter) {
    myFileFilter = newFileFilter
  }

  /**
   * Return the FileFilter used to identify Drawing files with the correct file
   * extension for this StorageFormat.
   *
   * @return FileFilter for this StorageFormat
   */
  def getFileFilter: FileFilter = myFileFilter

  /**
   * @see org.shotdraw.util.StorageFormat#isRestoreFormat()
   */
  def isRestoreFormat: Boolean = true

  /**
   * @see org.shotdraw.util.StorageFormat#isStoreFormat()
   */
  def isStoreFormat: Boolean = true

  /**
   * Store a Drawing under a given name. If the file name does not have the correct
   * file extension, then the file extension is added.
   *
   * @param fileName file name of the Drawing under which it should be stored
   * @param saveDrawing drawing to be saved
   * @return file name with correct file extension
   */
  def store(fileName: String, saveDrawing: Drawing): String = {
    val stream = new FileOutputStream(adjustFileName(fileName))
    val output = new StorableOutput(stream)
    output.writeStorable(saveDrawing)
    output.close
    adjustFileName(fileName)
  }

  /**
   * Restore a Drawing from a file with a given name.
   *
   * @param fileName of the file in which the Drawing has been saved
   * @return restored Drawing
   */
  def restore(fileName: String): Drawing = {
    if (!hasCorrectFileExtension(fileName)) null
    else {
      val stream = new FileInputStream(fileName)
      val input = new StorableInput(stream)
      input.readStorable match {
        case d: Drawing => d
        case _ => sys.error(fileName + " is not a Drawing")
      }
    }
  }

  /**
   * Test, whether two StorageFormats are the same. They are the same if they both support the
   * same file extension.
   *
   * @return true, if both StorageFormats have the same file extension, false otherwise
   */
  override def equals(compareObject: Any): Boolean = compareObject match {
    case s: StandardStorageFormat => getFileExtension == s.getFileExtension
    case _ => false
  }

  /**
   * Adjust a file name to have the correct file extension.
   *
   * @param testFileName file name to be tested for a correct file extension
   * @return testFileName + file extension if necessary
   */
  protected def adjustFileName(testFileName: String): String = {
    if (!hasCorrectFileExtension(testFileName)) testFileName + "." + getFileExtension
    else testFileName
  }
}