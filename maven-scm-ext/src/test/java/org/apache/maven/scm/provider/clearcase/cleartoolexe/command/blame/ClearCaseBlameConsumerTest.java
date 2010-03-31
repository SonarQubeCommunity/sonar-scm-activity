package org.apache.maven.scm.provider.clearcase.cleartoolexe.command.blame;

import junit.framework.Assert;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.provider.AbstractConsumerTest;

/**
 * @author Jérémie Lagarde
 */
public class ClearCaseBlameConsumerTest extends AbstractConsumerTest {

  public void testConsumer() {
    ClearCaseBlameConsumer consumer = new ClearCaseBlameConsumer(new DefaultLog());
    consume("clearcase.log", consumer);

    Assert.assertEquals(12, consumer.getLines().size());

    BlameLine line1 = (BlameLine) consumer.getLines().get(0);
    Assert.assertEquals("\\main\\7", line1.getRevision());
    Assert.assertEquals("Jeremie Lagarde", line1.getAuthor());

    BlameLine line12 = (BlameLine) consumer.getLines().get(11);
    Assert.assertEquals("\\main\\5", line12.getRevision());
    Assert.assertEquals("Evgeny Mandrikov", line12.getAuthor());
  }
  
}