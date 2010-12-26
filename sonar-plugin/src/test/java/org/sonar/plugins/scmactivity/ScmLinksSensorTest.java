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
import org.sonar.api.resources.Project;
import org.sonar.api.test.IsMeasure;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ScmLinksSensorTest {

  private ScmConfiguration scmConfiguration;
  private ScmLinksSensor sensor;
  private Project project;
  private SensorContext context;

  @Before
  public void setUp() {
    project = mock(Project.class);
    context = mock(SensorContext.class);
    scmConfiguration = mock(ScmConfiguration.class);
    sensor = new ScmLinksSensor(scmConfiguration);
  }

  @Test
  public void shouldNotExecute() {
    when(scmConfiguration.getBrowserUrlTemplate()).thenReturn(null).thenReturn("");
    assertThat(sensor.shouldExecuteOnProject(project), is(false));
    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void shouldExecute() {
    when(scmConfiguration.getBrowserUrlTemplate()).thenReturn("http://example");
    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void shouldSaveMeasure() {
    Project project = mock(Project.class);
    when(scmConfiguration.getBrowserUrlTemplate()).thenReturn("http://example");
    sensor.analyse(project, context);
    verify(context).saveMeasure(argThat(new IsMeasure(ScmActivityMetrics.BROWSER, "http://example")));
    verifyNoMoreInteractions(context);
  }

  @Test
  public void testToString() {
    assertThat(sensor.toString(), is("ScmLinksSensor"));
  }

}
