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

package org.apache.maven.scm.provider.git.gitjava;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.provider.git.AbstractGitScmProvider;
import org.apache.maven.scm.provider.git.command.GitCommand;
import org.apache.maven.scm.provider.git.gitjava.command.add.GitJavaAddCommand;
import org.apache.maven.scm.provider.git.gitjava.command.branch.GitJavaBranchCommand;
import org.apache.maven.scm.provider.git.gitjava.command.changelog.GitJavaChangeLogCommand;
import org.apache.maven.scm.provider.git.gitjava.command.checkin.GitJavaCheckInCommand;
import org.apache.maven.scm.provider.git.gitjava.command.checkout.GitJavaCheckOutCommand;
import org.apache.maven.scm.provider.git.gitjava.command.diff.GitJavaDiffCommand;
import org.apache.maven.scm.provider.git.gitjava.command.list.GitJavaListCommand;
import org.apache.maven.scm.provider.git.gitjava.command.remove.GitJavaRemoveCommand;
import org.apache.maven.scm.provider.git.gitjava.command.status.GitJavaStatusCommand;
import org.apache.maven.scm.provider.git.gitjava.command.tag.GitJavaTagCommand;
import org.apache.maven.scm.provider.git.gitjava.command.update.GitJavaUpdateCommand;

import java.io.File;

/**
 * @author Evgeny Mandrikov
 */
public class GitJavaScmProvider extends AbstractGitScmProvider {

  @Override
  protected String getRepositoryURL(File file) throws ScmException {
    // TODO
    return null;
  }

  @Override
  protected GitCommand getAddCommand() {
    return new GitJavaAddCommand();
  }

  @Override
  protected GitCommand getBranchCommand() {
    return new GitJavaBranchCommand();
  }

  @Override
  protected GitCommand getChangeLogCommand() {
    return new GitJavaChangeLogCommand();
  }

  @Override
  protected GitCommand getCheckInCommand() {
    return new GitJavaCheckInCommand();
  }

  @Override
  protected GitCommand getCheckOutCommand() {
    return new GitJavaCheckOutCommand();
  }

  @Override
  protected GitCommand getDiffCommand() {
    return new GitJavaDiffCommand();
  }

  @Override
  protected GitCommand getExportCommand() {
    // TODO
    return null;
  }

  @Override
  protected GitCommand getRemoveCommand() {
    return new GitJavaRemoveCommand();
  }

  @Override
  protected GitCommand getStatusCommand() {
    return new GitJavaStatusCommand();
  }

  @Override
  protected GitCommand getTagCommand() {
    return new GitJavaTagCommand();
  }

  @Override
  protected GitCommand getUpdateCommand() {
    return new GitJavaUpdateCommand();
  }

  @Override
  protected GitCommand getListCommand() {
    return new GitJavaListCommand();
  }

  @Override
  protected GitCommand getInfoCommand() {
    // TODO
    return null;
  }

}
