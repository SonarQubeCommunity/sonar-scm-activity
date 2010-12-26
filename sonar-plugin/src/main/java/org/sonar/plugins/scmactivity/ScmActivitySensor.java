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
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensor implements Sensor {

  private TimeMachine timeMachine;
  private ProjectScmManager scmManager;

  public ScmActivitySensor(ProjectScmManager scmManager, TimeMachine timeMachine) {
    this.scmManager = scmManager;
    this.timeMachine = timeMachine;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return scmManager.isEnabled();
  }

  public void analyse(Project project, SensorContext context) {
    scmManager.checkLocalModifications();

    // Determine modified files
    ProjectStatus projectStatus = new ProjectStatus(project);

    String startRevision = getPreviousRevision(project);
    List<ChangeSet> changeLog = scmManager.getChangeLog(startRevision);
    for (ChangeSet changeSet : changeLog) {
      if (ScmUtils.fixChangeSet(changeSet)) {
        // startRevision already was analyzed in previous Sonar run
        // Git excludes this revision from changelog, but Subversion not
        if (!StringUtils.equals(startRevision, changeSet.getRevision())) {
          Logs.INFO.info("{} file(s) changed {} ({})", new Object[] {
              changeSet.getFiles().size(),
              changeSet.getDateFormatted() + " " + changeSet.getTimeFormatted(),
              changeSet.getRevision() });
          projectStatus.analyzeChangeSet(changeSet);
        }
      }
    }

    // Analyze blame
    ProjectFileSystem fileSystem = project.getFileSystem();
    List<File> sourceDirs = fileSystem.getSourceDirs();

    for (FileStatus fileStatus : projectStatus.getFiles()) {
      Resource resource = JavaFile.fromIOFile(fileStatus.getFile(), sourceDirs, false);
      saveFile(context, resource, fileStatus);
    }
    saveProject(context, project, projectStatus);
  }

  private void saveFile(SensorContext context, Resource resource, FileStatus fileStatus) {
    final String revision, date, dates, authors, revisions;
    File file = fileStatus.getFile();
    BlameSensor blameSensor = new BlameSensor(scmManager, context);

    if (fileStatus.isModified()) {
      Logs.INFO.info("File {} has been modified since previous analysis", file);
      blameSensor.analyse(file, resource);

      revision = fileStatus.getRevision();
      date = ScmUtils.formatLastActivity(fileStatus.getDate());
      dates = blameSensor.getDates();
      revisions = blameSensor.getRevisions();
      authors = blameSensor.getAuthors();
    } else {
      revision = getPastMeasureData(resource, ScmActivityMetrics.REVISION);
      if (revision == null) {
        // File not modified, but no past measures
        // This should happen only for generated sources
        return;
      }
      date = getPastMeasureData(resource, ScmActivityMetrics.LAST_ACTIVITY);
      dates = getPastMeasureData(resource, ScmActivityMetrics.BLAME_DATE_DATA);
      revisions = getPastMeasureData(resource, ScmActivityMetrics.BLAME_REVISION_DATA);
      authors = getPastMeasureData(resource, ScmActivityMetrics.BLAME_AUTHORS_DATA);
    }

    context.saveMeasure(resource, new Measure(ScmActivityMetrics.REVISION, revision));
    context.saveMeasure(resource, new Measure(ScmActivityMetrics.LAST_ACTIVITY, date));

    context.saveMeasure(resource, new Measure(ScmActivityMetrics.BLAME_DATE_DATA, dates)
        .setPersistenceMode(PersistenceMode.DATABASE));
    context.saveMeasure(resource, new Measure(ScmActivityMetrics.BLAME_REVISION_DATA, revisions)
        .setPersistenceMode(PersistenceMode.DATABASE));
    context.saveMeasure(resource, new Measure(ScmActivityMetrics.BLAME_AUTHORS_DATA, authors)
        .setPersistenceMode(PersistenceMode.DATABASE));
  }

  private void saveProject(SensorContext context, Project project, ProjectStatus projectStatus) {
    final String revision;
    final String date;
    if (!projectStatus.isModified()) {
      revision = projectStatus.getRevision();
      date = ScmUtils.formatLastActivity(projectStatus.getDate());
    } else {
      revision = getPastMeasureData(project, ScmActivityMetrics.REVISION);
      date = getPastMeasureData(project, ScmActivityMetrics.LAST_ACTIVITY);
    }
    Double commits = getPastMeasure(project, ScmActivityMetrics.COMMITS);
    if (commits == null) {
      commits = 0d;
    }
    commits += projectStatus.getChanges();

    context.saveMeasure(project, new Measure(ScmActivityMetrics.REVISION, revision));
    context.saveMeasure(project, new Measure(ScmActivityMetrics.LAST_ACTIVITY, date));
    context.saveMeasure(project, new Measure(ScmActivityMetrics.COMMITS, commits));
  }

  private Double getPastMeasure(Resource resource, Metric metric) {
    TimeMachineQuery query = new TimeMachineQuery(resource)
        .setOnlyLastAnalysis(true)
        .setMetrics(metric);
    List<Object[]> fields = timeMachine.getMeasuresFields(query);
    if (fields.isEmpty()) {
      return null;
    }
    return (Double) fields.get(0)[2];
  }

  private String getPastMeasureData(Resource resource, Metric metric) {
    TimeMachineQuery query = new TimeMachineQuery(resource)
        .setOnlyLastAnalysis(true)
        .setMetrics(metric);
    List<Measure> measures = timeMachine.getMeasures(query);
    if (measures.isEmpty()) {
      return null;
    }
    return measures.get(0).getData();
  }

  private String getPreviousRevision(Project project) {
    return getPastMeasureData(project, ScmActivityMetrics.REVISION);
  }

  private Metric[] generatesMetrics() {
    return new Metric[] {
        ScmActivityMetrics.REVISION,
        ScmActivityMetrics.LAST_ACTIVITY,
        ScmActivityMetrics.COMMITS,
        ScmActivityMetrics.BLAME_AUTHORS_DATA,
        ScmActivityMetrics.BLAME_DATE_DATA,
        ScmActivityMetrics.BLAME_REVISION_DATA };
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
