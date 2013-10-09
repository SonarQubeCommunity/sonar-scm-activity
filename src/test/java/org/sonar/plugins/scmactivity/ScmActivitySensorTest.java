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
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScmActivitySensorTest {
  ScmActivitySensor scmActivitySensor;

  BlameVersionSelector blameVersionSelector = mock(BlameVersionSelector.class);
  ScmConfiguration conf = mock(ScmConfiguration.class);
  UrlChecker urlChecker = mock(UrlChecker.class);
  ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
  Project project = mock(Project.class);
  SensorContext context = mock(SensorContext.class);
  FileToResource fileToResource = mock(FileToResource.class);
  PreviousSha1Finder previousSha1Finder = mock(PreviousSha1Finder.class);
  TimeMachine timeMachine = mock(TimeMachine.class);
  Resource resource = mock(Resource.class);
  MeasureUpdate measureUpdate = mock(MeasureUpdate.class);

  @Before
  public void setUp() {
    scmActivitySensor = new ScmActivitySensor(conf, blameVersionSelector, urlChecker, fileToResource, previousSha1Finder, timeMachine);
  }

  @Test
  public void should_execute() {
    when(conf.isEnabled()).thenReturn(true);
    when(project.isLatestAnalysis()).thenReturn(true);

    boolean shouldExecute = scmActivitySensor.shouldExecuteOnProject(project);

    assertThat(shouldExecute).isTrue();
  }

  @Test
  public void should_not_execute_if_disabled() {
    when(conf.isEnabled()).thenReturn(false);
    when(project.isLatestAnalysis()).thenReturn(true);

    boolean shouldExecute = scmActivitySensor.shouldExecuteOnProject(project);

    assertThat(shouldExecute).isFalse();
  }

  @Test
  public void should_not_execute_if_not_latest_analysis() {
    when(conf.isEnabled()).thenReturn(true);
    when(project.isLatestAnalysis()).thenReturn(false);

    boolean shouldExecute = scmActivitySensor.shouldExecuteOnProject(project);

    assertThat(shouldExecute).isFalse();
  }

  @Test
  public void should_generate_metrics() {
    List<Metric> metrics = scmActivitySensor.generatesMetrics();

    assertThat(metrics).hasSize(4);
  }

  @Test(timeout = 2000)
  public void should_check_url() {
    when(conf.getThreadCount()).thenReturn(1);
    when(project.getLanguageKey()).thenReturn("java");
    when(project.getFileSystem()).thenReturn(projectFileSystem);
    when(conf.getUrl()).thenReturn("scm:url");

    scmActivitySensor.analyse(project, context);

    verify(urlChecker).check("scm:url");
  }

  @Test
  public void should_execute_measure_update_for_known_files() {
    InputFile source = file("source.java");
    InputFile test = file("UNKNOWN.java");
    when(conf.getThreadCount()).thenReturn(1);
    when(project.getLanguageKey()).thenReturn("java");
    when(project.getFileSystem()).thenReturn(projectFileSystem);
    when(projectFileSystem.mainFiles("java")).thenReturn(Arrays.asList(source));
    when(projectFileSystem.testFiles("java")).thenReturn(Arrays.asList(test));
    when(fileToResource.toResource(source, context)).thenReturn(resource);
    when(previousSha1Finder.find(resource)).thenReturn("SHA1");
    when(blameVersionSelector.detect(source, "SHA1", context)).thenReturn(measureUpdate);

    scmActivitySensor.analyse(project, context);

    verify(measureUpdate).execute(timeMachine, context);
  }

  @Test
  public void should_carry_on_after_error() {
    InputFile first = file("source.java");
    InputFile second = file("UNKNOWN.java");
    when(conf.getThreadCount()).thenReturn(1);
    when(project.getLanguageKey()).thenReturn("java");
    when(project.getFileSystem()).thenReturn(projectFileSystem);
    when(projectFileSystem.mainFiles("java")).thenReturn(Arrays.asList(first, second));
    when(fileToResource.toResource(first, context)).thenReturn(resource);
    when(fileToResource.toResource(second, context)).thenReturn(resource);
    when(previousSha1Finder.find(resource)).thenReturn("SHA1");
    when(blameVersionSelector.detect(first, "SHA1", context)).thenThrow(new RuntimeException("BUG"));
    when(blameVersionSelector.detect(second, "SHA1", context)).thenReturn(measureUpdate);

    scmActivitySensor.analyse(project, context);

    verify(measureUpdate).execute(timeMachine, context);
  }

  @Test
  public void should_have_debug_name() {
    String debugName = scmActivitySensor.toString();

    assertThat(debugName).isEqualTo("ScmActivitySensor");
  }

  static InputFile file(String name) {
    InputFile inputFile = mock(InputFile.class);
    when(inputFile.getFile()).thenReturn(new File(name));
    return inputFile;
  }
}
