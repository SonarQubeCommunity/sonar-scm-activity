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

import org.sonar.api.measures.Metric;

import org.sonar.api.BatchExtension;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.batch.TimeMachineQuery;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.scmactivity.sha1.PreviousSha1Finder;
import org.sonar.plugins.scmactivity.sha1.Sha1Generator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScmActivityBlame implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(ScmActivityBlame.class);

  private final Blame blameSensor;
  private final TimeMachine timeMachine;
  private final ProjectFileSystem projectFileSystem;
  private final Sha1Generator sha1Generator;
  private final PreviousSha1Finder previousSha1Finder;

  public ScmActivityBlame(Blame blameSensor, TimeMachine timeMachine, ProjectFileSystem projectFileSystem, Sha1Generator sha1Generator, PreviousSha1Finder previousSha1Finder) {
    this.blameSensor = blameSensor;
    this.timeMachine = timeMachine;
    this.projectFileSystem = projectFileSystem;
    this.sha1Generator = sha1Generator;
    this.previousSha1Finder = previousSha1Finder;
  }

  public void storeBlame(InputFile inputFile, SensorContext context) throws IOException {
    File file = inputFile.getFile();

    Resource resource = context.getResource(projectFileSystem.toResource(file));
    if (resource == null) {
      LOG.debug("File not found in Sonar index: " + file);
      return;
    }

    String currentSha1 = sha1Generator.sha1(file);
    String previousSha1 = previousSha1Finder.previousSha1(resource);

    if (currentSha1.equals(previousSha1)) {
      LOG.debug("File not changed since previous analysis: " + file);

      copyPreviousFileMeasures(resource, context);
    } else {
      context.saveMeasure(resource, new Measure(ScmActivityMetrics.SCM_HASH, currentSha1).setPersistenceMode(PersistenceMode.DATABASE));

      blameSensor.save(file, resource, context);
    }
  }

  private void copyPreviousFileMeasures(Resource resource, SensorContext context) {
    List<Measure> previousMeasures = timeMachine.getMeasures(new TimeMachineQuery(resource).setOnlyLastAnalysis(true).setMetrics(
        CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE,
        CoreMetrics.SCM_REVISIONS_BY_LINE,
        CoreMetrics.SCM_AUTHORS_BY_LINE));

    for (Measure previousMeasure : previousMeasures) {
      String data = previousMeasure.getData();
      if (StringUtils.isNotBlank(data)) {
        Metric metric = previousMeasure.getMetric();
        context.saveMeasure(resource, new Measure(metric, data).setPersistenceMode(PersistenceMode.DATABASE));
      }
    }
  }
}
