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

import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

public class FileToResource implements BatchExtension {
  private final Project project;

  public FileToResource(Project project) {
    this.project = project;
  }

  public Resource toResource(InputFile file, SensorContext context) {
    Resource resource = null;
    if (Java.KEY.equals(project.getLanguageKey())) {
      if (Java.isJavaFile(file.getFile())) {
        resource = JavaFile.fromRelativePath(file.getRelativePath(), false);
      }
    } else {
      resource = new org.sonar.api.resources.File(file.getRelativePath());
    }
    if (resource != null) {
      return context.getResource(resource);
    }
    return null;
  }
}
