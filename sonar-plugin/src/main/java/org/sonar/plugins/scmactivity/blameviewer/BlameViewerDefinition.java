package org.sonar.plugins.scmactivity.blameviewer;

import org.sonar.api.web.ResourceViewer;
import org.sonar.plugins.scmactivity.blameviewer.client.BlameViewer;

/**
 * @author Evgeny Mandrikov
 */
public class BlameViewerDefinition implements ResourceViewer {
  public String getTitle() {
    return "Blame";
  }

  public String getGwtId() {
    return BlameViewer.GWT_ID;
  }
}
