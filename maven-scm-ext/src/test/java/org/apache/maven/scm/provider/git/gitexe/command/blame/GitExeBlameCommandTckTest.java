package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.git.GitScmTestUtils;
import org.apache.maven.scm.provider.git.command.blame.GitBlameCommandTckTest;

import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class GitExeBlameCommandTckTest extends GitBlameCommandTckTest {
  @Override
  protected boolean isPureJava() {
    return false;
  }

  @Override
  public String getScmUrl() throws Exception {
    return GitScmTestUtils.getScmUrl(getRepositoryRoot(), "git");
  }

  @Override
  public void testBlameCommand() throws Exception {
    super.testBlameCommand();
  }

  @Override
  protected void verifyResult(BlameScmResult result) {
    List<BlameLine> lines = result.getLines();
    assertEquals("Expected 1 line in blame", 1, lines.size());
    BlameLine line = lines.get(0);
    assertEquals("Mark Struberg", line.getAuthor());
    assertEquals("92f139df", line.getRevision());
  }
}
