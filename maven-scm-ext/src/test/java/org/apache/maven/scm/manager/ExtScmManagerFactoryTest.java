/*
 * Copyright (C) 2010 Evgeny Mandrikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
