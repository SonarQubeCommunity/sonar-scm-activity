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

package org.apache.maven.scm.provider.svn.svnexe.command.blame;

import org.apache.maven.scm.command.blame.AbstractBlameConsumer;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import java.util.Date;

/**
 * @author Evgeny Mandrikov
 */
public class SvnBlameConsumer extends AbstractBlameConsumer {
  private static final String SVN_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

  private static final String LINE_PATTERN = "line-number=\"(.*)\"";
  private static final String REVISION_PATTERN = "revision=\"(.*)\"";
  private static final String AUTHOR_PATTERN = "<author>(.*)</author>";
  private static final String DATE_PATTERN = "<date>(.*)T(.*)\\.(.*)Z</date>";

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
      getLines().add(new BlameLine(dateTime, revision, author));
      if (getLogger().isDebugEnabled()) {
        getLogger().debug("Author of line " + lineNumber + ": " + author + " (" + date + ")");
      }
    }
  }
}
