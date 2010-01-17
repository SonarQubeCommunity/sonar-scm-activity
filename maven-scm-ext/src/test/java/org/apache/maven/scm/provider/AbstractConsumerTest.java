package org.apache.maven.scm.provider;

import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.StreamPumper;

import java.io.InputStream;

/**
 * @author Evgeny Mandrikov
 */
public abstract class AbstractConsumerTest {
  protected void consume(String resouce, StreamConsumer consumer) {
    InputStream is = getClass().getResourceAsStream(resouce);
    StreamPumper pumper = new StreamPumper(is, consumer);
    pumper.start();
    while (!pumper.isDone()) {
    }
  }
}
