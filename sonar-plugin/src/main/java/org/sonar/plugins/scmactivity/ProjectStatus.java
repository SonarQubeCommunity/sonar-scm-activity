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

import com.google.common.collect.Maps;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.sonar.api.resources.Project;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ProjectStatus extends Changeable {

  private Map<File, FileStatus> files = Maps.newHashMap();

  private class FileStatus extends Changeable {
    private String relativePath;

    public FileStatus(String relativePath) {
      this.relativePath = relativePath;
    }
  }

  public ProjectStatus(Project project) {
    this(project.getFileSystem().getBasedir(), project.getFileSystem().getJavaSourceFiles());
  }

  /**
   * For unit tests.
   */
  ProjectStatus(File basedir, List<File> files) {
    for (File file : files) {
      String relativePath = ScmUtils.getRelativePath(basedir, file);
      this.files.put(file, new FileStatus(relativePath));
    }
  }

  @Override
  protected void analyzeChangeSet(ChangeSet changeSet) {
    super.analyzeChangeSet(changeSet);
    for (FileStatus status : files.values()) {
      if (changeSet.containsFilename(status.relativePath)) {
        status.analyzeChangeSet(changeSet);
      }
    }
  }

  public void analyzeChangeLog(ChangeLogSet changeLog) {
    List<ChangeSet> sets = changeLog.getChangeSets();
    for (ChangeSet changeSet : sets) {
      analyzeChangeSet(changeSet);
    }
  }

  public Collection<File> getFiles() {
    return files.keySet();
  }

  public boolean isModified(File file) {
    return files.get(file).isModified();
  }

}
