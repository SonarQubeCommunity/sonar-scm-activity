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

import org.sonar.gwt.Links;
import org.sonar.gwt.Utils;
import org.sonar.gwt.ui.SourcePanel;
import org.sonar.wsclient.gwt.AbstractCallback;
import org.sonar.wsclient.gwt.AbstractListCallback;
import org.sonar.wsclient.gwt.Sonar;
import org.sonar.wsclient.services.*;

import java.util.ArrayList;
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
  private Map<Integer, List<Violation>> violationsByLine = new HashMap<Integer, List<Violation>>();

  public BlamePanel(Resource resource) {
    super(resource);
    loadBlame(resource);
  }

  private void loadBlame(final Resource resource) {
    ResourceQuery query = ResourceQuery
        .createForResource(resource, BLAME_AUTHORS_DATA, BLAME_DATE_DATA, BLAME_REVISIONS_DATA);

    Sonar.getInstance().find(query, new AbstractCallback<Resource>() {
      @Override
      protected void doOnResponse(Resource result) {
        authors = convert(result.getMeasure(BLAME_AUTHORS_DATA));
        dates = convert(result.getMeasure(BLAME_DATE_DATA));
        revisions = convert(result.getMeasure(BLAME_REVISIONS_DATA));
        loadViolations(resource);
      }
    });
  }

  private void loadViolations(final Resource resource) {
    Sonar.getInstance().findAll(ViolationQuery.createForResource(resource), new AbstractListCallback<Violation>() {
      @Override
      protected void doOnResponse(List<Violation> result) {
        divideByLines(result);
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

  private void divideByLines(List<Violation> result) {
    violationsByLine.clear();
    for (Violation violation : result) {
      List<Violation> lineViolations = violationsByLine.get(violation.getLine());
      if (lineViolations == null) {
        lineViolations = new ArrayList<Violation>();
        violationsByLine.put(violation.getLine(), lineViolations);
      }
      lineViolations.add(violation);
    }
  }

  @Override
  protected boolean shouldDecorateLine(int index) {
    return index > 0;
  }

  @Override
  protected List<Row> decorateLine(int index, String source) {
    List<Row> rows = new ArrayList<Row>();
    List<Violation> lineViolations = violationsByLine.get(index);
    boolean hasViolations = lineViolations != null && !lineViolations.isEmpty();

    String style = hasViolations ? "red" : "";
    Row row = new Row().setLineIndex(index, style).unsetValue().setSource(source, style);
    String author = authors.get(index);
    String date = dates.get(index);
    String revision = revisions.get(index);
    if (author != null && revision != null && date != null) {
      row.setValue(revision + " (" + date + ")", "");
      row.setValue2(author, "");
    }
    rows.add(row);

    if (hasViolations) {
      for (Violation violation : lineViolations) {
        rows.add(new ViolationRow(violation));
      }
    }

    return rows;
  }

  /**
   * Copied from ViolationsViewer.
   */
  public static class ViolationRow extends Row {
    private Violation violation;

    public ViolationRow(Violation violation) {
      this.violation = violation;
    }

    @Override
    public String getColumn1() {
      return "<div class=\"bigln\">&nbsp;</div>";
    }

    @Override
    public String getColumn2() {
      return "";
    }

    @Override
    public String getColumn3() {
      return "";
    }

    @Override
    public String getColumn4() {
      return "<div class=\"warn\"><img src='" + Links.baseUrl() + "/images/priority/" + violation.getPriority() + ".gif'></img> "
          + "<a href=\"" + Links.urlForRule(violation.getRuleKey(), false) + "\" onclick=\"window.open(this.href,'rule','height=800,width=900,scrollbars=1,resizable=1');return false;\" title=\"" + violation.getRuleKey() + "\"><b>" + Utils.escapeHtml(violation.getRuleName()) + "</b></a> : " + Utils.escapeHtml(violation.getMessage()) + "</div>";
    }
  }
}
