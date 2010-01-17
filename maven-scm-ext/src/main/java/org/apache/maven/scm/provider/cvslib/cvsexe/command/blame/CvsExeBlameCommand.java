package org.apache.maven.scm.provider.cvslib.cvsexe.command.blame;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.cvslib.command.CvsCommand;

/**
 * @author Evgeny Mandrikov
 */
public class CvsExeBlameCommand extends AbstractBlameCommand implements CvsCommand {
  @Override
  public BlameScmResult executeBlameCommand(ScmProviderRepository repo, ScmFileSet workingDirectory, String filename) throws ScmException {
    // TODO
    return null;
  }
}
