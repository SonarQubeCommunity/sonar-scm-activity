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

package org.sonar.plugins.scmactivity.blameviewer.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.sonar.gwt.ui.ViewerHeader;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;

class BlameHeader extends ViewerHeader {

  private String browser;

  public BlameHeader(Resource resource, String browser) {
    super(resource, new String[] { BlamePanel.LAST_ACTIVITY, BlamePanel.REVISION });
    this.browser = browser;
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
      String revision = m.getData();
      String revisionLink = BlameViewer.getBrowserLink(browser, revision);
      addCell(panel, m.getMetricName(), revisionLink);
    }
  }
}