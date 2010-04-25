package org.apache.maven.scm.provider.git.gitjava.command.update;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.changelog.ChangeLogCommand;
import org.apache.maven.scm.command.update.AbstractUpdateCommand;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaUpdateCommand extends AbstractUpdateCommand implements GitCommand {
  @Override
  protected UpdateScmResult executeUpdateCommand(ScmProviderRepository repository, ScmFileSet fileSet, ScmVersion scmVersion) throws ScmException {
    // TODO
    return null;
  }

  @Override
  protected ChangeLogCommand getChangeLogCommand() {
    // TODO
    return null;
  }
}
