/*
 * @(#)StandardVersionControlStrategy.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.util

import org.jhotdraw.framework._

/**
 * @author Wolfram Kaiser
 * @version <$CURRENT_VERSION$>
 */
class StandardVersionControlStrategy(var myVersionRequester: VersionRequester) extends VersionControlStrategy {

  /**
   * Define a strategy how to select those versions of JHotDraw
   * with which they are compatible.
   */
  def assertCompatibleVersion {
    val requiredVersions: List[String] = getVersionRequester.getRequiredVersions
    requiredVersions find(e => isCompatibleVersion(e)) match {
      case None => handleIncompatibleVersions
      case _ =>
    }
  }

  /**
   * This method is called in open() if an incompatible version has been
   * encountered. Applications can override this method to provide customized
   * exception handling for this case. In the default implementation, a
   * JHotDrawRuntimeException is thrown.
   */
  protected def handleIncompatibleVersions {
    val requiredVersions: List[String] = getVersionRequester.getRequiredVersions
    throw new JHotDrawRuntimeException("Incompatible version of JHotDraw found: " + VersionManagement.getJHotDrawVersion + " (expected: " +     requiredVersions.mkString("[",",","]") + ")")
  }

  /**
   * Subclasses can override this method to specify an algorithms that determines
   * how version strings are compared and which version strings can be regarded
   * as compatible. For example, a subclass may choose that all versions 5.x of
   * JHotDraw are compatible with the application, so only the first digit in
   * the version number is considered significant. In the default implementation,
   * all versions that are equal or greater than the expected version are compatible.
   *
   * @param compareVersionString application version to compare with JHotDraw's version
   */
  protected def isCompatibleVersion(compareVersionString: String): Boolean = VersionManagement.isCompatibleVersion(compareVersionString)

  private def setVersionRequester(newVersionRequester: VersionRequester) {
    myVersionRequester = newVersionRequester
  }

  protected def getVersionRequester: VersionRequester = myVersionRequester
}

