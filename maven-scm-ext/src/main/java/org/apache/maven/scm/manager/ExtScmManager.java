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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.accurev.AccuRevScmProvider;
import org.apache.maven.scm.provider.accurev.command.blame.AccuRevBlameCommand;
import org.apache.maven.scm.provider.bazaar.BazaarScmProvider;
import org.apache.maven.scm.provider.bazaar.command.blame.BazaarBlameCommand;
import org.apache.maven.scm.provider.clearcase.ClearCaseScmProvider;
import org.apache.maven.scm.provider.clearcase.cleartoolexe.command.blame.ClearCaseBlameCommand;
import org.apache.maven.scm.provider.cvslib.cvsexe.CvsExeScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsexe.command.blame.CvsExeBlameCommand;
import org.apache.maven.scm.provider.cvslib.cvsjava.CvsJavaScmProvider;
import org.apache.maven.scm.provider.cvslib.cvsjava.command.blame.CvsJavaBlameCommand;
import org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider;
import org.apache.maven.scm.provider.git.gitexe.command.blame.GitBlameCommand;
import org.apache.maven.scm.provider.hg.HgScmProvider;
import org.apache.maven.scm.provider.hg.command.blame.HgBlameCommand;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.provider.svn.svnexe.command.blame.SvnBlameCommand;
import org.apache.maven.scm.provider.svn.svnjava.SvnJavaScmProvider;
import org.apache.maven.scm.provider.svn.svnjava.command.blame.SvnJavaBlameCommand;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.Os;

/**
 * @author Evgeny Mandrikov
 */
public class ExtScmManager extends AbstractScmManager {
  private ScmLogger logger;

  public ExtScmManager() {
    this(new DefaultLog());
  }

  public ExtScmManager(ScmLogger logger) {
    super();
    this.logger = logger;
  }

  protected ScmLogger getScmLogger() {
    return logger;
  }

  protected AbstractBlameCommand getBlameCommand(ScmRepository repository) throws ScmException {
    ScmProvider provider = getProviderByRepository(repository);
    if (provider instanceof SvnJavaScmProvider) {
      return new SvnJavaBlameCommand();
    } else if (provider instanceof SvnExeScmProvider) {
      return new SvnBlameCommand();
    } else if (provider instanceof GitExeScmProvider) {
      return new GitBlameCommand();
    } else if (provider instanceof CvsExeScmProvider) {
      return new CvsExeBlameCommand();
    } else if (provider instanceof CvsJavaScmProvider) {
      return new CvsJavaBlameCommand();
    } else if (provider instanceof HgScmProvider) {
      return new HgBlameCommand();
    } else if (provider instanceof BazaarScmProvider) {
      return new BazaarBlameCommand();
    } else if (provider instanceof ClearCaseScmProvider) {
      return new ClearCaseBlameCommand();
    } else if (provider instanceof AccuRevScmProvider) {
      String accurevExecutable = resolveAccurevExecutable(Os.isFamily("windows"));
      return new AccuRevBlameCommand(accurevExecutable);
    } else {
      throw new ScmException("Unsupported SCM provider: " + provider.toString());
    }
  }

  public BlameScmResult blame(ScmRepository repository, ScmFileSet workingDirectory, String filename) throws ScmException {
    ScmProviderRepository providerRepository = repository.getProviderRepository();
    AbstractBlameCommand blameCommand = getBlameCommand(repository);
    blameCommand.setLogger(getScmLogger());
    return blameCommand.executeBlameCommand(providerRepository, workingDirectory, filename);
  }

  /**
   * Copied from {@link org.apache.maven.scm.provider.accurev.AccuRevScmProvider},
   * since {@link org.apache.maven.scm.provider.accurev.AccuRevScmProvider#getAccurevExecutable()} has protected access.
   */
  private static String resolveAccurevExecutable(boolean windows) {
    String executable = "accurev";
    //Append ".exe" suffix if the OS is Windows
    if (windows) {
      executable += ".exe";
    }
    //Grab exeucutable from system variable if specified
    String accurevExecutable = System.getProperty("accurevExecutable");
    if (accurevExecutable != null) {
      executable = accurevExecutable;
    }
    return executable;
  }
}
