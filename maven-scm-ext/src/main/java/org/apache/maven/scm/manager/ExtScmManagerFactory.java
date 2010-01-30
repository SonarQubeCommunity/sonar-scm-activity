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

import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsjava.CvsJavaScmProvider;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmProvider;

/**
 * @author Evgeny Mandrikov
 */
public class ExtScmManagerFactory {
  private boolean pureJava;

  public ExtScmManagerFactory(boolean pureJava) {
    this.pureJava = pureJava;
  }

  public ExtScmManager getScmManager() {
    ExtScmManager scmManager = new ExtScmManager();
    scmManager.setScmProvider("svn", pureJava ? new SvnJavaScmProvider() : new SvnExeScmProvider());
    scmManager.setScmProvider("git", new GitExeScmProvider());
    scmManager.setScmProvider("cvs", pureJava ? new CvsJavaScmProvider() : new CvsExeScmProvider());
    scmManager.setScmProvider("hg", new HgScmProvider());
    scmManager.setScmProvider("bazaar", new BazaarScmProvider());
    return scmManager;
  }

  public static ExtScmManager getScmManager(boolean pureJava) {
    return new ExtScmManagerFactory(pureJava).getScmManager();
  }
}
