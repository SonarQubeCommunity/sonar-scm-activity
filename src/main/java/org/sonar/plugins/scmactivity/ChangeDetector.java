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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Resource;

import java.io.File;
import java.io.IOException;

public class ChangeDetector implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(ChangeDetector.class);

  private final Blame blameSensor;
  private final Sha1Generator sha1Generator;
  private final FileToResource fileToResource;

  public ChangeDetector(Blame blameSensor, Sha1Generator sha1Generator, FileToResource fileToResource) {
    this.blameSensor = blameSensor;
    this.sha1Generator = sha1Generator;
    this.fileToResource = fileToResource;
  }

  public MeasureUpdate detectChange(InputFile inputFile, SensorContext context, String previousSha1) {
    File file = inputFile.getFile();
    try {
      Resource resource = fileToResource.toResource(inputFile, context);

      String currentSha1 = sha1Generator.sha1(file);

      if (currentSha1.equals(previousSha1)) {
        return fileNotChanged(file, resource);
      } else {
        return fileChanged(file, resource, currentSha1);
      }
    } catch (IOException e) {
      LOG.error("Unable to get scm information: " + file, e);
      return MeasureUpdate.NONE;
    }
  }

  private MeasureUpdate fileChanged(File file, Resource resource, String currentSha1) {
    LOG.debug("File changed since previous analysis: " + file);

    return blameSensor.save(file, resource, currentSha1);
  }

  private MeasureUpdate fileNotChanged(File file, Resource resource) {
    LOG.debug("File not changed since previous analysis: " + file);

    return new CopyPreviousMeasures(resource);
  }
}
