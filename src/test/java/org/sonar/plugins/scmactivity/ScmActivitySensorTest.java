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
import org.mockito.InOrder;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScmActivitySensorTest {
  ScmActivitySensor scmActivitySensor;

  ScmActivityBlame scmActivityBlame = mock(ScmActivityBlame.class);
  ScmConfiguration conf = mock(ScmConfiguration.class);
  UrlChecker urlChecker = mock(UrlChecker.class);
  LocalModificationChecker checkLocalModifications = mock(LocalModificationChecker.class);
  ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
  Project project = mock(Project.class);
  SensorContext context = mock(SensorContext.class);

  @Before
  public void setUp() {
    scmActivitySensor = new ScmActivitySensor(conf, scmActivityBlame, urlChecker, checkLocalModifications);
  }

  @Test
  public void should_execute() {
    when(conf.isEnabled()).thenReturn(true);

    boolean shouldExecute = scmActivitySensor.shouldExecuteOnProject(project);

    assertThat(shouldExecute).isTrue();
  }

  @Test
  public void should_not_execute() {
    when(conf.isEnabled()).thenReturn(false);

    boolean shouldExecute = scmActivitySensor.shouldExecuteOnProject(project);

    assertThat(shouldExecute).isFalse();
  }

  @Test
  public void should_generate_metrics() {
    List<Metric> metrics = scmActivitySensor.generatesMetrics();

    assertThat(metrics).hasSize(4);
  }

  @Test
  public void should_execute_checks() {
    when(project.getLanguage()).thenReturn(new Java());
    when(project.getFileSystem()).thenReturn(projectFileSystem);

    scmActivitySensor.analyse(project, context);

    InOrder inOrder = inOrder(urlChecker, checkLocalModifications);
    inOrder.verify(urlChecker).check();
    inOrder.verify(checkLocalModifications).check();
  }

  @Test
  public void should_get_blame_information() {
    InputFile source = file("source.java");
    InputFile test = file("test.java");
    when(project.getLanguage()).thenReturn(new Java());
    when(project.getFileSystem()).thenReturn(projectFileSystem);
    when(projectFileSystem.mainFiles("java")).thenReturn(Arrays.asList(source));
    when(projectFileSystem.testFiles("java")).thenReturn(Arrays.asList(test));

    scmActivitySensor.analyse(project, context);

    verify(scmActivityBlame).storeBlame(new File("source.java"), context);
    verify(scmActivityBlame).storeBlame(new File("test.java"), context);
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
