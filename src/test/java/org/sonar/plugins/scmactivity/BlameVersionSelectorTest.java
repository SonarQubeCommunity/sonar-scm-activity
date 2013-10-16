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
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.scmactivity.test.TemporaryFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlameVersionSelectorTest {

  @ClassRule
  public static TemporaryFile temporaryFile = new TemporaryFile();

  BlameVersionSelector blameVersionSelector;

  Blame blameSensor = mock(Blame.class);
  Sha1Generator sha1Generator = mock(Sha1Generator.class);
  SensorContext context = mock(SensorContext.class);
  Resource resource = mock(Resource.class);
  FileToResource fileToResource = mock(FileToResource.class);
  MeasureUpdate saveBlame = mock(MeasureUpdate.class);

  @Before
  public void setUp() {
    ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
    when(projectFileSystem.getSourceCharset()).thenReturn(Charset.forName("UTF-8"));
    blameVersionSelector = new BlameVersionSelector(blameSensor, sha1Generator, fileToResource, projectFileSystem);
  }

  @Test
  public void should_save_blame_when_hashes_changes() throws IOException {
    File file = file("source.java", "foo");
    InputFile inputFile = inputFile(file);
    when(fileToResource.toResource(inputFile, context)).thenReturn(resource);
    when(sha1Generator.find(anyString())).thenReturn("SHA1");
    when(blameSensor.save(file, resource, "SHA1", 1)).thenReturn(saveBlame);

    MeasureUpdate update = blameVersionSelector.detect(inputFile, "OLD SHA1", context);

    assertThat(update).isSameAs(saveBlame);
  }

  @Test
  public void should_save_blame_when_no_previous_hash() throws IOException {
    File file = file("source.java", "foo");
    InputFile inputFile = inputFile(file);
    when(fileToResource.toResource(inputFile, context)).thenReturn(resource);
    when(sha1Generator.find(anyString())).thenReturn("SHA1");
    when(blameSensor.save(file, resource, "SHA1", 1)).thenReturn(saveBlame);

    MeasureUpdate update = blameVersionSelector.detect(inputFile, "", context);

    assertThat(update).isSameAs(saveBlame);
  }

  @Test
  public void should_copy_previous_measures_when_hash_is_the_same() throws IOException {
    File file = file("source.java", "foo");
    InputFile inputFile = inputFile(file);
    when(fileToResource.toResource(inputFile, context)).thenReturn(resource);
    when(sha1Generator.find(anyString())).thenReturn("SHA1");
    when(blameSensor.save(file, resource, "SHA1", 1)).thenReturn(saveBlame);

    MeasureUpdate update = blameVersionSelector.detect(inputFile, "SHA1", context);

    assertThat(update).isInstanceOf(CopyPreviousMeasures.class);
  }

  @Test
  public void should_ignore_error() throws IOException {
    File file = file("source.java", "foo");
    InputFile inputFile = inputFile(file);
    when(sha1Generator.find(anyString())).thenThrow(new IOException("BUG"));

    MeasureUpdate update = blameVersionSelector.detect(inputFile, "SHA1", context);

    assertThat(update).isSameAs(MeasureUpdate.NONE);
  }

  static InputFile inputFile(File file) {
    InputFile inputFile = mock(InputFile.class);
    when(inputFile.getFile()).thenReturn(file);
    return inputFile;
  }

  static File file(String name, String content) throws IOException {
    return temporaryFile.create(name, content);
  }
}
