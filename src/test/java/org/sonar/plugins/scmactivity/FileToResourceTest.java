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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.InputFile;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileToResourceTest {
  FileToResource fileToResource;

  InputFile javaFile, cobolFile;
  org.sonar.api.resources.File file = mock(org.sonar.api.resources.File.class);

  @Before
  public void setUp() {
    javaFile = mock(InputFile.class);
    when(javaFile.attribute(InputFile.ATTRIBUTE_LANGUAGE)).thenReturn("java");
    when(javaFile.attribute(InputFile.ATTRIBUTE_SOURCE_RELATIVE_PATH)).thenReturn("com/foo/bar/MyClass.java");
    cobolFile = mock(InputFile.class);
    when(cobolFile.attribute(InputFile.ATTRIBUTE_LANGUAGE)).thenReturn("cobol");
    when(cobolFile.attribute(InputFile.ATTRIBUTE_SOURCE_RELATIVE_PATH)).thenReturn("some/Cobol.cbl");
    fileToResource = new FileToResource();
  }

  @Test
  public void should_find_java_file_in_java_project() {
    Resource resource = fileToResource.toResource(javaFile);

    assertThat(resource).isNotNull();
  }

  @Test
  public void should_find_file_in_non_java_project() {
    Resource resource = fileToResource.toResource(cobolFile);

    assertThat(resource).isNotNull();
  }

  @Test
  public void shouldnt_find_file() {
    Resource resource = fileToResource.toResource(mock(InputFile.class));

    assertThat(resource).isNull();
  }

}
