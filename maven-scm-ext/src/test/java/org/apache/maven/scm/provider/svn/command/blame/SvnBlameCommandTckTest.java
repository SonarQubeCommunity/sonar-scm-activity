package org.apache.maven.scm.provider.svn.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.svn.SvnScmTestUtils;
import org.apache.maven.scm.tck.command.blame.BlameTckTest;

import java.io.File;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public abstract class SvnBlameCommandTckTest extends BlameTckTest {
  @Override
  public String getScmUrl() throws Exception {
    return SvnScmTestUtils.getScmUrl(new File(getRepositoryRoot(), "trunk"));
  }

  @Override
  public void initRepo() throws Exception {
    SvnScmTestUtils.initializeRepository(getRepositoryRoot());
  }

  @Override
  protected void verifyResult(BlameScmResult result) {
    List<BlameLine> lines = result.getLines();
    assertEquals("Expected 1 line in blame", 1, lines.size());
    BlameLine line = lines.get(0);
    assertEquals("trygvis", line.getAuthor());
    assertEquals("7", line.getRevision());
  }
}
