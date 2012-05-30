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
import org.sonar.api.batch.TimeMachineQuery;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsMeasure;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class ScmActivityBlameTest {
  ScmActivityBlame scmActivityBlame;
  Blame blameSensor = mock(Blame.class);
  TimeMachine timeMachine = mock(TimeMachine.class);
  ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
  Sha1Generator sha1Generator = mock(Sha1Generator.class);
  PreviousSha1Finder previousSha1Finder = mock(PreviousSha1Finder.class);
  InputFile inputFile = mock(InputFile.class);
  SensorContext context = mock(SensorContext.class);
  Resource resource = mock(Resource.class);

  @Before
  public void setUp() {
    scmActivityBlame = new ScmActivityBlame(blameSensor, timeMachine, projectFileSystem, sha1Generator, previousSha1Finder);
  }

  @Test
  public void should_store_hash() throws IOException {
    File file = new File("source.java");
    when(inputFile.getFile()).thenReturn(file);
    when(projectFileSystem.toResource(file)).thenReturn(resource);
    when(context.getResource(resource)).thenReturn(resource);
    when(sha1Generator.sha1(file)).thenReturn("SHA1");

    scmActivityBlame.storeBlame(inputFile, context);

    verify(context).saveMeasure(same(resource), argThat(new IsMeasure(ScmActivityMetrics.SCM_HASH, "SHA1")));
  }

  @Test
  public void should_save_blame_when_hashes_are_different() throws IOException {
    File file = new File("source.java");
    when(inputFile.getFile()).thenReturn(file);
    when(projectFileSystem.toResource(file)).thenReturn(resource);
    when(context.getResource(resource)).thenReturn(resource);
    when(sha1Generator.sha1(file)).thenReturn("SHA1");
    when(previousSha1Finder.previousSha1(resource)).thenReturn("OLD SHA1");

    scmActivityBlame.storeBlame(inputFile, context);

    verify(blameSensor).save(file, resource, context);
  }

  @Test
  public void should_not_save_blame_when_hashes_are_the_same() throws IOException {
    File file = new File("source.java");
    when(inputFile.getFile()).thenReturn(file);
    when(projectFileSystem.toResource(file)).thenReturn(resource);
    when(context.getResource(resource)).thenReturn(resource);
    when(sha1Generator.sha1(file)).thenReturn("SHA1");
    when(previousSha1Finder.previousSha1(resource)).thenReturn("SHA1");

    scmActivityBlame.storeBlame(inputFile, context);

    verify(blameSensor, never()).save(file, resource, context);
  }

  @Test
  public void should_ignore_unknown_file() throws IOException {
    File file = new File("source.java");
    when(inputFile.getFile()).thenReturn(file);
    when(projectFileSystem.toResource(file)).thenReturn(resource);
    when(context.getResource(resource)).thenReturn(null);

    scmActivityBlame.storeBlame(inputFile, context);

    verifyZeroInteractions(blameSensor, sha1Generator, previousSha1Finder);
  }

  @Test
  public void should_copy_old_measures_when_hashes_are_the_same() throws IOException {
    File file = new File("source.java");
    when(inputFile.getFile()).thenReturn(file);
    when(projectFileSystem.toResource(file)).thenReturn(resource);
    when(context.getResource(resource)).thenReturn(resource);
    when(sha1Generator.sha1(file)).thenReturn("SHA1");
    when(previousSha1Finder.previousSha1(resource)).thenReturn("SHA1");
    when(timeMachine.getMeasures(any(TimeMachineQuery.class))).thenReturn(Arrays.asList(
        new Measure(CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE, "Measure1"),
        new Measure(CoreMetrics.SCM_REVISIONS_BY_LINE, "Measure2"),
        new Measure(CoreMetrics.SCM_AUTHORS_BY_LINE, "Measure3")));

    scmActivityBlame.storeBlame(inputFile, context);

    verify(context).saveMeasure(same(resource), argThat(new IsMeasure(CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE, "Measure1")));
    verify(context).saveMeasure(same(resource), argThat(new IsMeasure(CoreMetrics.SCM_REVISIONS_BY_LINE, "Measure2")));
    verify(context).saveMeasure(same(resource), argThat(new IsMeasure(CoreMetrics.SCM_AUTHORS_BY_LINE, "Measure3")));
  }
}
