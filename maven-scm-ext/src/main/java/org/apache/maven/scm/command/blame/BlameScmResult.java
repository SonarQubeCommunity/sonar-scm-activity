package org.apache.maven.scm.command.blame;

import org.apache.maven.scm.ScmResult;

import java.util.Date;
import java.util.List;

/**
 * TODO add lines count
 *
 * @author Evgeny Mandrikov
 */
public class BlameScmResult extends ScmResult {
  private List<String> authors;
  private List<Date> dates;

  public BlameScmResult(String commandLine, List<String> authors, List<Date> dates) {
    this(commandLine, null, null, true);
    this.authors = authors;
    this.dates = dates;
  }

  public BlameScmResult(String commandLine, String providerMessage, String commandOutput, boolean success) {
    super(commandLine, providerMessage, commandOutput, success);
  }

  public List<String> getAuthors() {
    return authors;
  }

  public List<Date> getDates() {
    return dates;
  }
}
