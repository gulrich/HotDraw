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
import java.awt.Graphics
import java.awt.image.ImageObserver

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
  private var fgDebug = false
  private var fgIconkit: Iconkit = null
  
  def instance: Iconkit = fgIconkit

  private final val ID = 123
}

class Iconkit(fComponent: Component) {
  import Iconkit._
  private var fMap = Map[String, Image]()
  private var fRegisteredImages = ArrayBuffer[String]()

  fgIconkit = this
  
  /**
   * Loads all registered images.
   * @see #registerImage
   */
  def loadRegisteredImages(component: Component) {
    val tracker = new MediaTracker(component)
    for (s <- fRegisteredImages if basicGetImage(s) != NoImage)
      tracker.addImage(loadImage(s), Iconkit.ID)
//    fRegisteredImages.clear()
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
  def registerAndLoadImage(component: Component, fileName: String): Image = {
    registerImage(fileName)
    loadRegisteredImages(component)
    getImage(fileName)
  }

  /**
   * Loads an image with the given name.
   */
  def loadImage(filename: String): Image = fMap.get(filename) match {
    case Some(image) => image
    case None =>
      val image = loadImageResource(filename)
      if(image != NoImage) fMap += ((filename, image))
      image
  }

  def loadImage(filename: String, waitForLoad: Boolean): Image = loadImage(filename) match {
    case image if waitForLoad => new ImageIcon(image).getImage
    case e => e
  }

  def loadImageResource(resourcename: String): Image = {
    if (fgDebug) System.out.println(resourcename)
    try {
      getClass.getResource(resourcename).getContent match {
        case ip: ImageProducer => Toolkit.getDefaultToolkit.createImage(ip)
        case _ => NoImage
      }
    } catch { case ex: Exception => NoImage }
  }

  /**
   * Gets the image with the given name. If the image
   * can't be found it tries it again after loading
   * all the registered images.
   */
  def getImage(filename: String): Image = basicGetImage(filename) match {
    case NoImage =>
      loadRegisteredImages(fComponent)
      basicGetImage(filename)
    case image => image
  }

  private def basicGetImage(filename: String): Image = fMap.get(filename) match {
    case Some(image) => image
    case _ => NoImage
  }
}

object NoImage extends Image {
  
  override def flush() {} 
  override def getGraphics: Graphics = null
  override def getHeight(observer: ImageObserver): Int = 0 
  override def getProperty(name: String, observer: ImageObserver): Object = null  
  override def getSource: ImageProducer = null
  override def getWidth(observer: ImageObserver): Int = 0 
}

