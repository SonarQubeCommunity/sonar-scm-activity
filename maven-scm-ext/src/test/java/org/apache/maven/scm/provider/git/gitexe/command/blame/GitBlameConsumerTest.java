package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.provider.AbstractConsumerTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Evgeny Mandrikov
 */
public class GitBlameConsumerTest extends AbstractConsumerTest {
  @Test
  public void test() {
    GitBlameConsumer consumer = new GitBlameConsumer(new DefaultLog());
    consume("git.log", consumer);

    assertEquals(12, consumer.getLines().size());

    BlameLine line = consumer.getLines().get(0);
    assertEquals("c86e31d2", line.getRevision());
    assertEquals("Evgeny Mandrikov", line.getAuthor());
  }
}
