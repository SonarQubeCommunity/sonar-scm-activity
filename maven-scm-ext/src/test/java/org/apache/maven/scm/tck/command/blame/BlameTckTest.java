package org.apache.maven.scm.tck.command.blame;

import org.apache.maven.scm.ExtScmTckTestCase;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.repository.ScmRepository;

/**
 * @author Evgeny Mandrikov
 */
public abstract class BlameTckTest extends ExtScmTckTestCase {
  public void testBlameCommand()
      throws Exception {
    ScmRepository repository = getScmRepository();
    ExtScmManager manager = getScmManager();
    ScmFileSet fileSet = new ScmFileSet(getWorkingCopy());

    BlameScmResult result = manager.blame(repository, fileSet, "pom.xml");

    assertNotNull("The command returned a null result.", result);

    assertResultIsSuccess(result);

    verifyResult(result);
  }

  protected abstract void verifyResult(BlameScmResult result);
}
