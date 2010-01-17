package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.log.ScmLogger;
import org.apache.regexp.RE;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class GitBlameConsumer implements StreamConsumer {
  private static final String LINE_PATTERN = "(.*)\t\\((.*)\t(.*)\t.*\\)";

  private List<String> revisions = new ArrayList<String>();
  private List<String> authors = new ArrayList<String>();
  private List<Date> dates = new ArrayList<Date>();
  private ScmLogger logger;

  /**
   * @see #LINE_PATTERN
   */
  private RE lineRegexp;

  public GitBlameConsumer(ScmLogger logger) {
    this.logger = logger;

    lineRegexp = new RE(LINE_PATTERN);
  }

  public void consumeLine(String line) {
    if (lineRegexp.match(line)) {
      String revision = lineRegexp.getParen(1);
      String author = lineRegexp.getParen(2);
      String dateTimeStr = lineRegexp.getParen(3);

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
      Date dateTime;
      try {
        dateTime = sdf.parse(dateTimeStr);
      } catch (ParseException e) {
        throw new RuntimeException("INTERNAL ERROR: Could not parse date");
      }

      revisions.add(revision);
      authors.add(author);
      dates.add(dateTime);

      if (logger.isDebugEnabled()) {
        logger.debug(author + " " + dateTimeStr);
      }
    }
  }

  public List<String> getRevisions() {
    return revisions;
  }

  public List<String> getAuthors() {
    return authors;
  }

  public List<Date> getDates() {
    return dates;
  }
}
