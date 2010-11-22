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

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Resource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class BlameSensor {
  private SensorContext context;
  private ScmManager scmManager;
  private ScmRepository repository;

  public BlameSensor(ScmManager scmManager, ScmRepository repository, SensorContext context) {
    this.scmManager = scmManager;
    this.repository = repository;
    this.context = context;
  }

  public void analyse(File file, Resource resource) {
    getLog().info("Analyzing blame for {}", file.getAbsolutePath());
    File basedir = file.getParentFile();
    String filename = file.getName();
    try {
      analyseBlame(basedir, filename, resource);
    } catch (ScmException e) {
      Logger logger = getLog();
      if (logger.isDebugEnabled()) {
        getLog().warn("Unable to analyze", e);
      } else {
        getLog().warn("Unable to analyze: {}", e.getMessage());
      }
    }
  }

  protected void analyseBlame(File basedir, String filename, Resource resource) throws ScmException {
    BlameScmResult result = scmManager.blame(repository, new ScmFileSet(basedir), filename);
    if (!result.isSuccess()) {
      throw new ScmException(result.getProviderMessage());
    }

    Date lastActivity = null;
    String lastRevision = null;

    PropertiesBuilder<Integer, String> authorsBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_AUTHORS_DATA);
    PropertiesBuilder<Integer, String> datesBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_DATE_DATA);
    PropertiesBuilder<Integer, String> revisionsBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_REVISION_DATA);

    List lines = result.getLines();
    for (int i = 0; i < lines.size(); i++) {
      BlameLine line = (BlameLine) lines.get(i);
      Date date = line.getDate();
      String revision = line.getRevision();
      String author = line.getAuthor();

      int lineNumber = i + 1;
      datesBuilder.add(lineNumber, formatLastActivity(date));
      revisionsBuilder.add(lineNumber, revision);
      authorsBuilder.add(lineNumber, author);

      if (lastActivity == null || lastActivity.before(date)) {
        lastActivity = date;
        lastRevision = revision;
      }
    }

    if (lastActivity != null) {
      context.saveMeasure(resource, authorsBuilder.build());
      context.saveMeasure(resource, datesBuilder.build());
      context.saveMeasure(resource, revisionsBuilder.build());

      Measure lastRevisionMeasure = new Measure(ScmActivityMetrics.REVISION, lastRevision);
      context.saveMeasure(resource, lastRevisionMeasure);

      Measure lastActivityMeasure = new Measure(ScmActivityMetrics.LAST_ACTIVITY, formatLastActivity(lastActivity));
      context.saveMeasure(resource, lastActivityMeasure);
    }
  }

  protected Logger getLog() {
    return LoggerFactory.getLogger(getClass());
  }

  public static String formatLastActivity(Date lastActivity) {
    SimpleDateFormat sdf = new SimpleDateFormat(ScmActivityMetrics.DATE_TIME_FORMAT);
    return sdf.format(lastActivity);
  }
}
