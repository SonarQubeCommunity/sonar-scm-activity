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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sonar.api.utils.SonarException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.provider.ScmUrlUtils;
import org.sonar.api.BatchExtension;

public class ScmConfiguration implements BatchExtension {
  private static final Logger LOG = LoggerFactory.getLogger(ScmConfiguration.class);

  private final Configuration configuration;
  private final MavenScmConfiguration mavenConfonfiguration;

  public ScmConfiguration(Configuration configuration, MavenScmConfiguration mavenConfiguration) {
    this.configuration = configuration;
    this.mavenConfonfiguration = mavenConfiguration;
  }

  public ScmConfiguration(Configuration configuration) {
    this(configuration, null /* not in maven environment */);
  }

  public String getScmProvider() {
    String url = getUrl();
    if (StringUtils.isBlank(url)) {
      return null;
    }

    return ScmUrlUtils.getProvider(url);
  }

  public boolean isEnabled() {
    return configuration.getBoolean(ScmActivityPlugin.ENABLED, ScmActivityPlugin.ENABLED_DEFAULT) && (getUrl() != null);
  }

  public String getUser() {
    return configuration.getString(ScmActivityPlugin.USER);
  }

  public String getPassword() {
    return configuration.getString(ScmActivityPlugin.PASSWORD);
  }

  public int getThreadCount() {
    int threadCount = configuration.getInt(ScmActivityPlugin.THREAD_COUNT, ScmActivityPlugin.THREAD_COUNT_DEFAULT);

    if (threadCount < 1) {
      throw new SonarException(String.format("SCM Activity Plugin is configured to use [%d] thread(s). The minimum is 1.", threadCount));
    }
    if (threadCount > Runtime.getRuntime().availableProcessors()) {
      LOG.warn("SCM Activity Plugin is configured to use more threads than actually available on this machine.");
    }

    return threadCount;
  }

  public String getUrl() {
    String urlProperty = configuration.getString(ScmActivityPlugin.URL);
    String url = StringUtils.defaultIfBlank(urlProperty, getMavenUrl());
    return StringUtils.defaultIfBlank(url, null);
  }

  private String getMavenUrl() {
    if (mavenConfonfiguration == null) {
      return null;
    }
    if (StringUtils.isNotBlank(mavenConfonfiguration.getDeveloperUrl()) && StringUtils.isNotBlank(getUser())) {
      return mavenConfonfiguration.getDeveloperUrl();
    }
    return mavenConfonfiguration.getUrl();
  }
}
