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
import com.google.gwt.user.client.ui.Widget;
import org.sonar.gwt.ui.Page;
import org.sonar.wsclient.gwt.AbstractCallback;
import org.sonar.wsclient.gwt.Sonar;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

/**
 * @author Evgeny Mandrikov
 */
public class BlameViewer extends Page {
  public static final String GWT_ID = "org.sonar.plugins.scmactivity.blameviewer.BlameViewer";

  private FlowPanel panel;

  @Override
  protected Widget doOnResourceLoad(Resource resource) {
    panel = new FlowPanel();
    panel.setWidth("100%");
    load(resource);
    return panel;
  }

  public void load(final Resource resource) {
    String[] keys = resource.getKey().split(":");
    String projectKey = keys[0] + ":" + keys[1];
    ResourceQuery query = ResourceQuery.createForMetrics(projectKey, "browser");
    Sonar.getInstance().find(query, new AbstractCallback<Resource>() {
      @Override
      protected void doOnResponse(Resource project) {
        String browser = project.getMeasure("browser").getData();
        panel.clear();
        panel.add(new BlameHeader(resource, browser));
        panel.add(new BlamePanel(resource, browser));
      }
    });
  }

  public static String getBrowserLink(String browser, String revision) {
    String revisionLink = browser.replace("{rev}", revision);
    return "<a href='" + revisionLink + "'>" + revision + "</a>";
  }
}
