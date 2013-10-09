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
import com.google.common.collect.Lists;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.TimeProfiler;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ScmActivitySensor implements Sensor {
  private static final Logger LOG = LoggerFactory.getLogger(ScmActivitySensor.class);

  private final ScmConfiguration configuration;
  private final BlameVersionSelector blameVersionSelector;
  private final UrlChecker urlChecker;
  private final FileToResource fileToResource;
  private final PreviousSha1Finder previousSha1Finder;
  private final TimeMachine timeMachine;

  public ScmActivitySensor(ScmConfiguration configuration, BlameVersionSelector blameVersionSelector, UrlChecker urlChecker, FileToResource fileToResource,
      PreviousSha1Finder previousSha1Finder, TimeMachine timeMachine) {
    this.configuration = configuration;
    this.blameVersionSelector = blameVersionSelector;
    this.urlChecker = urlChecker;
    this.fileToResource = fileToResource;
    this.previousSha1Finder = previousSha1Finder;
    this.timeMachine = timeMachine;
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
    return configuration.isEnabled() && project.isLatestAnalysis();
  }

  public void analyse(Project project, final SensorContext context) {
    urlChecker.check(configuration.getUrl());

    TimeProfiler profiler = new TimeProfiler().start("Retrieve SCM blame information with encoding " + Charset.defaultCharset());

    // Use multiple threads for the change detection and the blame retrieval
    // However all measures read/write should be done on main thread
    //
    ExecutorService executor = createExecutor();

    List<Future<MeasureUpdate>> updates = Lists.newArrayList();
    collect(updates, context, allFiles(project), executor);
    execute(updates, context);

    executor.shutdown();

    profiler.stop();
  }

  private void collect(List<Future<MeasureUpdate>> updates, final SensorContext context, Iterable<InputFile> allFiles, ExecutorService executor) {
    for (final InputFile inputFile : allFiles) {
      Resource resource = fileToResource.toResource(inputFile, context);

      if (resource == null) {
        LOG.debug("File not found in Sonar index: {}", inputFile.getFile());
      } else {
        final String previousSha1 = previousSha1Finder.find(resource);

        updates.add(executor.submit(new Callable<MeasureUpdate>() {
          public MeasureUpdate call() {
            return blameVersionSelector.detect(inputFile, previousSha1, context);
          }
        }));
      }
    }
  }

  private void execute(List<Future<MeasureUpdate>> updates, SensorContext context) {
    for (Future<MeasureUpdate> update : updates) {
      try {
        MeasureUpdate measureUpdate = update.get();
        measureUpdate.execute(timeMachine, context);
      } catch (Exception e) {
        LOG.error("Failure during SCM blame retrieval", ExceptionUtils.getRootCause(e));
      }
    }
  }

  private ExecutorService createExecutor() {
    return Executors.newFixedThreadPool(configuration.getThreadCount());
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
