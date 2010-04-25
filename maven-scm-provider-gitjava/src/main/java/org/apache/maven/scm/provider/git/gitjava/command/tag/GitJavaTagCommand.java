package org.apache.maven.scm.provider.git.gitjava.command.tag;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.ScmTagParameters;
import org.apache.maven.scm.command.tag.AbstractTagCommand;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaTagCommand extends AbstractTagCommand implements GitCommand {
  @Override
  protected ScmResult executeTagCommand(ScmProviderRepository repository, ScmFileSet fileSet, String tagName, ScmTagParameters scmTagParameters) throws ScmException {
    // TODO
    return null;
  }
}
