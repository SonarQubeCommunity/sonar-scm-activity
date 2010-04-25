package org.apache.maven.scm.provider.git.gitjava.command.branch;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.branch.AbstractBranchCommand;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaBranchCommand extends AbstractBranchCommand implements GitCommand {
  @Override
  protected ScmResult executeBranchCommand(ScmProviderRepository repository, ScmFileSet fileSet, String branchName, String message) throws ScmException {
    // TODO
    return null;
  }
}
