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

package org.sonar.plugins.scmactivity;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;

import java.io.File;

public class ScmFacade implements BatchExtension {
  private final ScmManager scmManager;
  private final ScmConfiguration configuration;
  private ScmRepository repository;

  public ScmFacade(ScmManager scmManager, ScmConfiguration configuration) {
    this.scmManager = scmManager;
    this.configuration = configuration;
  }

  public BlameScmResult blame(File file) throws ScmException {
    return scmManager.blame(getScmRepository(), new ScmFileSet(file.getParentFile()), file.getName());
  }

  public StatusScmResult localChanges(File sourceDir) throws ScmException {
    return scmManager.status(getScmRepository(), new ScmFileSet(sourceDir));
  }

  @VisibleForTesting
  ScmRepository getScmRepository() {
    if (repository == null) {
      try {
        String connectionUrl = configuration.getUrl();
        String user = configuration.getUser();
        String password = configuration.getPassword();

        repository = scmManager.makeScmRepository(connectionUrl);

        if (!StringUtils.isBlank(user)) {
          ScmProviderRepository providerRepository = repository.getProviderRepository();
          providerRepository.setUser(user);
          providerRepository.setPassword(password);
        }
      } catch (ScmException e) {
        throw new SonarException(e);
      }
    }

    return repository;
  }
}
