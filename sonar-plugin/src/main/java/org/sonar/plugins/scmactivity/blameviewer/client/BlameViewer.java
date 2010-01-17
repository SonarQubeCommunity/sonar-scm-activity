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
    };
    $wnd.on_resource_loaded_org_sonar_plugins_scmactivity_blameviewer_BlameViewer = function() {
      obj.@org.sonar.plugins.scmactivity.blameviewer.client.BlameViewer::onResourceLoaded()();
    };
  }-*/;

  @Override
  protected boolean isDefault(WSMetrics.Metric metric, Resource resource) {
    return metric.equals(BlamePanel.LAST_ACTIVITY)
        || metric.equals(BlamePanel.REVISION)
        || metric.equals(BlamePanel.BLAME_AUTHORS_DATA)
        || metric.equals(BlamePanel.BLAME_DATE_DATA);
  }

  @Override
  protected boolean isForResource(Resource resource) {
    return resource.getScope().equals(Resource.SCOPE_ENTITY)
        && resource.getQualifier().equals(Resource.QUALIFIER_CLASS);
  }

  private static class BlameHeader extends AbstractViewerHeader {
    public BlameHeader(Resource resource) {
      super(resource, Arrays.asList(BlamePanel.LAST_ACTIVITY, BlamePanel.REVISION));
    }

    @Override
    protected void display(FlowPanel header, Resource resource) {
      HorizontalPanel panel = new HorizontalPanel();
      header.add(panel);
      Measure m;
      m = resource.getMeasure(BlamePanel.LAST_ACTIVITY);
      if (m != null) {
        addCell(panel, m.getMetricName(), m.getData());
      }
      m = resource.getMeasure(BlamePanel.REVISION);
      if (m != null) {
        addCell(panel, m.getMetricName(), m.getData());
      }
    }
  }
}
