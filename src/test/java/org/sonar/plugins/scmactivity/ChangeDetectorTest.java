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
import org.sonar.api.resources.Resource;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChangeDetectorTest {
  ChangeDetector changeDetector;

  Blame blameSensor = mock(Blame.class);
  Sha1Generator sha1Generator = mock(Sha1Generator.class);
  SensorContext context = mock(SensorContext.class);
  Resource resource = mock(Resource.class);
  FileToResource fileToResource = mock(FileToResource.class);
  MeasureUpdate saveBlame = mock(MeasureUpdate.class);

  @Before
  public void setUp() {
    changeDetector = new ChangeDetector(blameSensor, sha1Generator, fileToResource);
  }

  @Test
  public void should_save_blame_when_hashes_changes() throws IOException {
    File file = new File("source.java");
    InputFile inputFile = inputFile(file);
    when(fileToResource.toResource(inputFile, context)).thenReturn(resource);
    when(sha1Generator.sha1(file)).thenReturn("SHA1");
    when(blameSensor.save(file, resource, "SHA1")).thenReturn(saveBlame);

    MeasureUpdate update = changeDetector.detectChange(inputFile, context, "OLD SHA1");

    assertThat(update).isSameAs(saveBlame);
  }

  @Test
  public void should_copy_previous_measures_when_hash_is_the_same() throws IOException {
    File file = new File("source.java");
    InputFile inputFile = inputFile(file);
    when(fileToResource.toResource(inputFile, context)).thenReturn(resource);
    when(sha1Generator.sha1(file)).thenReturn("SHA1");
    when(blameSensor.save(file, resource, "SHA1")).thenReturn(saveBlame);

    MeasureUpdate update = changeDetector.detectChange(inputFile, context, "SHA1");

    assertThat(update).isInstanceOf(CopyPreviousMeasures.class);
  }

  @Test
  public void should_ignore_error() throws IOException {
    File file = new File("source.java");
    InputFile inputFile = inputFile(file);
    when(sha1Generator.sha1(file)).thenThrow(new IOException("BUG"));

    MeasureUpdate update = changeDetector.detectChange(inputFile, context, "SHA1");

    assertThat(update).isSameAs(MeasureUpdate.NONE);
  }

  static InputFile inputFile(File file) {
    InputFile inputFile = mock(InputFile.class);
    when(inputFile.getFile()).thenReturn(file);
    return inputFile;
  }
}
