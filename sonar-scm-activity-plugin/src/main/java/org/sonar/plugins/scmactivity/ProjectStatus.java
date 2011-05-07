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

import com.google.common.collect.Lists;
import org.apache.maven.scm.ChangeSet;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public final class ProjectStatus extends Changeable {

  private List<FileStatus> fileStatuses = Lists.newArrayList();

  public ProjectStatus(Project project) {
    this(project.getFileSystem());
  }

  public ProjectStatus(ProjectFileSystem fileSystem) {
    for (InputFile inputFile : fileSystem.mainFiles()) {
      this.fileStatuses.add(new FileStatus(inputFile));
    }
  }

  public ProjectStatus(List<java.io.File> fileStatuses) {
    for (File file : fileStatuses) {
      this.fileStatuses.add(new FileStatus(file, file.getName()));
    }
  }

  protected void doAdd(ChangeSet changeSet) {
    for (FileStatus status : fileStatuses) {
      if (changeSet.containsFilename(status.getRelativePath())) {
        status.add(changeSet);
      }
    }
  }

  public List<FileStatus> getFileStatuses() {
    return fileStatuses;
  }

  static final class FileStatus extends Changeable {
    private File file;
    private String relativePath;

    FileStatus(InputFile inputFile) {
      this.file = inputFile.getFile();
      this.relativePath = inputFile.getRelativePath();
    }

    FileStatus(File file, String relativePath) {
      this.file = file;
      this.relativePath = relativePath;
    }

    public File getFile() {
      return file;
    }

    public String getRelativePath() {
      return relativePath;
    }
  }

}
