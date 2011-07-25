/*
 * Sonar SCM Activity Plugin
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

package org.sonar.plugins.scmactivity;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.Logs;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.TimeProfiler;

public class Changelog implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(Changelog.class);

  private SonarScmRepository repositoryBuilder;
  private ScmManager manager;
  private ScmConfiguration conf;

  public Changelog(ScmConfiguration conf, SonarScmRepository repositoryBuilder, ScmManager manager) {
    this.conf = conf;
    this.repositoryBuilder = repositoryBuilder;
    this.manager = manager;
  }

  public ProjectStatus load(ProjectStatus status, String startRevision) {
    List<ChangeSet> changeLog = retrieveChangeSets(startRevision);
    for (ChangeSet changeSet : changeLog) {
      if (fixChangeSet(changeSet)) {
        // startRevision already was analyzed in previous Sonar run
        // Git excludes this revision from changelog, but Subversion not
        if (!StringUtils.equals(startRevision, changeSet.getRevision())) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("{} files changed {} ({})", new Object[]{
                changeSet.getFiles().size(),
                changeSet.getDateFormatted() + " " + changeSet.getTimeFormatted(),
                changeSet.getRevision()});
          }
          status.add(changeSet);
        }
      }
    }
    return status;
  }

  /**
   * TODO BASE should be correctly handled by providers other than SvnExe
   *
   * @return changes that have happened between <code>startVersion</code> and BASE
   */
  private List<ChangeSet> retrieveChangeSets(String startRevision) {
    ScmRepository repository = repositoryBuilder.getScmRepository();
    boolean fullChangeLog = StringUtils.isBlank(startRevision);
    ScmRevision endVersion = null;
    if ("svn".equals(repository.getProvider())) {
      if (startRevision == null) {
        startRevision = "1";
      }
      endVersion = new ScmRevision("BASE");
    }

    String title = "Retrieve changelog";
    if (StringUtils.isNotBlank(startRevision)) {
      title += " from revision " + startRevision;
    }
    TimeProfiler profiler = new TimeProfiler().start(title);
    if (fullChangeLog) {
      Logs.INFO.info("It can be long this first time. Next analysis will be faster.");
    }

    try {
      // Next line is required to workaround problem with TFS (see SONARPLUGINS-1291) and it should not have an impact on other SCMs.
      ScmFileSet fileSet = new ScmFileSet(conf.getBaseDir(), Arrays.asList(new File(".")));
      ChangeLogScmResult result = manager.changeLog(repository, fileSet,
          startRevision == null ? null : new ScmRevision(startRevision), endVersion);

      if (!result.isSuccess()) {
        throw new SonarException("Unable to retrieve changelog: " + result.getCommandOutput());
      }

      if (conf.isVerbose()) {
        File output = new File(conf.getWorkdir(), "scm_changelog.xml");
        LOG.info("Storing changelog results into: " + output.getCanonicalPath());
        FileUtils.writeStringToFile(output, result.getChangeLog().toXML());
      }
      return result.getChangeLog().getChangeSets();

    } catch (ScmException e) {
      throw new SonarException("Fail to retrieve changelog from revision " + startRevision, e);

    } catch (IOException e) {
      throw new SonarException("Fail to store changelog", e);

    } finally {
      profiler.stop();
    }
  }

  /**
   * This is a workaround for bug, which exists in {@link org.apache.maven.scm.provider.git.gitexe.GitExeScmProvider}.
   *
   * @return true, if workaround can be applied
   */
  static boolean fixChangeSet(ChangeSet changeSet) {
    if (changeSet.getRevision() == null) {
      List files = changeSet.getFiles();
      if (files.isEmpty()) {
        // This may happen if Git changelog can't be correctly parsed
        // for example when message was not provided for commit
        return false;
      }
      ChangeFile file = (ChangeFile) files.get(0);
      changeSet.setRevision(file.getRevision());
    }
    return true;
  }
}
