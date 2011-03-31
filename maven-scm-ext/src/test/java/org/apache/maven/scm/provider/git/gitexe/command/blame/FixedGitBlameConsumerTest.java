/*
 * Sonar SCM Activity Plugin :: Maven SCM Ext
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.apache.maven.scm.provider.git.gitexe.command.blame;

import junit.framework.Assert;
import org.apache.maven.scm.ScmTestCase;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;

import java.io.*;

public class FixedGitBlameConsumerTest extends ScmTestCase {

  public void testParse() throws IOException {
    File testFile = getTestFile("src/test/resources/git/blamelog.txt");

    FixedGitBlameConsumer consumer = new FixedGitBlameConsumer(new DefaultLog());

    FileInputStream fis = new FileInputStream(testFile);
    BufferedReader in = new BufferedReader(new InputStreamReader(fis));
    String s = in.readLine();
    while (s != null) {
      consumer.consumeLine(s);
      s = in.readLine();
    }

    Assert.assertEquals(73, consumer.getLines().size());

    BlameLine line1 = (BlameLine) consumer.getLines().get(0);
    Assert.assertEquals("8748a722", line1.getRevision());

    BlameLine line2 = (BlameLine) consumer.getLines().get(11);
    Assert.assertEquals("96cfe5d4", line2.getRevision());
  }

}
