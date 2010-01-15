package org.apache.maven.scm.manager;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.cvslib.cvsexe.command.blame.CvsExeBlameCommand;
import org.apache.maven.scm.provider.cvslib.repository.CvsScmProviderRepository;
import org.apache.maven.scm.provider.git.gitexe.command.blame.GitBlameCommand;
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository;
import org.apache.maven.scm.provider.hg.command.blame.HgBlameCommand;
import org.apache.maven.scm.provider.hg.repository.HgScmProviderRepository;
import org.apache.maven.scm.provider.svn.repository.SvnScmProviderRepository;
import org.apache.maven.scm.provider.svn.svnexe.command.blame.SvnBlameCommand;
import org.apache.maven.scm.repository.ScmRepository;

/**
 * @author Evgeny Mandrikov
 */
public class ExtScmManager extends AbstractScmManager {
  private ScmLogger logger;

  public ExtScmManager() {
    this(new DefaultLog());
  }

  public ExtScmManager(ScmLogger logger) {
    this.logger = logger;
  }

  @Override
  protected ScmLogger getScmLogger() {
    return logger;
  }

  protected AbstractBlameCommand getBlameCommand(ScmProviderRepository providerRepository) throws ScmException {
    if (providerRepository instanceof SvnScmProviderRepository) {
      return new SvnBlameCommand();
    } else if (providerRepository instanceof GitScmProviderRepository) {
      return new GitBlameCommand();
    } else if (providerRepository instanceof HgScmProviderRepository) {
      return new HgBlameCommand();
    } else if (providerRepository instanceof CvsScmProviderRepository) {
      return new CvsExeBlameCommand();
    } else {
      throw new ScmException("Unsupported repository provider: " + providerRepository.toString());
    }
  }

  public BlameScmResult blame(ScmRepository repository, ScmFileSet workingDirectory, String filename) throws ScmException {
    ScmProviderRepository providerRepository = repository.getProviderRepository();
    AbstractBlameCommand blameCommand = getBlameCommand(providerRepository);
    blameCommand.setLogger(getScmLogger());
    return blameCommand.executeBlameCommand(providerRepository, workingDirectory, filename);
  }
}
