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

package org.apache.maven.scm.provider.svn.svnjava.command.changelog;

import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.svn.SvnChangeSet;
import org.apache.maven.scm.provider.svn.svnjava.repository.SvnJavaScmProviderRepository;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Original {@link SvnJavaChangeLogCommand} doesn't support revisions range.
 */
public class FixedSvnJavaChangeLogCommand extends SvnJavaChangeLogCommand {
  protected ChangeLogScmResult executeChangeLogCommand(ScmProviderRepository repo, ScmFileSet fileSet,
      ScmVersion startVersion, ScmVersion endVersion, String datePattern) throws ScmException {

    SvnJavaScmProviderRepository javaRepo = (SvnJavaScmProviderRepository) repo;

    SVNRevision startRevision = (startVersion != null) ?
        SVNRevision.create(Long.parseLong(startVersion.getName())) :
        SVNRevision.UNDEFINED;

    SVNRevision endRevision = (endVersion != null) ?
        SVNRevision.create(Long.parseLong(endVersion.getName())) :
        SVNRevision.BASE;

    try {
      ChangeLogHandler handler = new ChangeLogHandler(null, null); // TODO

      changelog(javaRepo.getClientManager(), fileSet.getBasedir(), startRevision, endRevision, true, true, handler);

      return new ChangeLogScmResult("JavaSVN Library", handler.getChangeSets());
    } catch (SVNException e) {
      return new ChangeLogScmResult("JavaSVN Library", "SVN Changelog failed.", e.getMessage(), false);
    }
  }

  public static void changelog(SVNClientManager clientManager, File basedir, SVNRevision startRevision, SVNRevision endRevision,
      boolean stopOnCopy, boolean reportPaths, ISVNLogEntryHandler handler) throws SVNException {
    SVNLogClient logClient = clientManager.getLogClient();

    logClient.doLog(new File[] { basedir }, startRevision, startRevision, endRevision, stopOnCopy, reportPaths, 1024L, handler);
  }

  protected static class ChangeLogHandler implements ISVNLogEntryHandler {
    private ChangeLogSet changeLogSet;
    private List changeSets = new ArrayList();

    public ChangeLogHandler(Date startDate, Date endDate) {
      changeLogSet = new ChangeLogSet(startDate, endDate);
    }

    public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
      List changedFiles = new ArrayList();

      for (Iterator i = logEntry.getChangedPaths().keySet().iterator(); i.hasNext();) {
        ChangeFile changeFile = new ChangeFile((String) i.next());
        changeFile.setRevision(Long.toString(logEntry.getRevision()));
        changedFiles.add(changeFile);
      }

      changeSets.add(new SvnChangeSet(logEntry.getDate(), logEntry.getMessage(), logEntry.getAuthor(), changedFiles));
    }

    public ChangeLogSet getChangeSets() {
      changeLogSet.setChangeSets(changeSets);
      return changeLogSet;
    }
  }

}
