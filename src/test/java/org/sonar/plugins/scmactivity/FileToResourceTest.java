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
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileToResourceTest {
  FileToResource fileToResource;

  Project project = mock(Project.class);
  SensorContext context = mock(SensorContext.class);
  JavaFile javaFile = mock(JavaFile.class);
  org.sonar.api.resources.File file = mock(org.sonar.api.resources.File.class);

  @Before
  public void setUp() {
    fileToResource = new FileToResource(project);
  }

  @Test
  public void should_find_java_file_in_java_project() {
    when(project.getLanguageKey()).thenReturn("java");
    when(context.getResource(any(JavaFile.class))).thenReturn(javaFile);

    Resource resource = fileToResource.toResource(inputFile("source.java"), context);

    assertThat(resource).isSameAs(javaFile);
  }

  @Test
  public void should_ignore_non_java_file_in_java_project() {
    when(project.getLanguageKey()).thenReturn("java");

    Resource resource = fileToResource.toResource(inputFile("pom.xml"), context);

    assertThat(resource).isNull();
  }

  @Test
  public void should_find_file_in_non_java_project() {
    when(project.getLanguageKey()).thenReturn("cpp");
    when(context.getResource(any(org.sonar.api.resources.File.class))).thenReturn(file);

    Resource resource = fileToResource.toResource(inputFile("source.cpp"), context);

    assertThat(resource).isSameAs(file);
  }

  @Test
  public void should_treat_java_file_as_standard_file_in_non_java_project() {
    when(project.getLanguageKey()).thenReturn("cpp");
    when(context.getResource(any(org.sonar.api.resources.File.class))).thenReturn(file);

    Resource resource = fileToResource.toResource(inputFile("source.java"), context);

    assertThat(resource).isSameAs(file);
  }

  @Test
  public void shouldnt_find_file() {
    Resource resource = fileToResource.toResource(inputFile("unknown.java"), context);

    assertThat(resource).isNull();
  }

  static InputFile inputFile(String relativePath) {
    InputFile inputFile = mock(InputFile.class);
    when(inputFile.getFile()).thenReturn(new File(relativePath));
    when(inputFile.getRelativePath()).thenReturn(relativePath);
    return inputFile;
  }
}
