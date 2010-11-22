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
