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

import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.provider.svn.util.SvnUtil;
import org.apache.maven.scm.provider.tfs.TfsScmProvider;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SonarScmManagerTest {
  ScmConfiguration conf = mock(ScmConfiguration.class);

  @Test(expected = NoSuchScmProviderException.class)
  public void shouldNotRegisterProvidersIfDisabled() throws Exception {
    when(conf.isEnabled()).thenReturn(false);

    SonarScmManager scmManager = new SonarScmManager(conf);
    scmManager.getProviderByType("svn");
  }

  @Test
  public void should_use_native_providers() throws NoSuchScmProviderException {
    when(conf.isEnabled()).thenReturn(true);

    SonarScmManager scmManager = new SonarScmManager(conf);

    assertThat(scmManager.getProviderByType("svn")).isInstanceOf(SvnExeScmProvider.class);
    assertThat(scmManager.getProviderByType("git")).isInstanceOf(GitExeScmProvider.class);
    assertThat(scmManager.getProviderByType("cvs")).isInstanceOf(CvsExeScmProvider.class);
    assertThat(scmManager.getProviderByType("hg")).isInstanceOf(HgScmProvider.class);
    assertThat(scmManager.getProviderByType("bazaar")).isInstanceOf(BazaarScmProvider.class);
    assertThat(scmManager.getProviderByType("clearcase")).isInstanceOf(ClearCaseScmProvider.class);
    assertThat(scmManager.getProviderByType("accurev")).isInstanceOf(AccuRevScmProvider.class);
    assertThat(scmManager.getProviderByType("perforce")).isInstanceOf(PerforceScmProvider.class);
    assertThat(scmManager.getProviderByType("tfs")).isInstanceOf(TfsScmProvider.class);
  }

  @Test
  public void shouldInitSvn() {
    when(conf.isEnabled()).thenReturn(true);
    when(conf.getScmProvider()).thenReturn("svn");

    new SonarScmManager(conf);

    assertThat(SvnUtil.getSettings().isTrustServerCert()).isTrue();
  }
}
