package org.apache.maven.scm.provider.git.gitjava.command.status;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.status.AbstractStatusCommand;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaStatusCommand extends AbstractStatusCommand implements GitCommand {
  @Override
  protected StatusScmResult executeStatusCommand(ScmProviderRepository repository, ScmFileSet fileSet) throws ScmException {
    // TODO
    return null;
  }
}
