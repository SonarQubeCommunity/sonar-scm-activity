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

import org.sonar.gwt.ui.SourcePanel;
import org.sonar.wsclient.gwt.AbstractCallback;
import org.sonar.wsclient.gwt.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Evgeny Mandrikov
 */
public class BlamePanel extends SourcePanel {
  public static final String BLAME_AUTHORS_DATA = "blame_authors_data";
  public static final String BLAME_DATE_DATA = "blame_date_data";
  public static final String BLAME_REVISIONS_DATA = "blame_revision_data";
  public static final String LAST_ACTIVITY = "last_commit";
  public static final String REVISION = "revision";

  private Map<Integer, String> authors = new HashMap<Integer, String>();
  private Map<Integer, String> dates = new HashMap<Integer, String>();
  private Map<Integer, String> revisions = new HashMap<Integer, String>();

  public BlamePanel(Resource resource) {
    super(resource);
    loadBlame(resource);
  }

  private void loadBlame(Resource resource) {
    ResourceQuery query = ResourceQuery
        .createForResource(resource, BLAME_AUTHORS_DATA, BLAME_DATE_DATA, BLAME_REVISIONS_DATA);
    Sonar.getInstance().find(query, new AbstractCallback<Resource>() {
      @Override
      protected void doOnResponse(Resource result) {
        authors = convert(result.getMeasure(BLAME_AUTHORS_DATA));
        dates = convert(result.getMeasure(BLAME_DATE_DATA));
        revisions = convert(result.getMeasure(BLAME_REVISIONS_DATA));
        setStarted();
      }
    });
  }

  private Map<Integer, String> convert(Measure measure) {
    Map<String, String> map = measure.getDataAsMap(";");
    Map<Integer, String> result = new HashMap<Integer, String>();
    for (Map.Entry<String, String> entry : map.entrySet()) {
      result.put(Integer.parseInt(entry.getKey()), entry.getValue());
    }
    return result;
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
