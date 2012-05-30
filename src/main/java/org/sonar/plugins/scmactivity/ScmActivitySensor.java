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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.TimeProfiler;

import java.io.IOException;
import java.util.List;

public final class ScmActivitySensor implements Sensor {
  private static final Logger LOG = LoggerFactory.getLogger(ScmActivitySensor.class);

  private final ScmActivityBlame scmActivityBlame;
  private final ScmConfiguration conf;
  private final UrlChecker urlChecker;
  private final LocalModificationChecker checkLocalModifications;
  private final ProjectFileSystem projectFileSystem;

  public ScmActivitySensor(ScmActivityBlame scmActivityBlame, ScmConfiguration conf, UrlChecker urlChecker, LocalModificationChecker checkLocalModifications,
      ProjectFileSystem projectFileSystem) {
    this.scmActivityBlame = scmActivityBlame;
    this.conf = conf;
    this.urlChecker = urlChecker;
    this.checkLocalModifications = checkLocalModifications;
    this.projectFileSystem = projectFileSystem;
  }

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return ImmutableList.of(
        ScmActivityMetrics.SCM_HASH,
        CoreMetrics.SCM_AUTHORS_BY_LINE,
        CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE,
        CoreMetrics.SCM_REVISIONS_BY_LINE);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return conf.isEnabled();
  }

  public void analyse(Project project, SensorContext context) {
    urlChecker.check();
    checkLocalModifications.check();

    TimeProfiler profiler = new TimeProfiler().start("Retrieve files SCM info");

    for (InputFile inputFile : allFiles(projectFileSystem)) {
      try {
        scmActivityBlame.storeBlame(inputFile, context);
      } catch (IOException e) {
        LOG.debug("Unable to get scm information: " + inputFile.getFile());
      }
    }

    profiler.stop();
  }

  private static Iterable<InputFile> allFiles(ProjectFileSystem fileSystem) {
    List<InputFile> mainFiles = fileSystem.mainFiles(Java.KEY);
    List<InputFile> testFiles = fileSystem.testFiles(Java.KEY);
    return Iterables.concat(mainFiles, testFiles);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
