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

package org.apache.maven.scm.tck.command.blame;

import org.apache.maven.scm.ExtScmTckTestCase;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.repository.ScmRepository;

/**
 * @author Evgeny Mandrikov
 */
public abstract class BlameTckTest extends ExtScmTckTestCase {
  public void testBlameCommand()
      throws Exception {
    ScmRepository repository = getScmRepository();
    ExtScmManager manager = getScmManager();
    ScmFileSet fileSet = new ScmFileSet(getWorkingCopy());

    BlameScmResult result = manager.blame(repository, fileSet, "pom.xml");

    assertNotNull("The command returned a null result.", result);

    assertResultIsSuccess(result);

    verifyResult(result);
  }

  protected abstract void verifyResult(BlameScmResult result);
}
