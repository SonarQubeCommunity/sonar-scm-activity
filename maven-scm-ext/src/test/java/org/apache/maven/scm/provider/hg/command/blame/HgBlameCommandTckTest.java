package org.apache.maven.scm.provider.hg.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.hg.HgRepoUtils;
import org.apache.maven.scm.tck.command.blame.BlameTckTest;

import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class HgBlameCommandTckTest extends BlameTckTest {
  @Override
  protected boolean isPureJava() {
    return false;
  }

  @Override
  public String getScmUrl() throws Exception {
    return HgRepoUtils.getScmUrl();
  }

  @Override
  public void initRepo() throws Exception {
    HgRepoUtils.initRepo();
  }

  @Override
  protected void verifyResult(BlameScmResult result) {
    List<BlameLine> lines = result.getLines();
    assertEquals("Expected 1 line in blame", 1, lines.size());
    BlameLine line = lines.get(0);
    assertEquals(System.getProperty("user.name"), line.getAuthor());
    assertEquals("0", line.getRevision());
  }
}
