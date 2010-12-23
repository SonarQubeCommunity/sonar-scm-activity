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

package org.apache.maven.scm.provider.svn.svnexe.command.changelog;

import org.apache.maven.scm.*;
import org.apache.maven.scm.command.changelog.AbstractChangeLogCommand;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.svn.SvnTagBranchUtils;
import org.apache.maven.scm.provider.svn.command.SvnCommand;
import org.apache.maven.scm.provider.svn.repository.SvnScmProviderRepository;
import org.apache.maven.scm.provider.svn.svnexe.command.SvnCommandLineUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Original {@link SvnChangeLogCommand} doesn't support BASE as a revision argument.
 */
public class FixedSvnChangeLogCommand extends AbstractChangeLogCommand implements SvnCommand {
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

  protected ChangeLogScmResult executeChangeLogCommand(ScmProviderRepository repo, ScmFileSet fileSet, ScmVersion startVersion,
      ScmVersion endVersion, String datePattern) throws ScmException {
    return executeChangeLogCommand(repo, fileSet, null, null, null, datePattern, startVersion, endVersion);
  }

  protected ChangeLogScmResult executeChangeLogCommand(ScmProviderRepository repo, ScmFileSet fileSet, Date startDate, Date endDate,
      ScmBranch branch, String datePattern) throws ScmException {
    return executeChangeLogCommand(repo, fileSet, startDate, endDate, branch, datePattern, null, null);
  }

  protected ChangeLogScmResult executeChangeLogCommand(ScmProviderRepository repo, ScmFileSet fileSet, Date startDate, Date endDate,
      ScmBranch branch, String datePattern, ScmVersion startVersion, ScmVersion endVersion) throws ScmException {

    Commandline cl = createCommandLine((SvnScmProviderRepository) repo, fileSet.getBasedir(), branch, startDate, endDate, startVersion,
        endVersion);

    SvnChangeLogConsumer consumer = new SvnChangeLogConsumer(getLogger(), datePattern);

    CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

    if (getLogger().isInfoEnabled()) {
      getLogger().info("Executing: " + SvnCommandLineUtils.cryptPassword(cl));
      getLogger().info("Working directory: " + cl.getWorkingDirectory().getAbsolutePath());
    }

    int exitCode;
    try {
      exitCode = SvnCommandLineUtils.execute(cl, consumer, stderr, getLogger());
    } catch (CommandLineException ex) {
      throw new ScmException("Error while executing svn command.", ex);
    }

    if (exitCode != 0) {
      return new ChangeLogScmResult(cl.toString(), "The svn command failed.", stderr.getOutput(), false);
    }
    ChangeLogSet changeLogSet = new ChangeLogSet(consumer.getModifications(), startDate, endDate);
    changeLogSet.setStartVersion(startVersion);
    changeLogSet.setEndVersion(endVersion);

    return new ChangeLogScmResult(cl.toString(), changeLogSet);
  }

  public static Commandline createCommandLine(SvnScmProviderRepository repository, File workingDirectory, ScmBranch branch, Date startDate,
      Date endDate, ScmVersion startVersion, ScmVersion endVersion) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    Commandline cl = SvnCommandLineUtils.getBaseSvnCommandLine(workingDirectory, repository);

    cl.createArg().setValue("log");

    cl.createArg().setValue("-v");

    if (startDate != null) {
      cl.createArg().setValue("-r");

      if (endDate != null) {
        cl.createArg().setValue("{" + dateFormat.format(startDate) + "}" + ":" + "{" + dateFormat.format(endDate) + "}");
      } else {
        cl.createArg().setValue("{" + dateFormat.format(startDate) + "}:HEAD");
      }
    }

    if (startVersion != null) {
      cl.createArg().setValue("-r");

      if (endVersion != null) {
        if (startVersion.getName().equals(endVersion.getName())) {
          cl.createArg().setValue(startVersion.getName());
        } else {
          cl.createArg().setValue(startVersion.getName() + ":" + endVersion.getName());
        }

      } else {
        // Next line was modified
        cl.createArg().setValue(startVersion.getName() + ":BASE");
      }
    }

    if ((branch != null) && (StringUtils.isNotEmpty(branch.getName()))) {
      if (branch instanceof ScmTag) {
        cl.createArg().setValue(SvnTagBranchUtils.resolveTagUrl(repository, (ScmTag) branch));
      } else {
        cl.createArg().setValue(SvnTagBranchUtils.resolveBranchUrl(repository, branch));
      }
    }

    // Next line was commented out to support BASE as a revision argument
    // cl.createArg().setValue(repository.getUrl());

    return cl;
  }
}
