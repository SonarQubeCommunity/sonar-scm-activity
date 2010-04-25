package org.apache.maven.scm.provider.git.gitjava.command.list;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.list.AbstractListCommand;
import org.apache.maven.scm.command.list.ListScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaListCommand extends AbstractListCommand implements GitCommand {
  @Override
  protected ListScmResult executeListCommand(ScmProviderRepository repository, ScmFileSet fileSet, boolean recursive, ScmVersion scmVersion) throws ScmException {
    // TODO
    return null;
  }
}
