/*
 * @(#)StorableOutput.java
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
import java.awt.Color
import java.util.ArrayList
import java.util.List
import scala.collection.mutable.ArrayBuffer

/**
 * An output stream that can be used to flatten Storable objects.
 * StorableOutput preserves the object identity of the stored objects.
 *
 * @see Storable
 * @see StorableInput
 *
 * @version <$CURRENT_VERSION$>
 */
class StorableOutput(stream: OutputStream) {

  /**
   * Writes a storable object to the output stream.
   */
  def writeStorable(storable: Storable) {
    if (storable == null) {
      fStream.print("NULL")
      space()
      return
    }
    if (mapped(storable)) {
      writeRef(storable)
      return
    }
    incrementIndent()
    startNewLine()
    map(storable)
    fStream.print(storable.getClass.getName)
    space()
    storable.write(this)
    space()
    decrementIndent()
  }

  /**
   * Writes an int to the output stream.
   */
  def writeInt(i: Int) {
    fStream.print(i)
    space()
  }

  /**
   * Writes a long to the output stream.
   */
  def writeLong(l: Long) {
    fStream.print(l)
    space()
  }

  def writeColor(c: Color) {
    writeInt(c.getRed)
    writeInt(c.getGreen)
    writeInt(c.getBlue)
  }

  /**
   * Writes an int to the output stream.
   */
  def writeDouble(d: Double) {
    fStream.print(d)
    space()
  }

  /**
   * Writes an int to the output stream.
   */
  def writeBoolean(b: Boolean) {
    if (b) {
      fStream.print(1)
    }
    else {
      fStream.print(0)
    }
    space()
  }

  /**
   * Writes a string to the output stream. Special characters
   * are quoted.
   */
  def writeString(s: String) {
    fStream.print('"')
    for(i <- 0 to s.length-1) {
      val c = s.charAt(i)
      c match {
        case '\n' =>
          fStream.print('\\')
          fStream.print('n')
        case '"' =>
          fStream.print('\\')
          fStream.print('"')
        case '\\' =>
          fStream.print('\\')
          fStream.print('\\')
        case '\t' =>
          fStream.print('\\')
          fStream.print('\t')
        case _ =>
          fStream.print(c)
      }
    }
    fStream.print('"')
    space()
  }

  /**
   * Closes a storable output stream.
   */
  def close() {
    fStream.close()
  }

  private def mapped(storable: Storable): Boolean = {
    return fMap.contains(storable)
  }

  private def map(storable: Storable) {
    if (!fMap.contains(storable)) {
      fMap += storable
    }
  }

  private def writeRef(storable: Storable) {
    val ref = fMap.indexOf(storable)
    fStream.print("REF")
    space()
    fStream.print(ref)
    space()
  }

  private def incrementIndent() {
    fIndent += 4
  }

  private def decrementIndent() {
    fIndent -= 4
    if (fIndent < 0) fIndent = 0
  }

  private def startNewLine() {
    fStream.println
    for (i <- 0 to fIndent-1) space()
  }

  private def space() {
    fStream.print(' ')
  }

  private var fStream = new PrintWriter(stream)
  private var fMap = ArrayBuffer[Storable]()
  private var fIndent = 0
}

