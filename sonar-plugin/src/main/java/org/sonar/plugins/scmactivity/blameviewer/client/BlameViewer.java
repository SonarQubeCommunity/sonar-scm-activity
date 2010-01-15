package org.sonar.plugins.scmactivity.blameviewer.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.sonar.api.web.gwt.client.AbstractViewer;
import org.sonar.api.web.gwt.client.webservices.Measure;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.WSMetrics;
import org.sonar.api.web.gwt.client.widgets.AbstractViewerHeader;

import java.util.Arrays;

/**
 * @author Evgeny Mandrikov
 */
public class BlameViewer extends AbstractViewer {
  public static final String GWT_ID = "org.sonar.plugins.scmactivity.blameviewer.BlameViewer";

  @Override
  protected String getGwtId() {
    return GWT_ID;
  }

  @Override
  protected Widget render(Resource resource) {
    FlowPanel panel = new FlowPanel();
    panel.setWidth("100%");
    panel.add(new BlameHeader(resource));
    panel.add(new BlamePanel(resource));
    return panel;
  }

  @Override
  protected void exportJavascript() {
    exportNativeJavascript(this);
  }

  public static native void exportNativeJavascript(BlameViewer obj) /*-{
    $wnd.load_org_sonar_plugins_scmactivity_blameviewer_BlameViewer = function() {
      obj.@org.sonar.plugins.scmactivity.blameviewer.client.BlameViewer::loadContainer()();
    }
    $wnd.on_resource_loaded_org_sonar_plugins_scmactivity_blameviewer_BlameViewer = function() {
      obj.@org.sonar.plugins.scmactivity.blameviewer.client.BlameViewer::onResourceLoaded()();
    }
  }-*/;

  @Override
  protected boolean isDefault(WSMetrics.Metric metric, Resource resource) {
    return metric.equals(BlamePanel.LAST_ACTIVITY)
        || metric.equals(BlamePanel.BLAME_AUTHORS_DATA)
        || metric.equals(BlamePanel.BLAME_DATE_DATA);
  }

  @Override
  protected boolean isForResource(Resource resource) {
    return resource.getScope().equals(Resource.SCOPE_ENTITY) && resource.getQualifier().equals(Resource.QUALIFIER_CLASS);
  }

  private static class BlameHeader extends AbstractViewerHeader {
    public BlameHeader(Resource resource) {
      super(resource, Arrays.asList(BlamePanel.LAST_ACTIVITY));
    }

    @Override
    protected void display(FlowPanel header, Resource resource) {
      HorizontalPanel panel = new HorizontalPanel();
      header.add(panel);

      Measure measure = resource.getMeasure(BlamePanel.LAST_ACTIVITY);
      if (measure == null) {
        addBigCell(panel, "-");
      } else {
        addBigCell(panel, measure.getFormattedValue());
      }
    }
  }
}
