package org.apache.maven.scm.provider.svn.svnexe.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.util.AbstractConsumer;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class SvnBlameConsumer extends AbstractConsumer {
  private static final String SVN_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

  private static final String LINE_PATTERN = "line-number=\"(.*)\"";
  private static final String REVISION_PATTERN = "revision=\"(.*)\"";
  private static final String AUTHOR_PATTERN = "<author>(.*)</author>";
  private static final String DATE_PATTERN = "<date>(.*)T(.*)\\.(.*)Z</date>";

  private List<BlameLine> lines = new ArrayList<BlameLine>();

  /**
   * @see #LINE_PATTERN
   */
  private RE lineRegexp;

  /**
   * @see #REVISION_PATTERN
   */
  private RE revisionRegexp;

  /**
   * @see #AUTHOR_PATTERN
   */
  private RE authorRegexp;

  /**
   * @see #DATE_PATTERN
   */
  private RE dateRegexp;

  public SvnBlameConsumer(ScmLogger logger) {
    super(logger);

    try {
      lineRegexp = new RE(LINE_PATTERN);
      revisionRegexp = new RE(REVISION_PATTERN);
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
  private String revision;
  private String author;

  public void consumeLine(String line) {
    if (lineRegexp.match(line)) {
      String lineNumberStr = lineRegexp.getParen(1);
      lineNumber = Integer.parseInt(lineNumberStr);
    } else if (revisionRegexp.match(line)) {
      revision = revisionRegexp.getParen(1);
    } else if (authorRegexp.match(line)) {
      author = authorRegexp.getParen(1);
    } else if (dateRegexp.match(line)) {
      String date = dateRegexp.getParen(1);
      String time = dateRegexp.getParen(2);
      Date dateTime = parseDate(date + " " + time, null, SVN_TIMESTAMP_PATTERN);
      lines.add(new BlameLine(dateTime, revision, author));
      if (getLogger().isDebugEnabled()) {
        getLogger().debug("Author of line " + lineNumber + ": " + author + " (" + date + ")");
      }
    }
  }

  public List<BlameLine> getLines() {
    return lines;
  }
}
