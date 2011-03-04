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

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.test.IsMeasure;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * @author Evgeny Mandrikov
 */
public class ProjectActivityDecoratorTest {
  private ProjectActivityDecorator decorator;
  private DecoratorContext context;

  @Before
  public void setUp() {
    decorator = new ProjectActivityDecorator();
  }

  @Test
  public void testGeneratesMetrics() {
    assertThat(decorator.generatesMetrics().size(), is(2));
  }

  @Test
  public void testDecorate() {
    DecoratorContext context = mock(DecoratorContext.class);
    List<DecoratorContext> children = Arrays.asList(
        mockChildContext("2010-01-02", "3"),
        mockChildContext("2010-01-01", "1"),
        mockChildContext("2010-01-01", "2"));
    when(context.getChildren()).thenReturn(children);
    decorator.decorate(new Project(""), context);
    verify(context).saveMeasure(argThat(new IsMeasure(CoreMetrics.SCM_LAST_COMMIT_DATE, "2010-01-02")));
  }

  @Test
  public void testNoData() {
    DecoratorContext context = mock(DecoratorContext.class);
    List<DecoratorContext> children = Arrays.asList(
        mockChildContext(null, null),
        mockChildContext("2010-01-02", "1"));
    when(context.getChildren()).thenReturn(children);
    decorator.decorate(new Project("").setPom(new MavenProject()), context);
    verify(context).saveMeasure(argThat(new IsMeasure(CoreMetrics.SCM_LAST_COMMIT_DATE, "2010-01-02")));

    reset(context);
    children = Arrays.asList(mockChildContext(null, null), mockChildContext(null, null));
    when(context.getChildren()).thenReturn(children);
    decorator.decorate(new Project("").setPom(new MavenProject()), context);
    verify(context, never()).saveMeasure((Measure) any());
  }

  private DecoratorContext mockChildContext(String lastActivity, String revision) {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.SCM_LAST_COMMIT_DATE))
        .thenReturn(new Measure(CoreMetrics.SCM_LAST_COMMIT_DATE, lastActivity));
    when(context.getMeasure(CoreMetrics.SCM_REVISION))
        .thenReturn(new Measure(CoreMetrics.SCM_REVISION, revision));
    return context;
  }
}
