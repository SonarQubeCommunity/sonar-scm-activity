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
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Scopes;
import org.sonar.api.test.IsMeasure;
import org.sonar.api.test.IsResource;
import org.sonar.plugins.scmactivity.test.TemporaryFile;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class BlameTest {
  private static final String FILENAME = "source.java";

  Blame blame;

  SonarScmRepository sonarScmRepository = mock(SonarScmRepository.class);
  ScmManager scmManager = mock(ScmManager.class);
  SensorContext context = mock(SensorContext.class);

  @ClassRule
  public static TemporaryFile temporaryFile = new TemporaryFile();

  @Before
  public void setUp() {
    blame = new Blame(scmManager, sonarScmRepository);
  }

  @Test
  public void should_save_blame_by_line() throws Exception {
    when(scmManager.blame(any(ScmRepository.class), any(ScmFileSet.class), anyString()))
        .thenReturn(new BlameScmResult("fake", Arrays.asList(
            new BlameLine(new Date(13), "2", "godin"),
            new BlameLine(new Date(10), "1", "godin"))));

    File file = temporaryFile.create(FILENAME, "foo");
    blame.save(file, new org.sonar.api.resources.File(FILENAME), context);

    verify(context).saveMeasure(
        argThat(new IsResource(Scopes.FILE, Qualifiers.FILE, FILENAME)),
        argThat(new IsMeasure(CoreMetrics.SCM_AUTHORS_BY_LINE)));
    verify(context).saveMeasure(
        argThat(new IsResource(Scopes.FILE, Qualifiers.FILE, FILENAME)),
        argThat(new IsMeasure(CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE)));
    verify(context).saveMeasure(
        argThat(new IsResource(Scopes.FILE, Qualifiers.FILE, FILENAME)),
        argThat(new IsMeasure(CoreMetrics.SCM_REVISIONS_BY_LINE)));
    verifyNoMoreInteractions(context);
  }

  /**
   * See SONARPLUGINS-368 - can occur with generated sources
   * @throws ScmException 
   */
  @Test
  public void should_not_throw_scm_exception() throws ScmException {
    when(scmManager.blame(any(ScmRepository.class), any(ScmFileSet.class), anyString()))
        .thenThrow(new ScmException("ERROR"));

    blame.save(new File("src/UNKNOWN"), new org.sonar.api.resources.File("UNKNOWN"), context);

    verifyZeroInteractions(context);
  }

  @Test
  public void should_not_save_measures_if_blame_is_unsuccessful() throws ScmException {
    when(scmManager.blame(any(ScmRepository.class), any(ScmFileSet.class), anyString()))
        .thenReturn(new BlameScmResult("", "", "", false));

    blame.save(new File("src/UNKNOWN"), new org.sonar.api.resources.File("UNKNOWN"), context);

    verifyZeroInteractions(context);
  }
}
