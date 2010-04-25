package org.apache.maven.scm.provider.git.gitjava.command.changelog;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.changelog.AbstractChangeLogCommand;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;

import java.util.Date;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaChangeLogCommand extends AbstractChangeLogCommand implements GitCommand {
  @Override
  protected ChangeLogScmResult executeChangeLogCommand(ScmProviderRepository repository, ScmFileSet fileSet, Date startDate, Date endDate, ScmBranch branch, String datePattern) throws ScmException {
    // TODO
    return null;
  }
}
