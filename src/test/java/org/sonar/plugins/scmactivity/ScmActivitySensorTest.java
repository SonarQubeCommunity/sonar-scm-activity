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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScmActivitySensorTest {

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  ScmActivitySensor scmActivitySensor;

  BlameVersionSelector blameVersionSelector = mock(BlameVersionSelector.class);
  ScmConfiguration conf = mock(ScmConfiguration.class);
  UrlChecker urlChecker = mock(UrlChecker.class);
  ModuleFileSystem fs = mock(ModuleFileSystem.class);
  Project project = mock(Project.class);
  SensorContext context = mock(SensorContext.class);
  TimeMachine timeMachine = mock(TimeMachine.class);
  Resource resource = mock(Resource.class);
  MeasureUpdate measureUpdate = mock(MeasureUpdate.class);

  @Before
  public void setUp() {
    scmActivitySensor = new ScmActivitySensor(fs, conf, blameVersionSelector, urlChecker, timeMachine);
  }

  @Test
  public void should_execute() {
    when(conf.isEnabled()).thenReturn(true);

    boolean shouldExecute = scmActivitySensor.shouldExecuteOnProject(project);

    assertThat(shouldExecute).isTrue();
  }

  @Test
  public void should_not_execute_if_disabled() {
    when(conf.isEnabled()).thenReturn(false);

    boolean shouldExecute = scmActivitySensor.shouldExecuteOnProject(project);

    assertThat(shouldExecute).isFalse();
  }

  @Test
  public void should_generate_metrics() {
    List<Metric> metrics = scmActivitySensor.generatesMetrics();

    assertThat(metrics).hasSize(3);
  }

  @Test(timeout = 2000)
  public void should_check_url() {
    when(conf.getThreadCount()).thenReturn(1);
    when(project.getLanguageKey()).thenReturn("java");
    when(conf.getUrl()).thenReturn("scm:url");

    scmActivitySensor.analyse(project, context);

    verify(urlChecker).check("scm:url");
  }

  @Test
  public void should_collect_measure_updates_on_main_and_test_files() throws Exception {
    File sourceDir = temp.newFolder();
    File testDir = temp.newFolder();
    File source = new File(sourceDir, "source.java");
    File changedSource = new File(sourceDir, "source2.java");
    File test = new File(testDir, "test.java");
    File changedTest = new File(testDir, "test2.java");
    when(conf.getThreadCount()).thenReturn(1);
    when(project.getLanguageKey()).thenReturn("java");
    when(fs.sourceDirs()).thenReturn(Arrays.asList(sourceDir));
    when(fs.testDirs()).thenReturn(Arrays.asList(testDir));
    when(fs.files(any(FileQuery.class))).thenReturn(Arrays.asList(source, changedSource)).thenReturn(Arrays.asList(test, changedTest));
    when(fs.changedFiles(any(FileQuery.class))).thenReturn(Arrays.asList(changedSource)).thenReturn(Arrays.asList(changedTest));
    when(blameVersionSelector.select(source, context, Arrays.asList(changedSource), Arrays.asList(sourceDir), false))
      .thenReturn(measureUpdate);
    when(blameVersionSelector.select(changedSource, context, Arrays.asList(changedSource), Arrays.asList(sourceDir), false))
      .thenReturn(measureUpdate);
    when(blameVersionSelector.select(test, context, Arrays.asList(changedTest), Arrays.asList(testDir), true))
      .thenReturn(measureUpdate);
    when(blameVersionSelector.select(changedTest, context, Arrays.asList(changedTest), Arrays.asList(testDir), true))
      .thenReturn(measureUpdate);

    scmActivitySensor.analyse(project, context);

    verify(measureUpdate, times(4)).execute(timeMachine, context);
  }

  @Test
  public void should_carry_on_after_error() throws Exception {
    File sourceDir = temp.newFolder();
    File source = new File(sourceDir, "source.java");
    File changedSource = new File(sourceDir, "source2.java");
    when(conf.getThreadCount()).thenReturn(1);
    when(project.getLanguageKey()).thenReturn("java");
    when(fs.sourceDirs()).thenReturn(Arrays.asList(sourceDir));
    when(fs.testDirs()).thenReturn(Arrays.<File> asList());
    when(fs.files(any(FileQuery.class))).thenReturn(Arrays.asList(source, changedSource)).thenReturn(Arrays.<File> asList());
    when(fs.changedFiles(any(FileQuery.class))).thenReturn(Arrays.asList(changedSource)).thenReturn(Arrays.<File> asList());
    when(blameVersionSelector.select(source, context, Arrays.asList(changedSource), Arrays.asList(sourceDir), false))
      .thenThrow(new RuntimeException("BUG"));
    when(blameVersionSelector.select(changedSource, context, Arrays.asList(changedSource), Arrays.asList(sourceDir), false))
      .thenReturn(measureUpdate);

    scmActivitySensor.analyse(project, context);

    verify(measureUpdate).execute(timeMachine, context);
  }

  @Test
  public void should_have_debug_name() {
    String debugName = scmActivitySensor.toString();

    assertThat(debugName).isEqualTo("ScmActivitySensor");
  }
}
