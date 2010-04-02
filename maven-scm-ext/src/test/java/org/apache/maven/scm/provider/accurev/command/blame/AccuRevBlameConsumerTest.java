package org.apache.maven.scm.provider.accurev.command.blame;

import junit.framework.Assert;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.provider.AbstractConsumerTest;

/**
 * @author Evgeny Mandrikov
 */
public class AccuRevBlameConsumerTest extends AbstractConsumerTest {

  public void testConsumer() {
    AccuRevBlameConsumer consumer = new AccuRevBlameConsumer(new DefaultLog());
    consume("accurev.log", consumer);

    Assert.assertEquals(12, consumer.getLines().size());

    BlameLine line1 = (BlameLine) consumer.getLines().get(0);
    Assert.assertEquals("2", line1.getRevision());
    Assert.assertEquals("godin", line1.getAuthor());

    BlameLine line12 = (BlameLine) consumer.getLines().get(11);
    Assert.assertEquals("1", line12.getRevision());
    Assert.assertEquals("godin", line12.getAuthor());
  }

}
