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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Scm;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.manager.SonarScmManagerFactory;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.Logs;
import org.sonar.api.utils.SonarException;

public class ScmConfiguration implements BatchExtension {

  private Project project;
  private ScmManager scmManager;
  private ScmRepository scmRepository;

  public ScmConfiguration(Project project) {
    this.project = project;
  }

  public ScmManager getScmManager() {
    if (scmManager == null) {
      scmManager = SonarScmManagerFactory.getScmManager(isPureJava());
    }
    return scmManager;
  }

  public ScmRepository getScmRepository() {
    try {
      if (scmRepository == null) {
        String connectionUrl = getUrl();
        Logs.INFO.info("SCM connection URL: {}", connectionUrl);
        scmRepository = getScmManager().makeScmRepository(connectionUrl);
        String user = getUser();
        String password = getPassword();
        if (!StringUtils.isBlank(user) && !StringUtils.isBlank(password)) {
          ScmProviderRepository providerRepository = scmRepository.getProviderRepository();
          providerRepository.setUser(user);
          providerRepository.setPassword(password);
        }
      }
      return scmRepository;
    } catch (ScmException e) {
      throw new SonarException(e);
    }
  }

  private Configuration getConfiguration() {
    return project.getConfiguration();
  }

  public boolean isEnabled() {
    return getConfiguration().getBoolean(ScmActivityPlugin.ENABLED_PROPERTY, ScmActivityPlugin.ENABLED_DEFAULT_VALUE);
  }

  public boolean isPureJava() {
    return getConfiguration().getBoolean(ScmActivityPlugin.PREFER_PURE_JAVA_PROPERTY, ScmActivityPlugin.PREFER_PURE_JAVA_DEFAULT_VALUE);
  }

  public String getUser() {
    return getConfiguration().getString(ScmActivityPlugin.USER_PROPERTY);
  }

  public String getPassword() {
    return getConfiguration().getString(ScmActivityPlugin.PASSWORD_PROPERTY);
  }

  public String getUrl() {
    String url = getConfiguration().getString(ScmActivityPlugin.URL_PROPERTY);
    if (StringUtils.isNotBlank(url)) {
      return url;
    }
    Scm scm = project.getPom().getScm();
    if (scm != null) {
      if (!StringUtils.isBlank(getUser()) && !StringUtils.isBlank(getPassword())) {
        return scm.getDeveloperConnection();
      } else {
        return scm.getConnection();
      }
    }
    return null;
  }

}
