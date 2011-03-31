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

package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;
import org.apache.maven.scm.provider.git.gitexe.command.GitCommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;

/**
 * Fix for SONARPLUGINS-861 and for showing long rev
 */
public class FixedGitBlameCommand extends AbstractBlameCommand implements GitCommand {
  /**
   * {@inheritDoc}
   */
  public BlameScmResult executeBlameCommand(ScmProviderRepository repo, ScmFileSet workingDirectory,
                                             String filename)
      throws ScmException {
    Commandline cl = createCommandLine(workingDirectory.getBasedir(), filename);
    FixedGitBlameConsumer consumer = new FixedGitBlameConsumer(getLogger());
    CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

    int exitCode = GitCommandLineUtils.execute(cl, consumer, stderr, getLogger());
    if (exitCode != 0) {
      throw new UnsupportedOperationException();
    }
    return new BlameScmResult(cl.toString(), consumer.getLines());
  }

  public static Commandline createCommandLine(File workingDirectory, String filename) {
    Commandline cl = GitCommandLineUtils.getBaseGitCommandLine(workingDirectory, "blame");
    cl.createArg().setValue("-c");
    cl.createArg().setValue("-l");
    cl.createArg().setValue(filename);
    return cl;
  }
}
