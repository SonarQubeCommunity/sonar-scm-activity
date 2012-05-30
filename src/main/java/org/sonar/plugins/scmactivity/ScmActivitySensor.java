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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.TimeProfiler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ScmActivitySensor implements Sensor {
  private static final Logger LOG = LoggerFactory.getLogger(ScmActivitySensor.class);

  private final ScmConfiguration configuration;
  private final ScmActivityBlame scmActivityBlame;
  private final UrlChecker urlChecker;
  private final LocalModificationChecker checkLocalModifications;

  public ScmActivitySensor(ScmConfiguration configuration, ScmActivityBlame scmActivityBlame, UrlChecker urlChecker, LocalModificationChecker checkLocalModifications) {
    this.configuration = configuration;
    this.scmActivityBlame = scmActivityBlame;
    this.urlChecker = urlChecker;
    this.checkLocalModifications = checkLocalModifications;
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
    return configuration.isEnabled();
  }

  public void analyse(Project project, final SensorContext context) {
    urlChecker.check();
    checkLocalModifications.check();

    TimeProfiler profiler = new TimeProfiler().start("Retrieve SCM blame information");

    ExecutorService executor = Executors.newSingleThreadExecutor();

    for (final InputFile file : allFiles(project)) {
      executor.submit(new Runnable() {
        public void run() {
          scmActivityBlame.storeBlame(file.getFile(), context);
        }
      });
    }

    executor.shutdown();
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      LOG.error("Unable to await termination of blame retrieval process", e);
    }

    profiler.stop();
  }

  private static Iterable<InputFile> allFiles(Project project) {
    String language = project.getLanguageKey();
    ProjectFileSystem fileSystem = project.getFileSystem();

    return Iterables.concat(fileSystem.mainFiles(language), fileSystem.testFiles(language));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
