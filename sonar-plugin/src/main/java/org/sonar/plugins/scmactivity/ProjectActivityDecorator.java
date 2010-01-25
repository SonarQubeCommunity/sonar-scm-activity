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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Evgeny Mandrikov
 */
public class ProjectActivityDecorator implements Decorator {
  @DependedUpon
  public Metric generatesMetrics() {
    return ScmActivityMetrics.LAST_ACTIVITY;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (ResourceUtils.isEntity(resource) ||
        resource.getQualifier().equals(Resource.QUALIFIER_VIEW) ||
        resource.getQualifier().equals(Resource.QUALIFIER_SUBVIEW) ||
        context.getMeasure(ScmActivityMetrics.LAST_ACTIVITY) != null) {
      return;
    }

    Date lastActivity = null;
    String lastRevision = null;

    for (DecoratorContext child : context.getChildren()) {
      Measure lastActivityMeasure = child.getMeasure(ScmActivityMetrics.LAST_ACTIVITY);
      Measure revisionMeasure = child.getMeasure(ScmActivityMetrics.REVISION);
      if (MeasureUtils.hasData(lastActivityMeasure)) {
        Date childLastActivity = convertStringMeasureToDate(lastActivityMeasure);
        if (lastActivity == null || lastActivity.before(childLastActivity)) {
          lastActivity = childLastActivity;
          lastRevision = revisionMeasure.getData();
        }
      }
    }

    if (lastActivity != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(ScmActivityMetrics.DATE_TIME_FORMAT);

      Measure lastActivityMeasure = new Measure(ScmActivityMetrics.LAST_ACTIVITY, sdf.format(lastActivity));
      context.saveMeasure(lastActivityMeasure);

      Measure revisionMeasure = new Measure(ScmActivityMetrics.REVISION, lastRevision);
      context.saveMeasure(revisionMeasure);
    }
  }

  private Date convertStringMeasureToDate(Measure date) {
    String data = date.getData();
    SimpleDateFormat sdf = new SimpleDateFormat(ScmActivityMetrics.DATE_TIME_FORMAT);
    try {
      return sdf.parse(data);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
