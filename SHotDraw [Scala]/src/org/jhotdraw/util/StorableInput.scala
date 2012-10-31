/*
 * @(#)StorableInput.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import java.awt.Color
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StreamTokenizer
import java.lang.Object
import scala.collection.mutable.ArrayBuffer

/**
 * An input stream that can be used to resurrect Storable objects.
 * StorableInput preserves the object identity of the stored objects.
 *
 * @see Storable
 * @see StorableOutput
 *
 * @version <$CURRENT_VERSION$>s
 */
class StorableInput(stream: InputStream) {
  
  private var fTokenizer: StreamTokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(stream)))
  private var fMap: ArrayBuffer[Storable] = ArrayBuffer()
  
  fTokenizer.wordChars('$', '$')

  /**
   * Reads and resurrects a Storable object from the input stream.
   */
  def readStorable: Storable = {
    var storable: Storable = null
    val s: String = readString
    if (s == "NULL") {
      return null
    }
    if (s == "REF") {
      val ref: Int = readInt
      return retrieve(ref)
    }
    storable = makeInstance(s).asInstanceOf[Storable]
    map(storable)
    storable.read(this)
    storable
  }

  /**
   * Reads a string from the input stream.
   */
  def readString: String = {
    val token: Int = fTokenizer.nextToken
    if (token == StreamTokenizer.TT_WORD || token == '"') {
      fTokenizer.sval
    }
    val msg: String = "String expected in line: " + fTokenizer.lineno
    throw new IOException(msg)
  }

  /**
   * Reads an int from the input stream.
   */
  def readInt: Int = {
    val token: Int = fTokenizer.nextToken
    if (token == StreamTokenizer.TT_NUMBER) {
      return fTokenizer.nval.toInt
    }
    val msg: String = "Integer expected in line: " + fTokenizer.lineno
    throw new IOException(msg)
  }

  /**
   * Reads an int from the input stream.
   */
  def readLong: Long = {
    val token: Long = fTokenizer.nextToken
    if (token == StreamTokenizer.TT_NUMBER) {
      return fTokenizer.nval.toLong
    }
    val msg: String = "Long expected in line: " + fTokenizer.lineno
    throw new IOException(msg)
  }

  /**
   * Reads a color from the input stream.
   */
  def readColor: Color = new Color(readInt, readInt, readInt)

  /**
   * Reads a double from the input stream.
   */
  def readDouble: Double = {
    val token: Int = fTokenizer.nextToken
    if (token == StreamTokenizer.TT_NUMBER) {
      return fTokenizer.nval
    }
    val msg: String = "Double expected in line: " + fTokenizer.lineno
    throw new IOException(msg)
  }

  /**
   * Reads a boolean from the input stream.
   */
  def readBoolean: Boolean = readInt == 1

  private def makeInstance(className: String): Any = {
    try {
      val cl: Class[_] = Class.forName(className)
      cl.newInstance
    } catch {
      case e: NoSuchMethodError => {
        throw new IOException("Class " + className + " does not seem to have a no-arg constructor")
      }
      case e: ClassNotFoundException => {
        throw new IOException("No class: " + className)
      }
      case e: InstantiationException => {
        throw new IOException("Cannot instantiate: " + className)
      }
      case e: IllegalAccessException => {
        throw new IOException("Class (" + className + ") not accessible")
      }
    }
  }

  private def map(storable: Storable) {
    if (!fMap.contains(storable)) {
      fMap += storable
    }
  }

  private def retrieve(ref: Int): Storable = fMap(ref)

}

