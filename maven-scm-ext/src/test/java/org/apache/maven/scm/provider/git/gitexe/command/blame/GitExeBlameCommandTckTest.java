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

package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.git.GitScmTestUtils;
import org.apache.maven.scm.provider.git.command.blame.GitBlameCommandTckTest;

import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class GitExeBlameCommandTckTest extends GitBlameCommandTckTest {
  @Override
  protected boolean isPureJava() {
    return false;
  }

  @Override
  public String getScmUrl() throws Exception {
    return GitScmTestUtils.getScmUrl(getRepositoryRoot(), "git");
  }

  @Override
  public void testBlameCommand() throws Exception {
    super.testBlameCommand();
  }

  @Override
  protected void verifyResult(BlameScmResult result) {
    List<BlameLine> lines = result.getLines();
    assertEquals("Expected 1 line in blame", 1, lines.size());
    BlameLine line = lines.get(0);
    assertEquals("Mark Struberg", line.getAuthor());
    assertEquals("92f139df", line.getRevision());
  }
}