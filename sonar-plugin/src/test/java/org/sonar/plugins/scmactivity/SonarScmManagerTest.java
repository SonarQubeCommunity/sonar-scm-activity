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
import org.apache.maven.scm.provider.tfs.TfsScmProvider;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SonarScmManagerTest {

  @Test(expected = NoSuchScmProviderException.class)
  public void shouldNotRegisterProvidersIfDisabled() throws Exception {
    ScmConfiguration conf = mock(ScmConfiguration.class);
    when(conf.isEnabled()).thenReturn(false);
    SonarScmManager scmManager = new SonarScmManager(conf);

    scmManager.getProviderByType("svn");
  }

  @Test
  public void shouldBeNativeProviders() throws Exception {
    ScmConfiguration conf = mock(ScmConfiguration.class);
    when(conf.isEnabled()).thenReturn(true);
    SonarScmManager scmManager = new SonarScmManager(conf);

    assertTrue(scmManager.getProviderByType("svn") instanceof SvnExeScmProvider);
    assertTrue(scmManager.getProviderByType("git") instanceof GitExeScmProvider);
    assertTrue(scmManager.getProviderByType("cvs") instanceof CvsExeScmProvider);
    assertTrue(scmManager.getProviderByType("hg") instanceof HgScmProvider);
    assertTrue(scmManager.getProviderByType("bazaar") instanceof BazaarScmProvider);
    assertTrue(scmManager.getProviderByType("clearcase") instanceof ClearCaseScmProvider);
    assertTrue(scmManager.getProviderByType("accurev") instanceof AccuRevScmProvider);
    assertTrue(scmManager.getProviderByType("perforce") instanceof PerforceScmProvider);
    assertTrue(scmManager.getProviderByType("tfs") instanceof TfsScmProvider);
  }
}
