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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsMeasure;
import org.sonar.api.test.IsResource;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.sonar.api.resources.Resource.QUALIFIER_CLASS;
import static org.sonar.api.resources.Resource.SCOPE_ENTITY;

/**
 * @author Evgeny Mandrikov
 */
public class BlameSensorTest {
  private static final String RESOURCE_KEY = "org.example.HelloWorld";

  private ProjectScmManager scmManager;
  private SensorContext context;
  private BlameSensor sensor;

  @Before
  public void setUp() {
    scmManager = mock(ProjectScmManager.class);
    context = mock(SensorContext.class);
    sensor = spy(new BlameSensor(scmManager, context));
  }

  /**
   * See SONARPLUGINS-368
   * 
   * @throws Exception if something wrong
   */
  @Test
  public void testScmException() throws Exception {
    doThrow(new ScmException("ERROR"))
        .when(sensor)
        .analyseBlame(any(File.class), anyString(), any(Resource.class));

    sensor.analyse(new File("."), new JavaFile(RESOURCE_KEY));

    verifyNoMoreInteractions(context);
  }

  @Test
  public void testAnalyse() throws Exception {
    when(scmManager.getBlame(any(File.class), anyString()))
        .thenReturn(new BlameScmResult("fake", Arrays.asList(
                new BlameLine(new Date(13), "2", "godin"),
                new BlameLine(new Date(10), "1", "godin"))));

    sensor.analyseBlame(new File("."), "HelloWorld.java", new JavaFile(RESOURCE_KEY));

    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.LAST_ACTIVITY, ScmUtils.formatLastActivity(new Date(13)))));
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.REVISION, "2")));
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.BLAME_AUTHORS_DATA)));
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.BLAME_DATE_DATA)));
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.BLAME_REVISION_DATA)));
    verifyNoMoreInteractions(context);
  }

  @Test
  public void test() throws Exception {
    when(scmManager.getBlame(any(File.class), anyString()))
        .thenReturn(new BlameScmResult("command", "Provider message", "output", false));

    try {
      sensor.analyseBlame(new File("."), "HelloWorld.java", new JavaFile(RESOURCE_KEY));
    } catch (ScmException e) {
      Assert.assertThat(e.getMessage(), is("Provider message"));
    }
    verifyNoMoreInteractions(context);
  }
}
