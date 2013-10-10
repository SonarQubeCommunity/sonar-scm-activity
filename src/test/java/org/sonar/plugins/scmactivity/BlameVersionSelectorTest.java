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

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.InputFile;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlameVersionSelectorTest {

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  BlameVersionSelector blameVersionSelector;

  Blame blameSensor = mock(Blame.class);
  SensorContext context = mock(SensorContext.class);
  Resource resource = mock(Resource.class);
  FileToResource fileToResource = mock(FileToResource.class);
  MeasureUpdate saveBlame = mock(MeasureUpdate.class);

  @Before
  public void setUp() {
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.sourceCharset()).thenReturn(Charsets.UTF_8);
    blameVersionSelector = new BlameVersionSelector(fileToResource, blameSensor, fs);
  }

  @Test
  public void should_save_blame_when_file_changes() throws IOException {
    File sourceDir = temp.newFolder();
    File changedSource = new File(sourceDir, "source2.java");
    FileUtils.write(changedSource, "foo\nbar\n");

    InputFile changedInputFile = mock(InputFile.class);
    when(changedInputFile.file()).thenReturn(changedSource);
    when(changedInputFile.has(InputFile.ATTRIBUTE_STATUS, InputFile.STATUS_SAME)).thenReturn(false);

    when(fileToResource.toResource(changedInputFile)).thenReturn(resource);
    when(blameSensor.save(changedSource, resource, 3)).thenReturn(saveBlame);

    MeasureUpdate update = blameVersionSelector.select(changedInputFile, context);

    assertThat(update).isSameAs(saveBlame);
  }

  @Test
  public void should_copy_previous_measures_when_file_is_the_same() throws IOException {
    File sourceDir = temp.newFolder();
    File source = new File(sourceDir, "source.java");

    InputFile inputFile = mock(InputFile.class);
    when(inputFile.file()).thenReturn(source);
    when(inputFile.has(InputFile.ATTRIBUTE_STATUS, InputFile.STATUS_SAME)).thenReturn(true);

    when(fileToResource.toResource(inputFile)).thenReturn(resource);

    MeasureUpdate update = blameVersionSelector.select(inputFile, context);

    assertThat(update).isInstanceOf(CopyPreviousMeasures.class);
  }

  @Test
  public void should_ignore_error() throws IOException {
    File sourceDir = temp.newFolder();
    File changedSource = new File(sourceDir, "source2.java");

    InputFile changedInputFile = mock(InputFile.class);
    when(changedInputFile.file()).thenReturn(changedSource);
    when(changedInputFile.has(InputFile.ATTRIBUTE_STATUS, InputFile.STATUS_SAME)).thenReturn(false);

    when(fileToResource.toResource(changedInputFile)).thenReturn(resource);

    MeasureUpdate update = blameVersionSelector.select(changedInputFile, context);

    assertThat(update).isSameAs(MeasureUpdate.NONE);
  }
}
