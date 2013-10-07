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
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import javax.annotation.CheckForNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

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
  public MeasureUpdate select(final File file, final SensorContext context,
    List<File> changedFiles,
    List<File> sourceDirs,
    boolean unitTests) {
    final Resource resource = fileToResource.toResource(file, sourceDirs, unitTests, context);

    if (resource == null) {
      LOG.debug("File not found in Sonar index: {}", file);
      return null;
    } else {
      if (changedFiles.contains(file)) {
        return fileChanged(file, resource, context);
      }
      return fileNotChanged(file, resource);
    }
  }

  public MeasureUpdate fileChanged(File file, Resource resource, SensorContext context) {
    try {
      Charset charset = fs.sourceCharset();

      String fileContent = FileUtils.readFileToString(file, charset.name());
      String[] lines = fileContent.split("(\r)?\n|\r", -1);
      return fileChanged(file, resource, lines.length);
    } catch (IOException e) {
      LOG.error("Unable to get scm information: {}", file, e);
      return MeasureUpdate.NONE;
    }
  }

  private MeasureUpdate fileNotChanged(File file, Resource resource) {
    LOG.debug("File not changed since previous analysis: {}", file);

    return new CopyPreviousMeasures(resource);
  }

  private MeasureUpdate fileChanged(File file, Resource resource, int lineCount) {
    LOG.debug("File changed since previous analysis: {}", file);

    return blame.save(file, resource, lineCount);
  }
}
