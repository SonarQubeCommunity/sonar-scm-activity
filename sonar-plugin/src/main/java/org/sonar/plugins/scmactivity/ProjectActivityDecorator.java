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
    if (ResourceUtils.isEntity(resource) || context.getMeasure(ScmActivityMetrics.LAST_ACTIVITY) != null) {
      return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat(ScmActivityMetrics.DATE_TIME_FORMAT);
    Date lastActivity = null;
    for (Measure childMeasure : context.getChildrenMeasures(ScmActivityMetrics.LAST_ACTIVITY)) {
      if (MeasureUtils.hasData(childMeasure)) {
        String data = childMeasure.getData();
        Date childLastActivity;
        try {
          childLastActivity = sdf.parse(data);
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
        if (lastActivity == null || lastActivity.before(childLastActivity)) {
          lastActivity = childLastActivity;
        }
      }
    }
    if (lastActivity != null) {
      Measure lastActivityMeasure = new Measure(ScmActivityMetrics.LAST_ACTIVITY, sdf.format(lastActivity));
      context.saveMeasure(lastActivityMeasure);
    }
  }
}
