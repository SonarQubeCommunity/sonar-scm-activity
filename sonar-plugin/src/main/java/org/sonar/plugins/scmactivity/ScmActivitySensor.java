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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.batch.TimeMachineQuery;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.Logs;
import org.sonar.plugins.scmactivity.ProjectStatus.FileStatus;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensor implements Sensor {

  private ScmConfiguration scmConfiguration;
  private TimeMachine timeMachine;
  private ProjectScmManager scmManager;

  public ScmActivitySensor(ScmConfiguration scmConfiguration, ProjectScmManager scmManager, TimeMachine timeMachine) {
    this.scmConfiguration = scmConfiguration;
    this.scmManager = scmManager;
    this.timeMachine = timeMachine;
  }

  public boolean shouldExecuteOnProject(Project project) {
    // this sensor is executed only for latest analysis and if plugin enabled and scm connection is defined
    return project.isLatestAnalysis() && scmConfiguration.isEnabled() && !StringUtils.isBlank(scmConfiguration.getUrl());
  }

  public void analyse(Project project, SensorContext context) {
    scmManager.checkLocalModifications();

    // Determine modified files
    ProjectStatus projectStatus = new ProjectStatus(project);

    String startRevision = getPreviousRevision(project);
    ChangeLogSet changeLog = scmManager.getChangeLog(startRevision);
    List<ChangeSet> sets = changeLog.getChangeSets();
    for (ChangeSet changeSet : sets) {
      // startRevision already was analyzed in previous Sonar run
      // Git excludes this revision from changelog, but Subversion not
      if (!StringUtils.equals(startRevision, changeSet.getRevision())) {
        projectStatus.analyzeChangeSet(changeSet);
      }
    }

    // Analyze blame
    ProjectFileSystem fileSystem = project.getFileSystem();
    List<File> sourceDirs = fileSystem.getSourceDirs();

    BlameSensor blameSensor = new BlameSensor(scmManager, context);

    Collection<File> files = projectStatus.getFiles();
    for (File file : files) {
      FileStatus fileStatus = projectStatus.getFileStatus(file);
      Resource resource = JavaFile.fromIOFile(file, sourceDirs, false);

      if (fileStatus.isModified()) {
        Logs.INFO.info("File {} has been modified since previous analysis", file);
        blameSensor.analyse(file, resource);

        // TODO SONARPLUGINS-877: save more accurate values for file
        // context.saveMeasure(resource, new Measure(ScmActivityMetrics.REVISION, fileStatus.getRevision()));
        // context.saveMeasure(resource, new Measure(ScmActivityMetrics.LAST_ACTIVITY, ScmUtils.formatLastActivity(fileStatus.getDate())));
      } else {
        List<Measure> pastMeasures = getPastMeasures(resource, generatesMetrics());
        if (pastMeasures.isEmpty()) {
          // File not modified, but no past measures
          blameSensor.analyse(file, resource);
        } else {
          resave(resource, context, pastMeasures);
        }
      }
    }

    if (projectStatus.isModified()) {
      context.saveMeasure(project, new Measure(ScmActivityMetrics.REVISION, projectStatus.getRevision()));
      context.saveMeasure(project, new Measure(ScmActivityMetrics.LAST_ACTIVITY, ScmUtils.formatLastActivity(projectStatus.getDate())));
    } else {
      resave(project, context, getPastMeasures(project, generatesMetrics()));
    }
  }

  private void resave(Resource resource, SensorContext context, List<Measure> pastMeasures) {
    for (Measure pastMeasure : pastMeasures) {
      Metric metric = pastMeasure.getMetric();
      Measure measure = new Measure(metric, pastMeasure.getData());
      if (metric.equals(ScmActivityMetrics.BLAME_AUTHORS_DATA)
          || metric.equals(ScmActivityMetrics.BLAME_DATE_DATA)
          || metric.equals(ScmActivityMetrics.BLAME_REVISION_DATA)) {
        measure.setPersistenceMode(PersistenceMode.DATABASE);
      }
      context.saveMeasure(resource, measure);
    }
  }

  private List<Measure> getPastMeasures(Resource resource, Metric... metrics) {
    TimeMachineQuery query = new TimeMachineQuery(resource)
        .setOnlyLastAnalysis(true)
        .setMetrics(metrics);
    return timeMachine.getMeasures(query);
  }

  private String getPreviousRevision(Project project) {
    List<Measure> measures = getPastMeasures(project, ScmActivityMetrics.REVISION);
    if (measures.isEmpty()) {
      // First analysis
      return null;
    }
    return measures.get(0).getData();
  }

  private Metric[] generatesMetrics() {
    return new Metric[] {
        ScmActivityMetrics.REVISION,
        ScmActivityMetrics.LAST_ACTIVITY,
        ScmActivityMetrics.BLAME_AUTHORS_DATA,
        ScmActivityMetrics.BLAME_DATE_DATA,
        ScmActivityMetrics.BLAME_REVISION_DATA };
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
