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

import com.google.common.collect.ImmutableList;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.provider.ScmUrlUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;
import java.util.List;

public class ScmConfiguration implements BatchExtension {
  private final ProjectFileSystem fileSystem;
  private final Configuration configuration;
  private final MavenScmConfiguration mavenConfonfiguration;

  public ScmConfiguration(ProjectFileSystem fileSystem, Configuration configuration, MavenScmConfiguration mavenConfiguration) {
    this.configuration = configuration;
    this.fileSystem = fileSystem;
    this.mavenConfonfiguration = mavenConfiguration;
  }

  public ScmConfiguration(ProjectFileSystem fileSystem, Configuration configuration) {
    this(fileSystem, configuration, null /* not in maven environment */);
  }

  public boolean isEnabled() {
    return configuration.getBoolean(ScmActivityPlugin.ENABLED_PROPERTY, ScmActivityPlugin.ENABLED_DEFAULT_VALUE) && (getUrl() != null);
  }

  public String getScmProvider() {
    String url = getUrl();
    if (StringUtils.isBlank(url)) {
      return null;
    }

    return ScmUrlUtils.getProvider(url);
  }

  public String getUser() {
    return configuration.getString(ScmActivityPlugin.USER_PROPERTY);
  }

  public String getPassword() {
    return configuration.getString(ScmActivityPlugin.PASSWORD_PROPERTY);
  }

  public boolean isIgnoreLocalModifications() {
    return configuration.getBoolean(ScmActivityPlugin.IGNORE_LOCAL_MODIFICATIONS, ScmActivityPlugin.IGNORE_LOCAL_MODIFICATIONS_DEFAULT_VALUE);
  }

  public List<File> getSourceDirs() {
    return ImmutableList.<File> builder()
        .addAll(fileSystem.getSourceDirs())
        .addAll(fileSystem.getTestDirs())
        .build();
  }

  public String getUrl() {
    String url = configuration.getString(ScmActivityPlugin.URL_PROPERTY);
    if (StringUtils.isBlank(url)) {
      url = getMavenUrl();
    }
    return StringUtils.defaultIfBlank(url, null);
  }

  private String getMavenUrl() {
    String url = null;
    if (mavenConfonfiguration != null) {
      if (StringUtils.isNotBlank(mavenConfonfiguration.getDeveloperUrl()) && StringUtils.isNotBlank(getUser())) {
        url = mavenConfonfiguration.getDeveloperUrl();
      } else {
        url = mavenConfonfiguration.getUrl();
      }
    }
    return url;
  }

}
