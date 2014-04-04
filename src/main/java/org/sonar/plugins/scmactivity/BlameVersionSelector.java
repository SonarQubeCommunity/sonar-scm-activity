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
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.batch.TimeMachineQuery;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Resource;

import java.io.File;
import java.util.List;

public class BlameVersionSelector implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(BlameVersionSelector.class);

  private final Blame blame;
  private final TimeMachine timeMachine;

  public BlameVersionSelector(Blame blame, TimeMachine timeMachine) {
    this.blame = blame;
    this.timeMachine = timeMachine;
  }

  public MeasureUpdate detect(Resource sonarFile, InputFile inputFile, SensorContext context) {
    File file = inputFile.file();

    if (inputFile.status() == InputFile.Status.SAME && hasScmMeasuresOnPreviousAnalysis(sonarFile)) {
      return fileNotChanged(file, sonarFile);
    }

    return fileChanged(file, sonarFile, inputFile.lines());
  }

  private boolean hasScmMeasuresOnPreviousAnalysis(Resource resource) {
    List<Measure> measures = timeMachine.getMeasures(queryPreviousMeasure(resource));
    if (measures.isEmpty()) {
      return false;
    }

    return true;
  }

  private static TimeMachineQuery queryPreviousMeasure(Resource resource) {
    return new TimeMachineQuery(resource)
      .setMetrics(CoreMetrics.SCM_AUTHORS_BY_LINE)
      .setOnlyLastAnalysis(true);
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
