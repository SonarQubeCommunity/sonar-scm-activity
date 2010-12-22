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
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.batch.TimeMachineQuery;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.Logs;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensor implements Sensor {

  private ScmConfiguration scmConfiguration;
  private TimeMachine timeMachine;

  public ScmActivitySensor(ScmConfiguration scmConfiguration, TimeMachine timeMachine) {
    this.scmConfiguration = scmConfiguration;
    this.timeMachine = timeMachine;
  }

  public boolean shouldExecuteOnProject(Project project) {
    // this sensor is executed only for latest analysis and if plugin enabled and scm connection is defined
    return project.isLatestAnalysis() && scmConfiguration.isEnabled() &&
        !StringUtils.isBlank(scmConfiguration.getUrl());
  }

  public void analyse(Project project, SensorContext context) {
    // Determine modified files
    ProjectStatus projectStatus = new ProjectStatus(project);

    String startRevision = getPreviousRevision(project);
    ChangeLogSet changeLog = getChangeLog(startRevision, project.getFileSystem().getBasedir());
    projectStatus.analyzeChangeLog(changeLog);

    for (File file : projectStatus.getFiles()) {
      if (projectStatus.isModified(file)) {
        Logs.INFO.info("File {} has been modified since previous analysis", file);
      }
    }

    // Analyze blame
    ProjectFileSystem fileSystem = project.getFileSystem();
    List<File> sourceDirs = fileSystem.getSourceDirs();

    BlameSensor blameSensor = new BlameSensor(scmConfiguration, context);

    Collection<File> files = projectStatus.getFiles();
    for (File file : files) {
      Resource resource = JavaFile.fromIOFile(file, sourceDirs, false);

      if (projectStatus.isModified(file)) {
        blameSensor.analyse(file, resource);
      } else {
        List<Measure> pastMeasures = getPastMeasures(resource, generatesMetrics());
        if (pastMeasures.isEmpty()) {
          // File not modified, but no past measures
          blameSensor.analyse(file, resource);
        } else {
          for (Measure measure : pastMeasures) {
            // TODO PersistenceMode.DATABASE
            context.saveMeasure(resource, new Measure(measure.getMetric(), measure.getData()));
          }
        }
      }
    }

    // TODO save correct revision for project
    // context.saveMeasure(project, new Measure(ScmActivityMetrics.REVISION, projectStatus.getRevision()));
    // context.saveMeasure(project, new Measure(ScmActivityMetrics.LAST_ACTIVITY, ScmUtils.formatLastActivity(projectStatus.getDate())));
  }

  private List<Measure> getPastMeasures(Resource resource, Metric... metrics) {
    TimeMachineQuery query = new TimeMachineQuery(resource)
        .setOnlyLastAnalysis(true)
        .setMetrics(generatesMetrics());
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

  /**
   * TODO BASE should be correctly handled by providers other than SvnExe
   * TODO {@link SvnJavaScmProvider} doesn't support revisions range
   * 
   * @return changes that have happened between <code>startVersion</code> and BASE
   */
  private ChangeLogSet getChangeLog(String startRevision, File basedir) {
    ScmManager scmManager = scmConfiguration.getScmManager();
    ScmRepository repository = scmConfiguration.getScmRepository();
    ChangeLogScmResult changeLogScmResult;
    try {
      changeLogScmResult = scmManager.changeLog(
          repository,
          new ScmFileSet(basedir),
          startRevision == null ? null : new ScmRevision(startRevision),
          new ScmRevision("BASE"));
    } catch (ScmException e) {
      throw new SonarException(e);
    }
    if (changeLogScmResult.isSuccess()) {
      return changeLogScmResult.getChangeLog();
    } else {
      throw new SonarException(changeLogScmResult.getCommandOutput());
    }
  }

  private Metric[] generatesMetrics() {
    return new Metric[] {
        ScmActivityMetrics.REVISION,
        ScmActivityMetrics.LAST_ACTIVITY,
        ScmActivityMetrics.BLAME_AUTHORS_DATA,
        ScmActivityMetrics.BLAME_DATE_DATA,
        ScmActivityMetrics.BLAME_REVISION_DATA };
  }

  protected Logger getLog() {
    return LoggerFactory.getLogger(getClass());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
