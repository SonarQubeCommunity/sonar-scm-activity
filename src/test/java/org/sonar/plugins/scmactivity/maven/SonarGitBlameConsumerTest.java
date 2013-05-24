/*
 * Sonar SCM Activity Plugin
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

package org.sonar.plugins.scmactivity.maven;

import junit.framework.Assert;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.provider.git.gitexe.command.blame.GitBlameConsumer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Plain copy of org.apache.maven.scm.provider.git.gitexe.command.blame.GitBlameConsumerTest
 *
 * @Todo: hack - to be submitted as an update in maven-scm-api for a future release
 * <p/>
 * <p/>
 * For more information, see:
 * <a href="http://jira.sonarsource.com/browse/DEVACT-103">DEVACT-103</a>
 * @since 1.5.1
 */
public class SonarGitBlameConsumerTest {

  @Test
  public void testConsumerEasy()
    throws Exception {
    SonarGitBlameConsumer consumer = consumeFile("src/test/resources/git/blame/git-blame-3.out");

    Assert.assertEquals(36, consumer.getLines().size());

    BlameLine blameLine = consumer.getLines().get(11);
    Assert.assertEquals("e670863b2b03e158c59f34af1fee20f91b2bd852", blameLine.getRevision());
    Assert.assertEquals("struberg@yahoo.de", blameLine.getAuthor());
    Assert.assertNotNull(blameLine.getDate());
  }

  @Test
  public void testConsumer()
    throws Exception {
    SonarGitBlameConsumer consumer = consumeFile("src/test/resources/git/blame/git-blame.out");

    Assert.assertEquals(187, consumer.getLines().size());

    BlameLine blameLine = consumer.getLines().get(11);
    Assert.assertEquals("e670863b2b03e158c59f34af1fee20f91b2bd852", blameLine.getRevision());
    Assert.assertEquals("struberg@yahoo.de", blameLine.getAuthor());
    Assert.assertNotNull(blameLine.getDate());
  }

  /**
   * Test what happens if a git-blame command got invoked on a
   * file which has no content.
   */
  @Test
  public void testConsumerEmptyFile()
    throws Exception {
    SonarGitBlameConsumer consumer = consumeFile("src/test/resources/git/blame/git-blame-empty.out");

    Assert.assertEquals(0, consumer.getLines().size());
  }


  /**
   * Test what happens if a git-blame command got invoked on a
   * file which didn't got added to the git repo yet.
   */
  @Test
  public void testConsumerOnNewFile()
    throws Exception {
    SonarGitBlameConsumer consumer = consumeFile("src/test/resources/git/blame/git-blame-new-file.out");

    Assert.assertEquals(3, consumer.getLines().size());
    BlameLine blameLine = consumer.getLines().get(0);
    Assert.assertNotNull(blameLine);
    Assert.assertEquals("0000000000000000000000000000000000000000", blameLine.getRevision());
    Assert.assertEquals("not.committed.yet", blameLine.getAuthor());
  }

  /**
   * Test a case where the committer and author are different persons
   */
  @Test
  public void testConsumerWithDifferentAuthor()
    throws Exception {
    SonarGitBlameConsumer consumer = consumeFile("src/test/resources/git/blame/git-blame-different-author.out");

    Assert.assertEquals(93, consumer.getLines().size());
    BlameLine blameLine = consumer.getLines().get(0);
    Assert.assertNotNull(blameLine);
    Assert.assertEquals("39574726d20f62023d39311e6032c7ab0a9d3cdb", blameLine.getRevision());
    Assert.assertEquals("struberg@yahoo.de", blameLine.getAuthor());
    Assert.assertEquals("struberg@yahoo.de", blameLine.getCommitter());

    blameLine = consumer.getLines().get(12);
    Assert.assertNotNull(blameLine);
    Assert.assertEquals("41e5bc05953781a5702f597a1a36c55371b517d3", blameLine.getRevision());
    Assert.assertEquals("another-email@struct.at", blameLine.getAuthor());
    Assert.assertEquals("struberg@yahoo.de", blameLine.getCommitter());
  }

  /**
   * Consume all lines in the given file with a fresh {@link GitBlameConsumer}.
   *
   * @param fileName
   * @return the resulting {@link GitBlameConsumer}
   * @throws java.io.IOException
   */
  private SonarGitBlameConsumer consumeFile(String fileName) throws IOException, URISyntaxException {
    SonarGitBlameConsumer consumer = new SonarGitBlameConsumer(new DefaultLog());

    File f = getTestFile(fileName);

    BufferedReader r = new BufferedReader(new FileReader(f));

    String line;

    while ((line = r.readLine()) != null) {
      consumer.consumeLine(line);
    }
    return consumer;
  }

  private File getTestFile(String fileName) throws URISyntaxException {
    return new File(fileName);
  }
}
