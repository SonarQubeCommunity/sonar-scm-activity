package org.apache.maven.scm.provider.clearcase.cleartoolexe.command.blame;

import java.util.Date;

import org.apache.maven.scm.command.blame.AbstractBlameConsumer;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.regexp.RE;

/**
 * @author Jérémie Lagarde
 */
public class ClearCaseBlameConsumer extends AbstractBlameConsumer {

  private static final String CLEARCASE_TIMESTAMP_PATTERN = "yyyyMMdd.HHmmss";
  private static final String LINE_PATTERN                = "VERSION:(.*)@@@USER:(.*)@@@DATE:(.*)@@@(.*)";

  private RE                  lineRegexp;

  public ClearCaseBlameConsumer(ScmLogger logger) {
    super(logger);
    lineRegexp = new RE(LINE_PATTERN);
  }

  public void consumeLine(String line) {
    if (lineRegexp.match(line)) {
      String revision = lineRegexp.getParen(1);
      String author = lineRegexp.getParen(2);
      String dateTimeStr = lineRegexp.getParen(3);

      Date dateTime = parseDate(dateTimeStr, null, CLEARCASE_TIMESTAMP_PATTERN);
      getLines().add(new BlameLine(dateTime, revision, author));

      if (getLogger().isDebugEnabled()) {
        getLogger().debug(author + " " + dateTimeStr);
      }
    }
  }
}
