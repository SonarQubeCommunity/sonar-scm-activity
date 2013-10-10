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
import org.sonar.api.scan.filesystem.InputFile;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.util.Arrays;
import java.util.Collections;
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
    when(fs.inputFiles(any(FileQuery.class))).thenReturn(Collections.<InputFile> emptyList());
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
    InputFile source = mock(InputFile.class);
    InputFile test = mock(InputFile.class);
    when(conf.getThreadCount()).thenReturn(1);
    when(fs.inputFiles(any(FileQuery.class))).thenReturn(Arrays.asList(source)).thenReturn(Arrays.asList(test));
    when(blameVersionSelector.select(source, context))
      .thenReturn(measureUpdate);
    when(blameVersionSelector.select(test, context))
      .thenReturn(measureUpdate);

    scmActivitySensor.analyse(project, context);

    verify(measureUpdate, times(2)).execute(timeMachine, context);
  }

  @Test
  public void should_carry_on_after_error() throws Exception {
    InputFile source1 = mock(InputFile.class);
    InputFile source2 = mock(InputFile.class);
    when(conf.getThreadCount()).thenReturn(1);
    when(fs.inputFiles(any(FileQuery.class))).thenReturn(Arrays.asList(source1, source2)).thenReturn(Arrays.<InputFile> asList());
    when(blameVersionSelector.select(source1, context))
      .thenThrow(new RuntimeException("BUG"));
    when(blameVersionSelector.select(source2, context))
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
