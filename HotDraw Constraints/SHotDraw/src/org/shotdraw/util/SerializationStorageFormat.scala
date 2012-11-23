/*
 * @(#)SerializationStorageFormat.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import java.io._
import org.shotdraw.framework.Drawing

/**
 * A SerializationStorageFormat is a straight-forward file format to store and restore
 * Drawings. It uses Java's serialization mechanism to store Drawings. The SerializationStorageFormat
 * has the file extension "ser" (e.g. my_picasso.ser).
 *
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
class SerializationStorageFormat extends StandardStorageFormat {


  /**
   * Factory method to create the file extension recognized by the FileFilter for this
   * SerializationStorageFormat. The SerializationStorageFormat has the file extension "ser"
   * (e.g. my_picasso.ser).
   *
   * @return new file extension
   */
  protected override def createFileExtension: String = "ser"

  /**
   * Factory method to create a file description for the file type when displaying the
   * associated FileFilter.
   *
   * @return new file description
   */
  override def createFileDescription: String = "Serialization (" + getFileExtension + ")"

  /**
   * Store a Drawing under a given name. The name should be valid with regard to the FileFilter
   * that means, it should already contain the appropriate file extension.
   *
   * @param fileName file name of the Drawing under which it should be stored
   * @param saveDrawing drawing to be saved
   */
  override def store(fileName: String, saveDrawing: Drawing): String = {
    val stream = new FileOutputStream(adjustFileName(fileName))
    val output = new ObjectOutputStream(stream)
    output.writeObject(saveDrawing)
    output.close()
    adjustFileName(fileName)
  }

  /**
   * Restore a Drawing from a file with a given name. The name must be should with regard to the
   * FileFilter that means, it should have the appropriate file extension.
   *
   * @param fileName of the file in which the Drawing has been saved
   * @return restored Drawing
   */
  override def restore(fileName: String): Drawing = {
    try {
      val stream = new FileInputStream(fileName)
      val input = new ObjectInputStream(stream)
      input.readObject match {
        case d: Drawing => d
        case None => sys.error(fileName + " is not a Drawing")
      }
    } catch {
      case exception: ClassNotFoundException => {
        throw new IOException("Could not restore drawing '" + fileName + "': class not found!")
      }
    }
  }
}