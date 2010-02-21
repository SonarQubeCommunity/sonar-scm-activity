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

package org.sonar.plugins.scmactivity;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Scm;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.manager.ExtScmManagerFactory;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensor implements Sensor {
  public static final String URL_PROPERTY = "sonar.scm-activity.url";
  public static final String ENABLED_PROPERTY = "sonar.scm-activity.enabled";
  public static final boolean ENABLED_DEFAULT_VALUE = false;
  public static final String USER_PROPERTY = "sonar.scm-activity.user.secured";
  public static final String PASSWORD_PROPERTY = "sonar.scm-activity.password.secured";
  public static final String PREFER_PURE_JAVA_PROPERTY = "sonar.scm-activity.prefer_pure_java";
  public static final boolean PREFER_PURE_JAVA_DEFAULT_VALUE = true;

  public boolean shouldExecuteOnProject(Project project) {
    // this sensor is executed only for latest analysis and if plugin enabled and scm connection is defined
    return project.isLatestAnalysis() &&
        project.getConfiguration().getBoolean(ENABLED_PROPERTY, ENABLED_DEFAULT_VALUE) &&
        !StringUtils.isBlank(getScmUrl(project));
  }

  public void analyse(Project project, SensorContext context) {
    ProjectFileSystem fileSystem = project.getFileSystem();
    List<File> sourceDirs = fileSystem.getSourceDirs();

    try {
      boolean pureJava = project.getConfiguration().getBoolean(PREFER_PURE_JAVA_PROPERTY, PREFER_PURE_JAVA_DEFAULT_VALUE);
      ExtScmManager scmManager = ExtScmManagerFactory.getScmManager(pureJava);
      ScmRepository repository = getRepository(scmManager, project);

      List<File> files = fileSystem.getJavaSourceFiles();
      for (File file : files) {
        getLog().info("Analyzing {}", file.getAbsolutePath());
        Resource resource = JavaFile.fromIOFile(file, sourceDirs, false);
        analyzeBlame(scmManager, repository, file, context, resource);
      }
    } catch (ScmException e) {
      throw new RuntimeException(e);
    }
  }

  protected Logger getLog() {
    return LoggerFactory.getLogger(getClass());
  }

  protected String getUser(Project project) {
    return project.getConfiguration().getString(USER_PROPERTY);
  }

  protected String getPassword(Project project) {
    return project.getConfiguration().getString(PASSWORD_PROPERTY);
  }

  protected String getScmUrl(Project project) {
    String url = project.getConfiguration().getString(URL_PROPERTY);
    Scm scm = project.getPom().getScm();
    if (StringUtils.isBlank(url) && scm != null) {
      if (!StringUtils.isBlank(getUser(project)) && !StringUtils.isBlank(getPassword(project))) {
        url = scm.getDeveloperConnection();
      } else {
        url = scm.getConnection();
      }
    }
    return url;
  }

  protected ScmRepository getRepository(ExtScmManager scmManager, Project project)
      throws NoSuchScmProviderException, ScmRepositoryException {
    ScmRepository repository;
    String connectionUrl = getScmUrl(project);
    getLog().info("SCM connection URL: {}", connectionUrl);
    repository = scmManager.makeScmRepository(connectionUrl);
    String user = getUser(project);
    String password = getPassword(project);
    if (!StringUtils.isBlank(user) && !StringUtils.isBlank(password)) {
      ScmProviderRepository providerRepository = repository.getProviderRepository();
      providerRepository.setUser(user);
      providerRepository.setPassword(password);
    }
    return repository;
  }

  protected void analyzeBlame(ExtScmManager scmManager, ScmRepository repository, File basedir, String filename, SensorContext context, Resource resource) throws ScmException {
    BlameScmResult result = scmManager.blame(repository, new ScmFileSet(basedir), filename);
    // TODO check result.isSuccess()

    Date lastActivity = null;
    String lastRevision = null;

    PropertiesBuilder<Integer, String> authorsBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_AUTHORS_DATA);
    PropertiesBuilder<Integer, String> datesBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_DATE_DATA);
    PropertiesBuilder<Integer, String> revisionsBuilder = new PropertiesBuilder<Integer, String>(ScmActivityMetrics.BLAME_REVISION_DATA);

    List<BlameLine> lines = result.getLines();
    for (int i = 0; i < lines.size(); i++) {
      BlameLine line = lines.get(i);
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

  protected void analyzeBlame(ExtScmManager scmManager, ScmRepository repository, File file, SensorContext context, Resource resource) {
    try {
      File basedir = file.getParentFile();
      String filename = file.getName();
      analyzeBlame(scmManager, repository, basedir, filename, context, resource);
    } catch (ScmException e) {
      getLog().warn("Unable to analyze {}: {}", file.getAbsolutePath(), e);
    }
  }

  public static String formatLastActivity(Date lastActivity) {
    SimpleDateFormat sdf = new SimpleDateFormat(ScmActivityMetrics.DATE_TIME_FORMAT);
    return sdf.format(lastActivity);
  }
}
