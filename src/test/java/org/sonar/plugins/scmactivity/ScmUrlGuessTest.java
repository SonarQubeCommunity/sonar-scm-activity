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

import org.apache.maven.scm.provider.ScmUrlUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScmUrlGuessTest {
  ScmUrlGuess scmUrlGuess;
  ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);

  @ClassRule
  public static TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before
  public void setUp() {
    scmUrlGuess = new ScmUrlGuess(projectFileSystem);
  }

  @Test
  public void shouldnt_guess_url_from_empty_project() throws IOException {
    File baseDir = createEmptyProject();
    when(projectFileSystem.getBasedir()).thenReturn(baseDir);

    String url = scmUrlGuess.guess();

    assertThat(url).isNull();
  }

  @Test
  public void should_guess_of_git_project() throws IOException {
    File baseDir = createGitProject();
    when(projectFileSystem.getBasedir()).thenReturn(baseDir);

    String url = scmUrlGuess.guess();

    assertThat(url).isEqualTo("scm:git:");
    assertThat(ScmUrlUtils.isValid(url)).isTrue();
  }

  @Test
  public void should_guess_of_svn_project() throws IOException {
    File baseDir = createSvnProject();
    when(projectFileSystem.getBasedir()).thenReturn(baseDir);

    String url = scmUrlGuess.guess();

    assertThat(url).isEqualTo("scm:svn:");
    assertThat(ScmUrlUtils.isValid(url)).isTrue();
  }

  static File createEmptyProject() throws IOException {
    return temporaryFolder.newFolder();
  }

  static File createGitProject() throws IOException {
    File baseDir = createEmptyProject();
    new File(baseDir, ".git").createNewFile();
    return baseDir;
  }

  static File createSvnProject() throws IOException {
    File baseDir = createEmptyProject();
    new File(baseDir, ".svn").createNewFile();
    return baseDir;
  }
}
