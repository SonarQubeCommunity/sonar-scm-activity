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

  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
        BLAME_AUTHORS_DATA,
        BLAME_DATE_DATA
    );
  }
}
