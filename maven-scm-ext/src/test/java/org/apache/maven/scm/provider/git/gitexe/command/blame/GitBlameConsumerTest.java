package org.apache.maven.scm.provider.git.gitexe.command.blame;

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

    final int linesCount = 12;

    assertEquals(linesCount, consumer.getRevisions().size());
    assertEquals("c86e31d2", consumer.getRevisions().get(0));

    assertEquals(linesCount, consumer.getAuthors().size());
    assertEquals("Evgeny Mandrikov", consumer.getAuthors().get(0));

    assertEquals(linesCount, consumer.getDates().size());
  }
}
