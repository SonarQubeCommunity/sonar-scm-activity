package org.apache.maven.scm.command.blame;

import org.apache.maven.scm.ScmResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO add lines count
 *
 * @author Evgeny Mandrikov
 */
public class BlameScmResult extends ScmResult {
  private List<BlameLine> lines;

  public BlameScmResult(String commandLine, List<String> authors, List<Date> dates, List<String> revisions) {
    this(commandLine, null, null, true);
    lines = new ArrayList<BlameLine>(authors.size());
    for (int i = 0; i < authors.size(); i++) {
      lines.add(new BlameLine(dates.get(i), revisions.get(i), authors.get(i)));
    }
  }

  public BlameScmResult(String commandLine, List<BlameLine> lines) {
    this(commandLine, null, null, true);
    this.lines = lines;
  }

  public BlameScmResult(String commandLine, String providerMessage, String commandOutput, boolean success) {
    super(commandLine, providerMessage, commandOutput, success);
  }

  public List<BlameLine> getLines() {
    return lines;
  }
}
