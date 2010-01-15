package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.log.ScmLogger;
import org.codehaus.plexus.util.cli.StreamConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class GitBlameConsumer implements StreamConsumer {
  private List<String> authors = new ArrayList<String>();
  private ScmLogger logger;

  public GitBlameConsumer(ScmLogger logger) {
    this.logger = logger;
  }

  public void consumeLine(String line) {
    logger.info(line);
  }

  public List<String> getAuthors() {
    return authors;
  }
}
