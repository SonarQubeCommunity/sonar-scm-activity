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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.InputFile;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import javax.annotation.CheckForNull;

import java.io.IOException;
import java.nio.charset.Charset;

public class BlameVersionSelector implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(BlameVersionSelector.class);

  private final Blame blame;
  private final ModuleFileSystem fs;
  private final FileToResource fileToResource;

  public BlameVersionSelector(FileToResource fileToResource, Blame blame, ModuleFileSystem fs) {
    this.fileToResource = fileToResource;
    this.blame = blame;
    this.fs = fs;
  }

  @CheckForNull
  public MeasureUpdate select(final InputFile file, final SensorContext context) {
    final Resource resource = fileToResource.toResource(file);

    if (resource == null) {
      LOG.debug("Unable to convert file in resource: {}", file);
      return null;
    } else {
      if (file.has(InputFile.ATTRIBUTE_STATUS, InputFile.STATUS_SAME)) {
        return fileNotChanged(file, resource);
      }
      return fileChanged(file, resource, context);
    }
  }

  /**
  * TODO Waiting for an official API in SonarQube to convert from InputFile to Resource
  */
  public static Resource toResource(org.sonar.api.scan.filesystem.InputFile inputFile) {
    String sourceRelativePath = inputFile.attribute(InputFile.ATTRIBUTE_SOURCE_RELATIVE_PATH);
    if (sourceRelativePath != null) {
      boolean isTest = InputFile.TYPE_TEST.equals(inputFile.attribute(InputFile.ATTRIBUTE_TYPE));
      boolean isJava = Java.KEY.equals(inputFile.attribute(InputFile.ATTRIBUTE_LANGUAGE));
      return isJava ? JavaFile.fromRelativePath(sourceRelativePath, isTest) : new org.sonar.api.resources.File(sourceRelativePath);
    }
    return null;
  }

  public MeasureUpdate fileChanged(InputFile inputFile, Resource resource, SensorContext context) {
    try {
      Charset charset = fs.sourceCharset();

      String fileContent = FileUtils.readFileToString(inputFile.file(), charset.name());
      String[] lines = fileContent.split("(\r)?\n|\r", -1);
      return fileChanged(inputFile, resource, lines.length);
    } catch (IOException e) {
      LOG.error("Unable to get scm information: {}", inputFile, e);
      return MeasureUpdate.NONE;
    }
  }

  private MeasureUpdate fileNotChanged(InputFile inputFile, Resource resource) {
    LOG.debug("File not changed since previous analysis: {}", inputFile);

    return new CopyPreviousMeasures(resource);
  }

  private MeasureUpdate fileChanged(InputFile inputFile, Resource resource, int lineCount) {
    LOG.debug("File changed since previous analysis: {}", inputFile);

    return blame.save(inputFile.file(), resource, lineCount);
  }
}
