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
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

public class ScmLinksSensor implements Sensor {

  private ScmConfiguration scmConfiguration;

  public ScmLinksSensor(ScmConfiguration scmConfiguration) {
    this.scmConfiguration = scmConfiguration;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return StringUtils.isNotBlank(scmConfiguration.getBrowserUrlTemplate());
  }

  public void analyse(Project project, SensorContext context) {
    context.saveMeasure(new Measure(ScmActivityMetrics.BROWSER, scmConfiguration.getBrowserUrlTemplate()));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
