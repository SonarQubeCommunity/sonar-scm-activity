/*
 * Copyright (C) 2010 Evgeny Mandrikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.scm.provider.tfs.command.blame;

import junit.framework.Assert;
import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author Evgeny Mandrikov
 */
public class TfsBlameConsumerTest extends ScmTestCase {

  public void testConsumer() throws Exception {
    File testFile = getTestFile("src/test/resources/org/apache/maven/scm/provider/tfs/command/blame/tfs.log");

    TfsBlameConsumer consumer = new TfsBlameConsumer(new DefaultLog());

    FileInputStream fis = new FileInputStream(testFile);
    BufferedReader in = new BufferedReader(new InputStreamReader(fis));
    String s = in.readLine();
    while (s != null) {
      consumer.consumeLine(s);
      s = in.readLine();
    }

    Assert.assertEquals(3, consumer.getLines().size());

    BlameLine line1 = (BlameLine) consumer.getLines().get(0);
    Assert.assertEquals("3", line1.getRevision());
    Assert.assertEquals("hatusr01", line1.getAuthor());

    BlameLine line2 = (BlameLine) consumer.getLines().get(1);
    Assert.assertEquals("4", line2.getRevision());
    Assert.assertEquals("buckh", line2.getAuthor());
  }
}
