/*
 * Sonar SCM Activity Plugin :: Maven SCM Ext
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

package org.apache.maven.scm.manager;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsjava.CvsJavaScmProvider;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmProvider;
import org.apache.maven.scm.provider.tfs.TfsScmProvider;

/**
 * @author Evgeny Mandrikov
 */
public class ExtScmManagerFactoryTest extends TestCase {
  public void testPureJava() throws Exception {
    ScmManager scmManager = ExtScmManagerFactory.getScmManager(true);

    Assert.assertTrue(scmManager.getProviderByType("svn") instanceof SvnJavaScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("cvs") instanceof CvsJavaScmProvider);
    assertNonJava(scmManager);
  }

  public void testExe() throws Exception {
    ScmManager scmManager = ExtScmManagerFactory.getScmManager(false);

    Assert.assertTrue(scmManager.getProviderByType("svn") instanceof SvnExeScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("cvs") instanceof CvsExeScmProvider);
    assertNonJava(scmManager);
  }

  private void assertNonJava(ScmManager scmManager) throws Exception {
    Assert.assertTrue(scmManager.getProviderByType("git") instanceof GitExeScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("hg") instanceof HgScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("bazaar") instanceof BazaarScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("clearcase") instanceof ClearCaseScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("accurev") instanceof AccuRevScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("perforce") instanceof PerforceScmProvider);
    Assert.assertTrue(scmManager.getProviderByType("tfs") instanceof TfsScmProvider);
  }
}
