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

package org.sonar.plugins.scmactivity.sha1;

import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.plugins.scmactivity.test.TemporaryFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * For examples of SHA1, see <a href="http://en.wikipedia.org/wiki/SHA-1#Example_hashes">wikipedia</a>
 */
public class Sha1GeneratorTest {
  @ClassRule
  public static TemporaryFile temporaryFile = new TemporaryFile();

  @Test
  public void should_generate_sha1_of_empty_file() throws IOException {
    File emptyFile = temporaryFile.create("empty.java", "");

    String sha1 = new Sha1Generator().sha1(emptyFile);

    assertThat(sha1).isEqualTo("da39a3ee5e6b4b0d3255bfef95601890afd80709");
  }

  @Test
  public void should_generate_sha1_of_file() throws IOException {
    File file = temporaryFile.create("quickBrownFox.java", "The quick brown fox jumps over the lazy dog");

    String sha1 = new Sha1Generator().sha1(file);

    assertThat(sha1).isEqualTo("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12");
  }

  @Test(expected = FileNotFoundException.class)
  public void should_fail_on_unkown_file() throws IOException {
    new Sha1Generator().sha1(new File("UNKNOWN"));
  }
}
