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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmDecorator implements Decorator {

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(CoreMetrics.SCM_LAST_COMMIT_DATE, CoreMetrics.SCM_REVISION);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (shouldDecorate(resource, context)) {
      Date lastCommitDate = null;
      String lastRevision = null;

      for (DecoratorContext child : context.getChildren()) {
        Measure lastCommit = child.getMeasure(CoreMetrics.SCM_LAST_COMMIT_DATE);
        Measure revision = child.getMeasure(CoreMetrics.SCM_REVISION);
        if (MeasureUtils.hasData(lastCommit) && MeasureUtils.hasData(revision)) {
          Date date = ScmUtils.parseLastCommitDate(lastCommit.getData());
          if (lastCommitDate == null || lastCommitDate.before(date)) {
            lastCommitDate = date;
            lastRevision = revision.getData();
          }
        }
      }

      if (lastCommitDate != null) {
        context.saveMeasure(new Measure(CoreMetrics.SCM_LAST_COMMIT_DATE, ScmUtils.formatLastCommitDate(lastCommitDate)));
      }
      if (lastRevision != null) {
        context.saveMeasure(new Measure(CoreMetrics.SCM_REVISION, lastRevision));
      }
    }
  }

  private boolean shouldDecorate(Resource resource, DecoratorContext context) {
    return Scopes.isHigherThan(resource, Scopes.FILE) &&
        !resource.getQualifier().equals(Qualifiers.VIEW) &&
        !resource.getQualifier().equals(Qualifiers.SUBVIEW) &&
        context.getMeasure(CoreMetrics.SCM_LAST_COMMIT_DATE) == null;
  }
}
