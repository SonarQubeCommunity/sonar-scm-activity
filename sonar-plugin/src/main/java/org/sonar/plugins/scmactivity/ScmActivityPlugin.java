package org.sonar.plugins.scmactivity;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.plugins.scmactivity.blameviewer.BlameViewerDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivityPlugin implements Plugin {
  public static final String KEY = "scm-activity";

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "SCM Activity";
  }

  public String getDescription() {
    return "SCM Activity"; // TODO
  }

  public List<Class<? extends Extension>> getExtensions() {
    return Arrays.asList(
        ScmActivityMetrics.class,
        ScmActivityWidget.class,
        ScmActivitySensor.class,
        ProjectActivityDecorator.class,
        BlameViewerDefinition.class
    );
  }
}
