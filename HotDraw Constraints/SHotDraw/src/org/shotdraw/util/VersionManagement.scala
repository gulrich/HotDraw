/*
 * @(#)VersionManagement.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.shotdraw.util
import java.io.IOException
import java.io.FileInputStream
import java.io.File
import java.util.jar._
import java.util.jar.Attributes
import scala.collection.JavaConversions._




/**
 * Collection of utility methods that are useful for dealing with version information
 * retrieved from reading jar files or package loaded by the class manager. Some
 * methods also help comparing version information. The method getJHotDrawVersion()
 * can be used to retrieve the current version of JHotDraw as loaded by the class manager.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
object VersionManagement {
  /**
   * Return the version of the main package of the framework. A version number is
   * available if there is a corresponding version entry in the JHotDraw jar file
   * for the framework package.
   */
  def getJHotDrawVersion: String = packages(4).getSpecificationVersion

  /**
   *
   */
  def getPackageVersion(lookupPackage: Package): String = {
    if (lookupPackage == null) {
      return null
    }
    val specVersion = lookupPackage.getSpecificationVersion
    if (specVersion != null) {
      return specVersion
    }
    else {
      val normalizedPackageName = normalizePackageName(lookupPackage.getName)
      val nextPackageName = getNextPackage(normalizedPackageName)
      return getPackageVersion(Package.getPackage(nextPackageName))
    }
  }

  /**
   * Check whether a given application version is compatible with the version
   * of JHotDraw currently loaded in the Java VM. A version number is
   * available if there is a corresponding version entry in the JHotDraw jar file
   * for the framework package.
   */
  def isCompatibleVersion(compareVersionString: String): Boolean = {
    val pack = packages(4)
    (compareVersionString == null && pack.getSpecificationVersion == null) || pack.isCompatibleWith(compareVersionString)  
  }

  /**
   * Read the version information from a file with a given file name. The file
   * must be a jar manifest file and all its entries are searched for package names
   * and their specification version information. All information is collected
   * in a map for later lookup of package names and their versions.
   *
   * @param versionFileName name of the jar file containing version information
   */
  def readVersionFromFile(applicationName: String, versionFileName: String): String = {
    try {
      val fileInput = new FileInputStream(versionFileName)
      val manifest = new Manifest
      manifest.read(fileInput)
      val entries = manifest.getEntries
      
      entries foreach { case e@(s, a) =>
        normalizePackageName(s)
        return extractVersionInfo(a.getValue(Attributes.Name.SPECIFICATION_VERSION))
      }
    } catch { case exception: IOException => exception.printStackTrace
    }
    ""
  }

  /**
   * Get the super package of a package specifier. The super package is the package
   * specifier without the latest package name, e.g. the next package for "org.shotdraw.tools"
   * would be "org.shotdraw".
   *
   * @param searchPackage package specifier
   * @return next package if one is available, null otherwise
   */
  protected def getNextPackage(searchPackage: String): String = {
    if (searchPackage == null) {
      return null
    }
    val foundNextPackage = searchPackage.lastIndexOf('.')
    if (foundNextPackage > 0) {
      return searchPackage.substring(0, foundNextPackage)
    }
    else {
      return null
    }
  }

  /**
   * A package name is normalized by replacing all path delimiters by "." to retrieve
   * a valid standardized package specifier used in package declarations in Java source
   * files.
   *
   * @param toBeNormalized package name to be normalized
   * @return normalized package name
   */
  def normalizePackageName(toBeNormalized: String): String = {
    var replaced = toBeNormalized.replace('/', '.')
    replaced = replaced.replace(File.pathSeparatorChar, '.')
    if (replaced.endsWith(".")) {
      val lastSeparator = replaced.lastIndexOf('.')
      return replaced.substring(0, lastSeparator)
    }
    else {
      return replaced
    }
  }

  /**
   * Get the version information specified in a jar manifest file without
   * any leading or trailing "\"".
   *
   * @param versionString a version string with possibly leading or trailing "\""
   * @return stripped version information
   */
  def extractVersionInfo(versionString: String): String = {
    if (versionString == null) {
      return null
    }
    if (versionString.length == 0) {
      return ""
    }
    var startIndex = versionString.indexOf("\"")
    if (startIndex < 0) {
      startIndex = 0
    }
    else {
      startIndex += 1
    }
    var endIndex = versionString.lastIndexOf("\"")
    if (endIndex < 0) {
      endIndex = versionString.length
    }
    versionString.substring(startIndex, endIndex)
  }

  var JHOTDRAW_COMPONENT = "org.shotdraw/"
  var JHOTDRAW_JAR = "jhotdraw.jar"
  var packages = List(Package.getPackage("org.shotdraw.applet"), Package.getPackage("org.shotdraw.application"), Package.getPackage("org.shotdraw.contrib"), Package.getPackage("org.shotdraw.figures"), Package.getPackage("org.shotdraw.framework"), Package.getPackage("org.shotdraw.standard"), Package.getPackage("org.shotdraw.util"))
}

class VersionManagement
