package org.apache.maven.scm.provider.svn.svnexe.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.provider.AbstractConsumerTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Evgeny Mandrikov
 */
public class SvnBlameConsumerTest extends AbstractConsumerTest {
  @Test
  public void test() {
    SvnBlameConsumer consumer = new SvnBlameConsumer(new DefaultLog());
    consume("svn.log", consumer);

    assertEquals(179, consumer.getLines().size());

    BlameLine line = consumer.getLines().get(0);
    assertEquals("1016", line.getRevision());
    assertEquals("godin", line.getAuthor());
  }
}
