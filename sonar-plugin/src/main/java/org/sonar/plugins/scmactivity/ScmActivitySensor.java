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
import org.apache.maven.scm.manager.ExtScmManagerFactory;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;

import java.io.File;
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

    BlameSensor blameSensor;
    try {
      boolean pureJava = project.getConfiguration().getBoolean(PREFER_PURE_JAVA_PROPERTY, PREFER_PURE_JAVA_DEFAULT_VALUE);
      ScmManager scmManager = ExtScmManagerFactory.getScmManager(pureJava);
      ScmRepository repository = getRepository(scmManager, project);
      blameSensor = new BlameSensor(scmManager, repository, context);
    } catch (ScmException e) {
      throw new SonarException(e);
    }

    List<File> files = fileSystem.getJavaSourceFiles();
    for (File file : files) {
      Resource resource = JavaFile.fromIOFile(file, sourceDirs, false);
      blameSensor.analyse(file, resource);
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

  protected ScmRepository getRepository(ScmManager scmManager, Project project)
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

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
