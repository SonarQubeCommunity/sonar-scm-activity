package org.apache.maven.scm.provider.git.gitjava.command.add;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.add.AbstractAddCommand;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaAddCommand extends AbstractAddCommand implements GitCommand {
  @Override
  protected ScmResult executeAddCommand(ScmProviderRepository repository, ScmFileSet fileSet, String message, boolean binary) throws ScmException {
    // TODO
    return null;
  }
}
