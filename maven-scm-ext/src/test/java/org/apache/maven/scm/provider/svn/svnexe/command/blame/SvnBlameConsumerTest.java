package org.apache.maven.scm.provider.svn.svnexe.command.blame;

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

    final int linesCount = 179;

    assertEquals(linesCount, consumer.getRevisions().size());
    assertEquals("1016", consumer.getRevisions().get(0));

    assertEquals(linesCount, consumer.getAuthors().size());
    assertEquals("godin", consumer.getAuthors().get(0));

    assertEquals(linesCount, consumer.getDates().size());
  }
}
