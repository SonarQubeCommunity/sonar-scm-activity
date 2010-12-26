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

package org.apache.maven.scm.provider.svn.svnjava.command.blame;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.svn.svnjava.repository.SvnJavaScmProviderRepository;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Original {@link SvnJavaBlameCommand} doesn't support BASE.
 */
public class FixedSvnJavaBlameCommand extends SvnJavaBlameCommand {
  public BlameScmResult executeBlameCommand(ScmProviderRepository repo, ScmFileSet workingDirectory, String filename)
      throws ScmException {
    try {
      SvnJavaScmProviderRepository javaRepo = (SvnJavaScmProviderRepository) repo;
      javaRepo.getClientManager();
      AnnotationHandler handler = new AnnotationHandler();
      blame(javaRepo.getClientManager(), new File(workingDirectory.getBasedir(), filename), handler);

      return new BlameScmResult("", handler.lines);
    } catch (SVNException e) {
      throw new ScmException(e.getMessage(), e);
    }
  }

  public static void blame(SVNClientManager clientManager, File file, ISVNAnnotateHandler handler) throws SVNException {
    clientManager.getLogClient()
        .doAnnotate(file, SVNRevision.UNDEFINED, SVNRevision.create(1L), SVNRevision.BASE, true, false, handler, null);
  }

  private static class AnnotationHandler implements ISVNAnnotateHandler {
    private List lines;

    private AnnotationHandler() {
      this.lines = new ArrayList();
    }

    public void handleEOF() {
    }

    /** @deprecated */
    public void handleLine(Date arg0, long arg1, String arg2, String arg3)
        throws SVNException {
    }

    public void handleLine(Date date, long revision, String author, String line, Date mergedDate, long mergedRevision, String mergedAuthor,
        String mergedPath, int lineNumber) throws SVNException {
      BlameLine blameLine = new BlameLine(date, Long.toString(revision), author);
      if (this.lines.size() > lineNumber) {
        this.lines.set(lineNumber, blameLine);
      } else {
        this.lines.add(blameLine);
      }
    }

    public boolean handleRevision(Date arg0, long arg1, String arg2, File arg3) throws SVNException {
      return true;
    }
  }
}
