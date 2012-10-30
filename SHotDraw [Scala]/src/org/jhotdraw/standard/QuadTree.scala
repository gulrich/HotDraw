/*
 * @(#)QuadTree.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.standard

import java.awt.geom.Rectangle2D
import java.io.Serializable
import org.jhotdraw.framework.Figure
import java.lang.Object

/**
 * @author WMG (INIT Copyright (C) 2000 All rights reserved)
 * @version <$CURRENT_VERSION$>
 */
object QuadTree {
  private final val serialVersionUID: Long = -2989788971735637894L
}

class QuadTree(nMaxTreeDepth: Int, absoluteBoundingRectangle2D: Rectangle2D) extends Serializable {
  
  private var _absoluteBoundingRectangle2D: Rectangle2D = new Rectangle2D.Double
  private var _nMaxTreeDepth: Int = 0
  private var _theHashtable: scala.collection.mutable.Map[Figure, Rectangle2D] = scala.collection.mutable.Map[Figure, Rectangle2D]()
  private var _outsideHashtable: scala.collection.mutable.Map[Figure, Rectangle2D] = scala.collection.mutable.Map[Figure, Rectangle2D]()
  private var _nwQuadTree: QuadTree = null
  private var _neQuadTree: QuadTree = null
  private var _swQuadTree: QuadTree = null
  private var _seQuadTree: QuadTree = null
  
  _init(nMaxTreeDepth, absoluteBoundingRectangle2D)
  
  def this(absoluteBoundingRectangle2D: Rectangle2D) {
    this(2, absoluteBoundingRectangle2D)
  }
 

  def add(anObject: Figure, absoluteBoundingRectangle2D: Rectangle2D) {
    if (_nMaxTreeDepth == 1) {
      if (absoluteBoundingRectangle2D.intersects(_absoluteBoundingRectangle2D)) {
        _theHashtable += ((anObject, absoluteBoundingRectangle2D))
      }
      else {
        _outsideHashtable += ((anObject, absoluteBoundingRectangle2D))
      }
      return
    }
    val bNW: Boolean = absoluteBoundingRectangle2D.intersects(_nwQuadTree.getAbsoluteBoundingRectangle2D)
    val bNE: Boolean = absoluteBoundingRectangle2D.intersects(_neQuadTree.getAbsoluteBoundingRectangle2D)
    val bSW: Boolean = absoluteBoundingRectangle2D.intersects(_swQuadTree.getAbsoluteBoundingRectangle2D)
    val bSE: Boolean = absoluteBoundingRectangle2D.intersects(_seQuadTree.getAbsoluteBoundingRectangle2D)
    var nCount: Int = 0
    if (bNW) {
      nCount += 1
    }
    if (bNE) {
      nCount += 1
    }
    if (bSW) {
      nCount += 1
    }
    if (bSE) {
      nCount += 1
    }
    if (nCount > 1) {
      _theHashtable += ((anObject, absoluteBoundingRectangle2D))
      return
    }
    if (nCount == 0) {
      _outsideHashtable += ((anObject, absoluteBoundingRectangle2D))
      return
    }
    if (bNW) {
      _nwQuadTree.add(anObject, absoluteBoundingRectangle2D)
    }
    if (bNE) {
      _neQuadTree.add(anObject, absoluteBoundingRectangle2D)
    }
    if (bSW) {
      _swQuadTree.add(anObject, absoluteBoundingRectangle2D)
    }
    if (bSE) {
      _seQuadTree.add(anObject, absoluteBoundingRectangle2D)
    }
  }

  def remove(anObject: Figure): Rectangle2D = {
    var returnObject: Rectangle2D = null
    _theHashtable.remove(anObject) match {
      case Some(rect) => rect
      case None =>
        if (_nMaxTreeDepth > 1) {
          returnObject = _nwQuadTree.remove(anObject)
          if (returnObject != null) {
            return returnObject
          }
          returnObject = _neQuadTree.remove(anObject)
          if (returnObject != null) {
            return returnObject
          }
          returnObject = _swQuadTree.remove(anObject)
          if (returnObject != null) {
            return returnObject
          }
          returnObject = _seQuadTree.remove(anObject)
          if (returnObject != null) {
            return returnObject
          }
        }
        _outsideHashtable.remove(anObject) match {
          case Some(figure) => figure
          case None => null
        }
    }   
  }

  def clear {
    _theHashtable.clear
    _outsideHashtable.clear
    if (_nMaxTreeDepth > 1) {
      _nwQuadTree.clear
      _neQuadTree.clear
      _swQuadTree.clear
      _seQuadTree.clear
    }
  }

  def getMaxTreeDepth: Int = _nMaxTreeDepth

  def getAllWithin(r: Rectangle2D): Seq[Figure] = {
    var l: List[Figure] = List[Figure]()
    _outsideHashtable foreach { case (fig,rect) =>
      if (rect.intersects(r)) l ::= fig
    }
    if (_absoluteBoundingRectangle2D.intersects(r)) {
      _theHashtable foreach { case (fig, rect) =>
        if (rect.intersects(r)) l ::= fig
      }
      if (_nMaxTreeDepth > 1) {
        return l  ++ _nwQuadTree.getAllWithin(r) ++ _neQuadTree.getAllWithin(r) ++ _swQuadTree.getAllWithin(r) ++ _seQuadTree.getAllWithin(r)
      }
    }
    l
  }

  def getAbsoluteBoundingRectangle2D: Rectangle2D = _absoluteBoundingRectangle2D  

  private def _init(nMaxTreeDepth: Int, absoluteBoundingRectangle2D: Rectangle2D) {
    _absoluteBoundingRectangle2D.setRect(absoluteBoundingRectangle2D)
    _nMaxTreeDepth = nMaxTreeDepth
    if (_nMaxTreeDepth > 1) {
      _nwQuadTree = new QuadTree(_nMaxTreeDepth - 1, _makeNorthwest(absoluteBoundingRectangle2D))
      _neQuadTree = new QuadTree(_nMaxTreeDepth - 1, _makeNortheast(absoluteBoundingRectangle2D))
      _swQuadTree = new QuadTree(_nMaxTreeDepth - 1, _makeSouthwest(absoluteBoundingRectangle2D))
      _seQuadTree = new QuadTree(_nMaxTreeDepth - 1, _makeSoutheast(absoluteBoundingRectangle2D))
    }
  }

  private def _makeNorthwest(r: Rectangle2D): Rectangle2D = new Rectangle2D.Double(r.getX, r.getY, r.getWidth / 2.0, r.getHeight / 2.0)

  private def _makeNortheast(r: Rectangle2D): Rectangle2D = new Rectangle2D.Double(r.getX + r.getWidth / 2.0, r.getY, r.getWidth / 2.0, r.getHeight / 2.0)

  private def _makeSouthwest(r: Rectangle2D): Rectangle2D = new Rectangle2D.Double(r.getX, r.getY + r.getHeight / 2.0, r.getWidth / 2.0, r.getHeight / 2.0)

  private def _makeSoutheast(r: Rectangle2D): Rectangle2D = new Rectangle2D.Double(r.getX + r.getWidth / 2.0, r.getY + r.getHeight / 2.0, r.getWidth / 2.0, r.getHeight / 2.0)

}

