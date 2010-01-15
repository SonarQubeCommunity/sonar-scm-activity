package org.apache.maven.scm.provider.svn.svnexe.command.blame;

import org.apache.maven.scm.log.ScmLogger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class SvnBlameConsumer implements StreamConsumer {
  private static final String LINE_PATTERN = "line-number=\"(.*)\"";
  private static final String AUTHOR_PATTERN = "<author>(.*)</author>";
  private static final String DATE_PATTERN = "<date>(.*)T(.*)\\.(.*)Z</date>";

  private ScmLogger logger;
  private List<String> authors = new ArrayList<String>();
  private List<Date> dates = new ArrayList<Date>();

  /**
   * @see #LINE_PATTERN
   */
  private RE lineRegexp;

  /**
   * @see #AUTHOR_PATTERN
   */
  private RE authorRegexp;

  /**
   * @see #DATE_PATTERN
   */
  private RE dateRegexp;

  public SvnBlameConsumer(ScmLogger logger) {
    this.logger = logger;

    try {
      lineRegexp = new RE(LINE_PATTERN);
      authorRegexp = new RE(AUTHOR_PATTERN);
      dateRegexp = new RE(DATE_PATTERN);
    }
    catch (RESyntaxException ex) {
      throw new RuntimeException(
          "INTERNAL ERROR: Could not create regexp to parse git log file. This shouldn't happen. Something is probably wrong with the oro installation.",
          ex);
    }
  }

  private int lineNumber;
  private String author;

  public void consumeLine(String line) {
    if (lineRegexp.match(line)) {
      String lineNumberStr = lineRegexp.getParen(1);
      lineNumber = Integer.parseInt(lineNumberStr);
    } else if (authorRegexp.match(line)) {
      author = authorRegexp.getParen(1);
    } else if (dateRegexp.match(line)) {
      String date = dateRegexp.getParen(1);
      String time = dateRegexp.getParen(2);

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date dateTime;
      try {
        dateTime = sdf.parse(date + " " + time);
      } catch (ParseException e) {
        throw new RuntimeException("INTERNAL ERROR: Could not parse date");
      }

      authors.add(author);
      dates.add(dateTime);

      if (logger.isDebugEnabled()) {
        logger.debug("Author of line " + lineNumber + ": " + author + " (" + date + ")");
      }
    }
  }

  public List<String> getAuthors() {
    return authors;
  }

  public List<Date> getDates() {
    return dates;
  }
}
