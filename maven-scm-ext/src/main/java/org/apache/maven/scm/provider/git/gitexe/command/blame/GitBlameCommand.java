package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.git.command.GitCommand;
import org.apache.maven.scm.provider.git.gitexe.command.GitCommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;

/**
 * @author Evgeny Mandrikov
 */
public class GitBlameCommand extends AbstractBlameCommand implements GitCommand {
  @Override
  public BlameScmResult executeBlameCommand(ScmProviderRepository repo, ScmFileSet workingDirectory, String filename)
      throws ScmException {
    Commandline cl = createCommandLine(workingDirectory.getBasedir(), filename);
    GitBlameConsumer consumer = new GitBlameConsumer(getLogger());
    CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

    int exitCode = GitCommandLineUtils.execute(cl, consumer, stderr, getLogger());
    if (exitCode != 0) {
      throw new UnsupportedOperationException();
    }
    return new BlameScmResult(cl.toString(), consumer.getLines());
  }

  public static Commandline createCommandLine(File workingDirectory, String filename) {
    Commandline cl = GitCommandLineUtils.getBaseGitCommandLine(workingDirectory, "blame");
    cl.createArg().setValue("-c");
    cl.createArg().setValue(filename);
    return cl;
  }
}
