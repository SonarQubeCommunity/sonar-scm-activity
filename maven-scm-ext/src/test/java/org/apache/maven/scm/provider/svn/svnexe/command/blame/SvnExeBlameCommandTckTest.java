package org.apache.maven.scm.provider.svn.svnexe.command.blame;

import org.apache.maven.scm.provider.svn.command.blame.SvnBlameCommandTckTest;

/**
 * @author Evgeny Mandrikov
 */
public class SvnExeBlameCommandTckTest extends SvnBlameCommandTckTest {
  @Override
  protected boolean isPureJava() {
    return false;
  }

  @Override
  public void testBlameCommand() throws Exception {
    super.testBlameCommand();
  }
}
