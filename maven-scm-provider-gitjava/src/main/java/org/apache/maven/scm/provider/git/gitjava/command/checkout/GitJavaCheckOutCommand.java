package org.apache.maven.scm.provider.git.gitjava.command.checkout;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.checkout.AbstractCheckOutCommand;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaCheckOutCommand extends AbstractCheckOutCommand implements GitCommand {
  @Override
  protected CheckOutScmResult executeCheckOutCommand(ScmProviderRepository repository, ScmFileSet fileSet, ScmVersion scmVersion, boolean recursive) throws ScmException {
    // TODO
    return null;
  }
}
