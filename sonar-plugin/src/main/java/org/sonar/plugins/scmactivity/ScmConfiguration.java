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

import com.google.common.collect.Lists;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.provider.ScmUrlUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;
import java.util.List;

public class ScmConfiguration implements BatchExtension {

  private Configuration conf;
  private MavenScmConfiguration mavenConf;
  private ProjectFileSystem fileSystem;
  private boolean isJavaProject;
  private boolean isIgnoreLocalModifications;

  public ScmConfiguration(Project project, Configuration configuration, MavenScmConfiguration mavenConfiguration) {
    this.fileSystem = project.getFileSystem();
    this.conf = configuration;
    this.mavenConf = mavenConfiguration;
    isJavaProject = Java.KEY.equals(project.getLanguageKey());
    isIgnoreLocalModifications = configuration.getBoolean(ScmActivityPlugin.IGNORE_LOCAL_MODIFICATIONS, ScmActivityPlugin.IGNORE_LOCAL_MODIFICATIONS_DEFAULT_VALUE);
  }

  public ScmConfiguration(Project project, Configuration configuration) {
    this(project, configuration, null /* not in maven environment */);
  }

  public boolean isEnabled() {
    return conf.getBoolean(ScmActivityPlugin.ENABLED_PROPERTY, ScmActivityPlugin.ENABLED_DEFAULT_VALUE) && getUrl() != null;
  }

  public String getScmProvider() {
    String url = getUrl();
    if (StringUtils.isNotBlank(url)) {
      return ScmUrlUtils.getProvider(url);
    }
    return null;
  }

  public String getUser() {
    return conf.getString(ScmActivityPlugin.USER_PROPERTY);
  }

  public String getPassword() {
    return conf.getString(ScmActivityPlugin.PASSWORD_PROPERTY);
  }

  public File getBaseDir() {
    return fileSystem.getBasedir();
  }

  public boolean isJavaProject() {
    return isJavaProject;
  }

  public boolean isIgnoreLocalModifications() {
    return isIgnoreLocalModifications;
  }

  public List<File> getSourceDirs() {
    List<File> dirs = Lists.newArrayList();
    dirs.addAll(fileSystem.getSourceDirs());
    dirs.addAll(fileSystem.getTestDirs());
    return dirs;
  }

  public boolean isVerbose() {
    return conf.getBoolean(ScmActivityPlugin.VERBOSE_PROPERTY, ScmActivityPlugin.VERBOSE_DEFAULT_VALUE);
  }

  public File getWorkdir() {
    return fileSystem.getSonarWorkingDirectory();
  }

  public String getUrl() {
    String url = conf.getString(ScmActivityPlugin.URL_PROPERTY);
    if (StringUtils.isBlank(url)) {
      url = getMavenUrl();
    }
    return StringUtils.defaultIfBlank(url, null);
  }

  private String getMavenUrl() {
    String url = null;
    if (mavenConf != null) {
      if (StringUtils.isNotBlank(mavenConf.getDeveloperUrl()) && StringUtils.isNotBlank(getUser()) && StringUtils.isNotBlank(getPassword())) {
        url = mavenConf.getDeveloperUrl();
      } else {
        url = mavenConf.getUrl();
      }
    }
    return url;
  }

}
