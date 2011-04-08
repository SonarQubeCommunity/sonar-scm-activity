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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.*;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.Logs;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class Blame implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(Blame.class);

  private ScmManager scmManager;
  private SonarScmRepository repositoryBuilder;

  private PropertiesBuilder<Integer, String> authorsBuilder = new PropertiesBuilder<Integer, String>(CoreMetrics.SCM_AUTHORS_BY_LINE);
  private PropertiesBuilder<Integer, String> datesBuilder = new PropertiesBuilder<Integer, String>(CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE);
  private PropertiesBuilder<Integer, String> revisionsBuilder = new PropertiesBuilder<Integer, String>(CoreMetrics.SCM_REVISIONS_BY_LINE);

  public Blame(ScmManager scmManager, SonarScmRepository repositoryBuilder) {
    this.scmManager = scmManager;
    this.repositoryBuilder = repositoryBuilder;
  }


  public void analyse(ProjectStatus.FileStatus fileStatus, Resource resource, SensorContext context) {
    BlameScmResult result = retrieveBlame(fileStatus.getFile());

    if (result != null) {
      authorsBuilder.clear();
      revisionsBuilder.clear();
      datesBuilder.clear();

      List lines = result.getLines();
      for (int i = 0; i < lines.size(); i++) {
        BlameLine line = (BlameLine) lines.get(i);
        Date date = line.getDate();
        String revision = line.getRevision();
        String author = line.getAuthor();

        int lineNumber = i + 1;
        datesBuilder.add(lineNumber, DateUtils.formatDateTime(date));
        revisionsBuilder.add(lineNumber, revision);
        authorsBuilder.add(lineNumber, author);
      }


      saveDataMeasure(context, resource, CoreMetrics.SCM_REVISION, fileStatus.getRevision(), PersistenceMode.FULL);
      saveDataMeasure(context, resource, CoreMetrics.SCM_LAST_COMMIT_DATE, ScmUtils.formatLastCommitDate(fileStatus.getDate()), PersistenceMode.FULL);
      saveDataMeasure(context, resource, CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE, datesBuilder.buildData(), PersistenceMode.DATABASE);
      saveDataMeasure(context, resource, CoreMetrics.SCM_REVISIONS_BY_LINE, revisionsBuilder.buildData(), PersistenceMode.DATABASE);
      saveDataMeasure(context, resource, CoreMetrics.SCM_AUTHORS_BY_LINE, authorsBuilder.buildData(), PersistenceMode.DATABASE);
    }
  }

  private void saveDataMeasure(SensorContext context, Resource resource, Metric metricKey, String data, PersistenceMode persistence) {
    if (StringUtils.isNotBlank(data)) {
      context.saveMeasure(resource, new Measure(metricKey, data).setPersistenceMode(persistence));
    }
  }

  BlameScmResult retrieveBlame(File file) {
    try {
      Logs.INFO.info("Retrieve SCM info for " + file);
      BlameScmResult result = scmManager.blame(repositoryBuilder.getScmRepository(), new ScmFileSet(file.getParentFile()), file.getName());
      if (!result.isSuccess()) {
        LOG.warn("Fail to retrieve SCM info of: " + file + ". Reason: " + result.getProviderMessage() + SystemUtils.LINE_SEPARATOR + result.getCommandOutput());
        return null;
      }
      return result;

    } catch (ScmException e) {
      // See SONARPLUGINS-368. Can occur on generated source
      LOG.warn("Fail to retrieve SCM info of: " + file, e);
      return null;
    }
  }
}
