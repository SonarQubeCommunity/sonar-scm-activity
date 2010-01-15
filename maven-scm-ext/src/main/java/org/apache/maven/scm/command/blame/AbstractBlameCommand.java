package org.apache.maven.scm.command.blame;

import org.apache.maven.scm.*;
import org.apache.maven.scm.command.AbstractCommand;
import org.apache.maven.scm.provider.ScmProviderRepository;

/**
 * @author Evgeny Mandrikov
 */
public abstract class AbstractBlameCommand extends AbstractCommand {
  public abstract BlameScmResult executeBlameCommand(ScmProviderRepository repo, ScmFileSet workingDirectory, String filename)
      throws ScmException;

  @Override
  protected ScmResult executeCommand(ScmProviderRepository repository, ScmFileSet workingDirectory, CommandParameters parameters)
      throws ScmException {
    String file = parameters.getString(CommandParameter.FILE);
    return executeBlameCommand(repository, workingDirectory, file);
  }
}
