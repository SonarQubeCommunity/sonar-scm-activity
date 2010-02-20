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

package org.apache.maven.scm.provider.cvslib.command.blame;

import org.apache.maven.scm.ExtScmTckTestCase;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.provider.cvslib.CvsScmTestUtils;

import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public abstract class CvsBlameCommandTckTest extends ExtScmTckTestCase {
  public String getScmUrl() throws Exception {
    return CvsScmTestUtils.getScmUrl(getRepositoryRoot(), getModule());
  }

  protected String getModule() {
    return "test-repo/module";
  }

  public void initRepo() throws Exception {
    CvsScmTestUtils.initRepo("src/test/tck-repository/", getRepositoryRoot(), getWorkingDirectory());
  }

  protected void testBlameCommand() throws Exception {
    ExtScmManager scmManager = (ExtScmManager) getScmManager();
    BlameScmResult result = scmManager.blame(
        getScmRepository(),
        getScmFileSet(),
        "pom.xml"
    );
    assertResultIsSuccess(result);

    List lines = result.getLines();
    int size = lines.size();
    assertEquals(1, size);
    BlameLine line = (BlameLine) lines.get(0);
    assertEquals("1.1", line.getRevision());
    assertEquals("Brett", line.getAuthor());
  }

}