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

package org.apache.maven.scm.provider.git.command.blame;

import org.apache.maven.scm.provider.git.GitScmTestUtils;
import org.apache.maven.scm.tck.command.blame.BlameTckTest;

/**
 * @author Evgeny Mandrikov
 */
public abstract class GitBlameCommandTckTest extends BlameTckTest {
  @Override
  public void initRepo() throws Exception {
    GitScmTestUtils.initRepo("src/test/git-repository/", getRepositoryRoot(), getWorkingDirectory());
  }
}
