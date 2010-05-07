/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.scm.provider.git.gitjava.repository;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.provider.git.repository.GitScmProviderRepository;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaScmProviderRepository extends GitScmProviderRepository {

  private Repository db;

  public GitJavaScmProviderRepository(String url) throws ScmException {
    super(url);
  }

  public GitJavaScmProviderRepository(String url, String user, String password) throws ScmException {
    super(url, user, password);
  }

  public Repository getRepository() {
    return db;
  }

  private void initializeRepository() throws IOException {
    File workDir = null;
    db = new Repository(workDir);
  }
}
