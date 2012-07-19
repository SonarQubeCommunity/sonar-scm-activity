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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class Sha1Generator implements BatchExtension {
  private static final Pattern LINE_BREAKS = Pattern.compile("(\r)?+\n|\r");

  private final ProjectFileSystem projectFileSystem;

  public Sha1Generator(ProjectFileSystem projectFileSystem) {
    this.projectFileSystem = projectFileSystem;
  }

  public String find(File file) throws IOException {
    Charset charset = projectFileSystem.getSourceCharset();

    String fileContent = FileUtils.readFileToString(file, charset.name());
    fileContent = LINE_BREAKS.matcher(fileContent).replaceAll("\n");

    return DigestUtils.shaHex(fileContent);
  }
}
