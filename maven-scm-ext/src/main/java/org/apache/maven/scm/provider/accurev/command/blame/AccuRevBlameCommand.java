package org.apache.maven.scm.provider.accurev.command.blame;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.AbstractBlameCommand;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;

/**
 * @author Evgeny Mandrikov
 */
public class AccuRevBlameCommand extends AbstractBlameCommand {
  private final String accurevExecutable;

  public AccuRevBlameCommand(String accurevExec) {
    accurevExecutable = accurevExec;
  }

  public BlameScmResult executeBlameCommand(ScmProviderRepository repo, ScmFileSet workingDirectory, String filename) throws ScmException {
    Commandline cl = createCommandLine(accurevExecutable, workingDirectory.getBasedir(), filename);

    AccuRevBlameConsumer consumer = new AccuRevBlameConsumer(getLogger());

    CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

    int exitCode;

    try {
      exitCode = CommandLineUtils.executeCommandLine(cl, consumer, stderr);
    }
    catch (CommandLineException ex) {
      throw new ScmException("Error while executing command.", ex);
    }
    if (exitCode != 0) {
      return new BlameScmResult(cl.toString(), "The accurev command failed.", stderr.getOutput(), false);
    }

    return new BlameScmResult(cl.toString(), consumer.getLines());
  }

  public static Commandline createCommandLine(String accurevExecutable, File workingDirectory, String filename) {
    Commandline cl = new Commandline();
    cl.setExecutable(accurevExecutable);
    cl.setWorkingDirectory(workingDirectory);
    cl.createArg().setValue("annotate");
    cl.createArg().setValue(filename);
    return cl;
  }
}
