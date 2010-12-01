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
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.Project;

public class ScmConfiguration implements BatchExtension {

  private Project project;

  public ScmConfiguration(Project project) {
    this.project = project;
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
