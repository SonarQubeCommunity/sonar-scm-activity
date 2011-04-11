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
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProvider;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;

public class UrlChecker implements BatchExtension {

  private static final String PARAMETER_MESSAGE = String.format("Please check the parameter \"%s\" or the <scm> section of Maven pom.", ScmActivityPlugin.URL_PROPERTY);

  private ScmManager manager;
  private ScmConfiguration conf;

  public UrlChecker(ScmManager manager, ScmConfiguration conf) {
    this.manager = manager;
    this.conf = conf;
  }

  public void check() {
    check(conf.getUrl());
  }

  void check(String url) {
    if (StringUtils.isBlank(url)) {
      throw new SonarException(String.format("SCM URL must not be blank. " + PARAMETER_MESSAGE));
    }
    if (!StringUtils.startsWith(url, "scm:")) {
      throw new SonarException(String.format("URL does not respect the SCM URL format described in http://maven.apache.org/scm/scm-url-format.html: \"%s\". %s", url, PARAMETER_MESSAGE));
    }
    if (!isSupportedProvider(url)) {
      throw new SonarException(String.format("SCM provider not supported: \"%s\". Compatibility matrix is available at http://docs.codehaus.org/display/SONAR/SCM+Activity+Plugin", conf.getScmProvider()));
    }
  }

  private boolean isSupportedProvider(String url) {
    try {
      ScmProvider provider = manager.getProviderByUrl(url);
      return provider != null;

    } catch (Exception e) {
      return false;
    }
  }
}
