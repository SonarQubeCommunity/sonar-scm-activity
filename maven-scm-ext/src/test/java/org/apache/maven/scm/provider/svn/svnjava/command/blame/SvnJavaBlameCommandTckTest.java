package org.apache.maven.scm.provider.svn.svnjava.command.blame;

import org.apache.maven.scm.provider.svn.command.blame.SvnBlameCommandTckTest;

/**
 * @author Evgeny Mandrikov
 */
public class SvnJavaBlameCommandTckTest extends SvnBlameCommandTckTest {
  @Override
  protected boolean isPureJava() {
    return true;
  }

  @Override
  public void testBlameCommand() throws Exception {
    // FIXME super.testBlameCommand();
  }
}
