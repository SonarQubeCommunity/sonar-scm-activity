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

import com.google.common.base.Joiner;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.apache.maven.scm.manager.ScmManager;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.TimeProfiler;

import java.io.File;

public class LocalModificationChecker implements BatchExtension {

  private ScmManager manager;
  private SonarScmRepository repository;
  private ScmConfiguration config;

  public LocalModificationChecker(ScmConfiguration config, ScmManager manager, SonarScmRepository repository) {
    this.config = config;
    this.manager = manager;
    this.repository = repository;
  }

  public void check() {
    if (config.isCheckLocalModifications()) {
      doCheck();
    }
  }

  void doCheck() {
    TimeProfiler profiler = new TimeProfiler().start("Check for local modifications");
    try {
      for (File sourceDir : config.getSourceDirs()) {
        // limitation of http://jira.codehaus.org/browse/SONAR-2266, the directory existence must be checked
        if (sourceDir.exists()) {
          LoggerFactory.getLogger(getClass()).debug("Check directory: " + sourceDir);
          StatusScmResult result = manager.status(repository.getScmRepository(), new ScmFileSet(sourceDir));

          if (!result.isSuccess()) {
            throw new SonarException("Unable to check for local modifications: " + result.getProviderMessage());
          }

          if (!result.getChangedFiles().isEmpty()) {
            Joiner joiner = Joiner.on(SystemUtils.LINE_SEPARATOR + "\t");
            throw new SonarException("Fail to load SCM data as there are local modifications: " + SystemUtils.LINE_SEPARATOR + "\t" + joiner.join(result.getChangedFiles()));
          }
        }
      }

    } catch (ScmException e) {
      throw new SonarException("Unable to check for local modifications", e);

    } finally {
      profiler.stop();
    }
  }

}
