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

import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsjava.CvsJavaScmProvider;
import org.apache.maven.scm.provider.git.gitexe.FixedGitExeScmProvider;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmProvider;
import org.apache.maven.scm.provider.tfs.TfsScmProvider;

/**
 * @author Evgeny Mandrikov
 */
public final class ExtScmManagerFactory {
  /**
   * Hide utility-class constructor.
   */
  private ExtScmManagerFactory() {
  }

  public static ScmManager getScmManager(boolean pureJava) {
    ScmManager scmManager = new BasicScmManager();
    if (pureJava) {
      scmManager.setScmProvider("svn", new SvnJavaScmProvider());
      scmManager.setScmProvider("cvs", new CvsJavaScmProvider());
    } else {
      scmManager.setScmProvider("svn", new SvnExeScmProvider());
      scmManager.setScmProvider("cvs", new CvsExeScmProvider());
    }
    scmManager.setScmProvider("git", new FixedGitExeScmProvider());
    scmManager.setScmProvider("hg", new HgScmProvider());
    scmManager.setScmProvider("bazaar", new BazaarScmProvider());
    scmManager.setScmProvider("clearcase", new ClearCaseScmProvider());
    scmManager.setScmProvider("accurev", new AccuRevScmProvider());
    scmManager.setScmProvider("perforce", new PerforceScmProvider());
    scmManager.setScmProvider("tfs", new TfsScmProvider());
    return scmManager;
  }
}
