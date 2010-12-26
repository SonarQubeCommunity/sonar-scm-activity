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

import junit.framework.Assert;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsjava.CvsJavaScmProvider;
import org.apache.maven.scm.provider.git.gitexe.SonarGitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SonarSvnExeScmProvider;
import org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmProvider;
import org.apache.maven.scm.provider.tfs.TfsScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ProjectScmManagerTest {

  private ScmConfiguration configuration;
  private ScmManager scmManager;
  private ScmRepository repository;
  private ScmProviderRepository providerRepository;
  private ProjectScmManager projectScmManager;

  @Before
  public void setUp() throws Exception {
    configuration = mock(ScmConfiguration.class);
    scmManager = mock(ScmManager.class);
    repository = mock(ScmRepository.class);
    providerRepository = mock(ScmProviderRepository.class);
    when(scmManager.makeScmRepository(anyString())).thenReturn(repository);
    when(repository.getProviderRepository()).thenReturn(providerRepository);

    projectScmManager = spy(new ProjectScmManager(null, configuration));
  }

  @Test
  public void shouldSetCredentials() throws Exception {
    doReturn(scmManager).when(projectScmManager).getScmManager();
    when(configuration.getUser()).thenReturn("godin");
    when(configuration.getPassword()).thenReturn("pass");

    assertThat(projectScmManager.getScmRepository(), sameInstance(repository));

    verify(providerRepository).setUser("godin");
    verify(providerRepository).setPassword("pass");
  }

  @Test
  public void shouldNotSetCredentials() throws Exception {
    doReturn(scmManager).when(projectScmManager).getScmManager();
    assertThat(projectScmManager.getScmRepository(), sameInstance(repository));

    verify(providerRepository, never()).setUser(anyString());
    verify(providerRepository, never()).setPassword(anyString());
  }

  @Test
  public void shouldBeDisabled() {
    when(configuration.isEnabled()).thenReturn(true).thenReturn(false);
    when(configuration.getUrl()).thenReturn("").thenReturn("scm:svn:http//localhost");

    assertThat(projectScmManager.isEnabled(), is(false));
    assertThat(projectScmManager.isEnabled(), is(false));
  }

  @Test
  public void shouldBeEnabled() {
    when(configuration.isEnabled()).thenReturn(true);
    when(configuration.getUrl()).thenReturn("scm:svn:http//localhost");

    assertThat(projectScmManager.isEnabled(), is(true));
  }

  @Test
  public void testPureJava() throws Exception {
    when(configuration.isPureJava()).thenReturn(true);

    ScmManager scmManager = projectScmManager.getScmManager();

    Assert.assertTrue(scmManager.getProviderByType("svn") instanceof SvnJavaScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("cvs") instanceof CvsJavaScmProvider);
    assertNonJava(scmManager);
  }

  @Test
  public void testExe() throws Exception {
    when(configuration.isPureJava()).thenReturn(false);

    ScmManager scmManager = projectScmManager.getScmManager();

    Assert.assertTrue(scmManager.getProviderByType("svn") instanceof SonarSvnExeScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("cvs") instanceof CvsExeScmProvider);
    assertNonJava(scmManager);
  }

  private void assertNonJava(ScmManager scmManager) throws Exception {
    Assert.assertTrue(scmManager.getProviderByType("git") instanceof SonarGitExeScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("hg") instanceof HgScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("bazaar") instanceof BazaarScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("clearcase") instanceof ClearCaseScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("accurev") instanceof AccuRevScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("perforce") instanceof PerforceScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("tfs") instanceof TfsScmProvider);
  }
}
