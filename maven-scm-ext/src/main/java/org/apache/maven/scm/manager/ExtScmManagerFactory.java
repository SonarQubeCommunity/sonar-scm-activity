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

/**
 * @author Evgeny Mandrikov
 */
public final class ExtScmManagerFactory {
  /**
   * Hide utility-class constructor.
   */
  private ExtScmManagerFactory() {
  }

  public static ExtScmManager getScmManager(boolean pureJava) {
    ExtScmManager scmManager = new ExtScmManager();
    if (pureJava) {
      scmManager.setScmProvider("svn", new SvnJavaScmProvider());
      scmManager.setScmProvider("cvs", new CvsJavaScmProvider());
    } else {
      scmManager.setScmProvider("svn", new SvnExeScmProvider());
      scmManager.setScmProvider("cvs", new CvsExeScmProvider());
    }
    scmManager.setScmProvider("git", new GitExeScmProvider());
    scmManager.setScmProvider("hg", new HgScmProvider());
    scmManager.setScmProvider("bazaar", new BazaarScmProvider());
    scmManager.setScmProvider("clearcase", new ClearCaseScmProvider());
    scmManager.setScmProvider("accurev", new AccuRevScmProvider());
    scmManager.setScmProvider("perforce", new PerforceScmProvider());
    return scmManager;
  }
}
