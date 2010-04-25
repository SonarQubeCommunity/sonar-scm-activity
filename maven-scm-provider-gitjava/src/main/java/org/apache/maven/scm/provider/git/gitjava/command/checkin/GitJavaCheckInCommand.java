package org.apache.maven.scm.provider.git.gitjava.command.checkin;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.checkin.AbstractCheckInCommand;
import org.apache.maven.scm.command.checkin.CheckInScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaCheckInCommand extends AbstractCheckInCommand implements GitCommand {
  @Override
  protected CheckInScmResult executeCheckInCommand(ScmProviderRepository repository, ScmFileSet fileSet, String message, ScmVersion scmVersion) throws ScmException {
    // TODO
    return null;
  }
}
