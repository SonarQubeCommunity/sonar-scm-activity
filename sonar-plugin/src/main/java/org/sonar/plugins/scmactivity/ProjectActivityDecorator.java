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
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.SonarException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ProjectActivityDecorator implements Decorator {
  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(CoreMetrics.SCM_LAST_COMMIT_DATE, CoreMetrics.SCM_REVISION);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (ResourceUtils.isEntity(resource) ||
        resource.getQualifier().equals(Qualifiers.VIEW) ||
        resource.getQualifier().equals(Qualifiers.SUBVIEW) ||
        context.getMeasure(CoreMetrics.SCM_LAST_COMMIT_DATE) != null) {
      return;
    }

    Date lastActivity = null;
    String lastRevision = null;

    for (DecoratorContext child : context.getChildren()) {
      Measure lastActivityMeasure = child.getMeasure(CoreMetrics.SCM_LAST_COMMIT_DATE);
      Measure revisionMeasure = child.getMeasure(CoreMetrics.SCM_REVISION);
      if (MeasureUtils.hasData(lastActivityMeasure)) {
        Date childLastActivity = convertStringMeasureToDate(lastActivityMeasure);
        if (lastActivity == null || lastActivity.before(childLastActivity)) {
          lastActivity = childLastActivity;
          lastRevision = revisionMeasure.getData();
        }
      }
    }

    if (lastActivity != null) {
      Measure lastActivityMeasure = new Measure(CoreMetrics.SCM_LAST_COMMIT_DATE, ScmUtils.formatLastActivity(lastActivity));
      context.saveMeasure(lastActivityMeasure);

      Measure revisionMeasure = new Measure(CoreMetrics.SCM_REVISION, lastRevision);
      context.saveMeasure(revisionMeasure);
    }
  }

  private Date convertStringMeasureToDate(Measure date) {
    String data = date.getData();
    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATETIME_FORMAT);
    try {
      return sdf.parse(data);
    } catch (ParseException e) {
      throw new SonarException(e);
    }
  }
}
