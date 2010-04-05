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
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.provider.accurev.command.blame.AccuRevBlameCommand;
import org.apache.maven.scm.provider.bazaar.command.blame.BazaarBlameCommand;
import org.apache.maven.scm.provider.clearcase.cleartoolexe.command.blame.ClearCaseBlameCommand;
import org.apache.maven.scm.provider.cvslib.cvsexe.command.blame.CvsExeBlameCommand;
import org.apache.maven.scm.provider.cvslib.cvsjava.command.blame.CvsJavaBlameCommand;
import org.apache.maven.scm.provider.git.gitexe.command.blame.GitBlameCommand;
import org.apache.maven.scm.provider.hg.command.blame.HgBlameCommand;
import org.apache.maven.scm.provider.perforce.command.blame.PerforceBlameCommand;
import org.apache.maven.scm.provider.svn.svnexe.command.blame.SvnBlameCommand;
import org.apache.maven.scm.provider.svn.svnjava.command.blame.SvnJavaBlameCommand;
import org.apache.maven.scm.provider.tfs.command.blame.TfsBlameCommand;

/**
 * @author Evgeny Mandrikov
 */
public class ExtScmManagerTest extends TestCase {
  public void testPureJava() throws Exception {
    ExtScmManager scmManager = ExtScmManagerFactory.getScmManager(true);

    Assert.assertTrue(getBlameCommand(scmManager, "scm:svn:http://localhost") instanceof SvnJavaBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:cvs:local:/cvs:module") instanceof CvsJavaBlameCommand);
  }

  public void testExe() throws Exception {
    ExtScmManager scmManager = ExtScmManagerFactory.getScmManager(false);

    Assert.assertTrue(getBlameCommand(scmManager, "scm:svn:http://host/") instanceof SvnBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:git:git://host/") instanceof GitBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:hg:http://host/") instanceof HgBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:bazaar:http://host/") instanceof BazaarBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:cvs:local:/cvs:module") instanceof CvsExeBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:clearcase:load \\module") instanceof ClearCaseBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:accurev:server:port/depot/my_app/") instanceof AccuRevBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:perforce://depot/modules/myproject") instanceof PerforceBlameCommand);
    Assert.assertTrue(getBlameCommand(scmManager, "scm:tfs:http://host:/myproject") instanceof TfsBlameCommand);
  }

  private AbstractBlameCommand getBlameCommand(ExtScmManager scmManager, String scmUrl) throws ScmException {
    return scmManager.getBlameCommand(scmManager.makeScmRepository(scmUrl));
  }
}
