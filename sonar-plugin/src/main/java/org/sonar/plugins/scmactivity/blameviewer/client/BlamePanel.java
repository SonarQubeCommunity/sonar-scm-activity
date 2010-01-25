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

import com.google.gwt.core.client.JavaScriptObject;
import org.sonar.api.web.gwt.client.webservices.*;
import org.sonar.api.web.gwt.client.widgets.AbstractSourcePanel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Evgeny Mandrikov
 */
public class BlamePanel extends AbstractSourcePanel {
  public static final WSMetrics.Metric BLAME_AUTHORS_DATA = new WSMetrics.Metric("blame_authors_data");
  public static final WSMetrics.Metric BLAME_DATE_DATA = new WSMetrics.Metric("blame_date_data");
  public static final WSMetrics.Metric BLAME_REVISIONS_DATA = new WSMetrics.Metric("blame_revision_data");
  public static final WSMetrics.Metric LAST_ACTIVITY = new WSMetrics.Metric("last_commit");
  public static final WSMetrics.Metric REVISION = new WSMetrics.Metric("revision");

  private Map<Integer, String> authors = new HashMap<Integer, String>();
  private Map<Integer, String> dates = new HashMap<Integer, String>();
  private Map<Integer, String> revisions = new HashMap<Integer, String>();

  public BlamePanel(Resource resource) {
    super(resource);
    loadBlame();
  }

  private void loadBlame() {
    ResourcesQuery.get(getResource().getKey())
        .setMetrics(Arrays.asList(BLAME_AUTHORS_DATA, BLAME_DATE_DATA, BLAME_REVISIONS_DATA))
        .execute(new BaseQueryCallback<Resources>() {
          public void onResponse(Resources response, JavaScriptObject jsonRawResponse) {
            handleResponse(response, BLAME_AUTHORS_DATA, authors);
            handleResponse(response, BLAME_DATE_DATA, dates);
            handleResponse(response, BLAME_REVISIONS_DATA, revisions);
            setStarted();
          }
        });
  }

  private void handleResponse(Resources response, WSMetrics.Metric metric, Map<Integer, String> values) {
    if (response.getResources().size() != 1 || !response.firstResource().hasMeasure(metric)) {
      return;
    }
    values.clear();
    String linesValue = response.getResources().get(0).getMeasure(metric).getData();
    String[] lineWithValueArray = linesValue.split(";");
    for (String lineWithValue : lineWithValueArray) {
      String[] elt = lineWithValue.split("=");
      if (elt != null && elt.length == 2) {
        values.put(Integer.parseInt(elt[0]), elt[1]);
      }
    }
  }

  @Override
  protected boolean shouldDecorateLine(int index) {
    return index > 0;
  }

  @Override
  protected List<Row> decorateLine(int index, String source) {
    String author = authors.get(index);
    String date = dates.get(index);
    String revision = revisions.get(index);
    Row row = new Row(index, source);
    if (author != null && revision != null && date != null) {
      row.setValue(revision + " (" + date + ")", "");
      row.setValue2(author, "");
    }
    return Arrays.asList(row);
  }
}
