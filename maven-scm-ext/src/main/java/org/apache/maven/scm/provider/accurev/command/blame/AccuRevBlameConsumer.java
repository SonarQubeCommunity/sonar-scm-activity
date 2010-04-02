package org.apache.maven.scm.provider.accurev.command.blame;

import org.apache.maven.scm.command.blame.AbstractBlameConsumer;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.regexp.RE;

import java.util.Date;

/**
 * @author Evgeny Mandrikov
 */
public class AccuRevBlameConsumer extends AbstractBlameConsumer {
  private static final String ACCUREV_TIMESTAMP_PATTERN = "yyyy/MM/dd HH:mm:ss";

  /* 3 godin 2009/11/18 16:26:33 */
  private static final String LINE_PATTERN = "\\s+(\\d+)\\s+(\\w+)\\s+([^ ]+ [^ ]+)";

  private RE lineRegexp;

  public AccuRevBlameConsumer(ScmLogger logger) {
    super(logger);
    lineRegexp = new RE(LINE_PATTERN);
  }

  public void consumeLine(String line) {
    if (lineRegexp.match(line)) {
      String revision = lineRegexp.getParen(1).trim();
      String author = lineRegexp.getParen(2).trim();
      String dateStr = lineRegexp.getParen(3).trim();

      if (getLogger().isDebugEnabled()) {
        getLogger().debug(author + " " + dateStr);
      }

      Date date = parseDate(dateStr, null, ACCUREV_TIMESTAMP_PATTERN);

      getLines().add(new BlameLine(date, revision, author));
    }
  }
}
