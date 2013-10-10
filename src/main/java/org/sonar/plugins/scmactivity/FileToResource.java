/*
 * Sonar SCM Activity Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.scmactivity;

import org.sonar.api.BatchExtension;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.InputFile;

public class FileToResource implements BatchExtension {

  public FileToResource() {
  }

  /**
   * TODO Waiting for an official API in SonarQube to convert from InputFile to Resource
   */
  public Resource toResource(org.sonar.api.scan.filesystem.InputFile inputFile) {
    String sourceRelativePath = inputFile.attribute(InputFile.ATTRIBUTE_SOURCE_RELATIVE_PATH);
    if (sourceRelativePath != null) {
      boolean isTest = InputFile.TYPE_TEST.equals(inputFile.attribute(InputFile.ATTRIBUTE_TYPE));
      boolean isJava = Java.KEY.equals(inputFile.attribute(InputFile.ATTRIBUTE_LANGUAGE));
      return isJava ? JavaFile.fromRelativePath(sourceRelativePath, isTest) : new org.sonar.api.resources.File(sourceRelativePath);
    }
    return null;
  }
}
