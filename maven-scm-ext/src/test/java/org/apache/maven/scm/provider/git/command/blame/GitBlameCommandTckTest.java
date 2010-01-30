package org.apache.maven.scm.provider.git.command.blame;

import org.apache.maven.scm.provider.git.GitScmTestUtils;
import org.apache.maven.scm.tck.command.blame.BlameTckTest;

/**
 * @author Evgeny Mandrikov
 */
public abstract class GitBlameCommandTckTest extends BlameTckTest {
  @Override
  public void initRepo() throws Exception {
    GitScmTestUtils.initRepo("src/test/git-repository/", getRepositoryRoot(), getWorkingDirectory());
  }
}
