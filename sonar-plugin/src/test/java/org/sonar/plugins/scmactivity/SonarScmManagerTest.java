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
import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.apache.maven.scm.provider.git.gitexe.SonarGitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SonarSvnExeScmProvider;
import org.apache.maven.scm.provider.tfs.TfsScmProvider;
import org.junit.Test;

public class SonarScmManagerTest {

  @Test
  public void shouldBePatchedProviders() throws Exception {
    SonarScmManager scmManager = new SonarScmManager();
    Assert.assertTrue(scmManager.getProviderByType("svn") instanceof SonarSvnExeScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("git") instanceof SonarGitExeScmProvider);
  }

  @Test
  public void shouldBeNativeProviders() throws Exception {
    SonarScmManager scmManager = new SonarScmManager();
    Assert.assertTrue(scmManager.getProviderByType("cvs") instanceof CvsExeScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("hg") instanceof HgScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("bazaar") instanceof BazaarScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("clearcase") instanceof ClearCaseScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("accurev") instanceof AccuRevScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("perforce") instanceof PerforceScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("tfs") instanceof TfsScmProvider);
  }
}
