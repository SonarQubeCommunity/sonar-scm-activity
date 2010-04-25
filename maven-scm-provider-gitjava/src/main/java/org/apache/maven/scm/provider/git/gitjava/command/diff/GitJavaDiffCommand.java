package org.apache.maven.scm.provider.git.gitjava.command.diff;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.diff.AbstractDiffCommand;
import org.apache.maven.scm.command.diff.DiffScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaDiffCommand extends AbstractDiffCommand implements GitCommand {
  @Override
  protected DiffScmResult executeDiffCommand(ScmProviderRepository repository, ScmFileSet fileSet, ScmVersion startRevision, ScmVersion endRevision) throws ScmException {
    // TODO
    return null;
  }
}
