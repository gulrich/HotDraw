/*
 * @(#)Iconkit.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util

import java.awt.Component
import java.awt.Image
import java.awt.MediaTracker
import java.awt.Toolkit
import java.awt.image.ImageProducer
import java.net.URL
import javax.swing.ImageIcon
import scala.collection.mutable.ArrayBuffer

/**
 * The Iconkit class supports the sharing of images. It maintains
 * a map of image names and their corresponding images.
 *
 * Iconkit also supports to load a collection of images in
 * synchronized way.
 * The resolution of a path name to an image is delegated to the DrawingEditor.
 * <hr>
 * <b>Design Patterns</b><P>
 * <img src="images/red-ball-small.gif" width=6 height=6 alt=" o ">
 * <b><a href=../pattlets/sld031.htm>Singleton</a></b><br>
 * The Iconkit is a singleton.
 * <hr>
 *
 * @version <$CURRENT_VERSION$>
 */
object Iconkit {
  /**
   * Gets the single instance
   */
  def instance: Iconkit = new Iconkit

  private final val ID: Int = 123
}

class Iconkit {
  /**
   * Constructs an Iconkit that uses the given editor to
   * resolve image path names.
   */
  def this(component: Component) {
    this()
    fComponent = component
    fgIconkit = this
  }

  /**
   * Loads all registered images.
   * @see #registerImage
   */
  def loadRegisteredImages(component: Component) {
    val tracker: MediaTracker = new MediaTracker(component)
    for (s <- fRegisteredImages if basicGetImage(s).isDefined)
      tracker.addImage(loadImage(s).get, Iconkit.ID)
//    fRegisteredImages.clear
    try {
      tracker.waitForAll
    }
    catch {
      case e: Exception => {
        println(e)
      }
    }
  }

  /**
   * Registers an image that is then loaded together with
   * the other registered images by loadRegisteredImages.
   * @see #loadRegisteredImages
   */
  def registerImage(fileName: String) {
    fRegisteredImages += fileName
  }

  /**
   * Registers and loads an image.
   */
  def registerAndLoadImage(component: Component, fileName: String): Option[Image] = {
    registerImage(fileName)
    loadRegisteredImages(component)
    getImage(fileName)
  }

  /**
   * Loads an image with the given name.
   */
  def loadImage(filename: String): Option[Image] = fMap.get(filename) match {
    case Some(image) => Some(image)
    case None =>
      val image: Option[Image] = loadImageResource(filename)
      if(image.isDefined) fMap += ((filename, image.get))
      image
  }

  def loadImage(filename: String, waitForLoad: Boolean): Option[Image] = loadImage(filename) match {
    case Some(image) if waitForLoad => Some(new ImageIcon(image).getImage)
    case e => e
  }

  def loadImageResource(resourcename: String): Option[Image] = {
    if (fgDebug) System.out.println(resourcename)
    try {
      getClass.getResource(resourcename).getContent match {
        case ip: ImageProducer => Some(Toolkit.getDefaultToolkit.createImage(ip))
        case _ => None
      }
    } catch { case ex: Exception => None }
  }

  /**
   * Gets the image with the given name. If the image
   * can't be found it tries it again after loading
   * all the registered images.
   */
  def getImage(filename: String): Option[Image] = basicGetImage(filename) match {
    case Some(image) => Some(image)
    case None =>
      loadRegisteredImages(fComponent)
      basicGetImage(filename)
  }

  private def basicGetImage(filename: String): Option[Image] = fMap.get(filename)

  private var fMap: Map[String, Image] = Map()
  private var fRegisteredImages: ArrayBuffer[String] = ArrayBuffer()
  private var fgIconkit: Iconkit = null
  private var fComponent: Component = null
  private var fgDebug: Boolean = false
}

