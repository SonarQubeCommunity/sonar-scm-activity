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

package org.sonar.plugins.scmactivity;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public class ScmActivityMetrics implements Metrics {

  public static final String DOMAIN_SCM = "SCM";

  /**
   * Date of the last commit.
   */
  public static final Metric LAST_ACTIVITY = new Metric(
      "last_commit",
      "Last Commit",
      "Last Commit",
      Metric.ValueType.STRING,
      Metric.DIRECTION_NONE,
      false,
      DOMAIN_SCM
  );

  /**
   * Revision.
   */
  public static final Metric REVISION = new Metric(
      "revision",
      "Revision",
      "Revision",
      Metric.ValueType.STRING,
      Metric.DIRECTION_NONE,
      false,
      DOMAIN_SCM
  );

  /**
   * Blame authors information.
   */
  public static final Metric BLAME_AUTHORS_DATA = new Metric(
      "blame_authors_data",
      "Blame data",
      "Blame data",
      Metric.ValueType.DATA,
      Metric.DIRECTION_NONE,
      false,
      DOMAIN_SCM
  );

  /**
   * Blame dates information.
   */
  public static final Metric BLAME_DATE_DATA = new Metric(
      "blame_date_data",
      "Blame date data",
      "Blame date data",
      Metric.ValueType.DATA,
      Metric.DIRECTION_NONE,
      false,
      DOMAIN_SCM
  );

  /**
   * Blame revisions information.
   */
  public static final Metric BLAME_REVISION_DATA = new Metric(
      "blame_revision_data",
      "Blame revision data",
      "Blame revision data",
      Metric.ValueType.DATA,
      Metric.DIRECTION_NONE,
      false,
      DOMAIN_SCM
  );

  /**
   * Number of commits.
   */
  public static final Metric COMMITS = new Metric(
      "commits",
      "Commits",
      "Commits",
      Metric.ValueType.INT,
      Metric.DIRECTION_NONE,
      false,
      DOMAIN_SCM
  );

  public List<Metric> getMetrics() {
    return Arrays.asList(
        LAST_ACTIVITY,
        REVISION,
        BLAME_AUTHORS_DATA,
        BLAME_DATE_DATA,
        BLAME_REVISION_DATA
    );
  }
}
