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
import org.sonar.gwt.ui.Page;
import org.sonar.gwt.ui.ViewerHeader;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;

/**
 * @author Evgeny Mandrikov
 */
public class BlameViewer extends Page {
  public static final String GWT_ID = "org.sonar.plugins.scmactivity.blameviewer.BlameViewer";

  @Override
  protected Widget doOnResourceLoad(Resource resource) {
    FlowPanel panel = new FlowPanel();
    panel.setWidth("100%");
    panel.add(new BlameHeader(resource));
    panel.add(new BlamePanel(resource));
    return panel;
  }

  private static class BlameHeader extends ViewerHeader {

    public BlameHeader(Resource resource) {
      super(resource, new String[]{BlamePanel.LAST_ACTIVITY, BlamePanel.REVISION});
    }

    @Override
    protected void display(FlowPanel header, Resource resource) {
      HorizontalPanel panel = new HorizontalPanel();
      header.add(panel);
      Measure m;
      m = resource.getMeasure(BlamePanel.LAST_ACTIVITY);
      if (m == null) {
        addBigCell(panel, "No data available");
      } else {
        addCell(panel, m.getMetricName(), m.getData());
        m = resource.getMeasure(BlamePanel.REVISION);
        addCell(panel, m.getMetricName(), m.getData());
      }
    }
  }
}
