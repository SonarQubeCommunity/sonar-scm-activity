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

package org.sonar.plugins.scmactivity;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

/**
 * TODO
 * <p/>
 * 1) Number of created/modified/deleted files during the past x weeks
 * (x could be a configuration property of the plugin)
 * <p/>
 * 2) Store the information at package and file level to be able to drilldown to the source code.
 * We can also imagine to add three new metrics : number of added/deleted and updated lines.
 * <p/>
 * 3) Use the blame command (available for instance with CVS, Subversion and GIT),
 * to know who is the last commiter on any line of code and decorate the source code viewer to display those names.
 */
public class ScmActivityMetrics implements Metrics {

  public static final String DOMAIN_SCM = "SCM";

  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd";

  /**
   * Date of the last commit.
   */
  public static final Metric LAST_ACTIVITY = new Metric(
      "last_commit", // Key
      "Last Commit", // Name
      "Last Commit", // Description
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

  /**
   * Used by Sonar to retrieve the list of new Metric.
   * {@inheritDoc}
   */
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
