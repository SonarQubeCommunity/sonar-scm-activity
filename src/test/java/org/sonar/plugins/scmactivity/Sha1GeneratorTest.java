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

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.plugins.scmactivity.test.TemporaryFile;
import org.sonar.test.TestUtils;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * For examples of SHA1, see <a href="http://en.wikipedia.org/wiki/SHA-1#Example_hashes">wikipedia</a>
 */
public class Sha1GeneratorTest {
  Sha1Generator sha1;

  @ClassRule
  public static TemporaryFile temporaryFile = new TemporaryFile();

  @Before
  public void setUp() {
    sha1 = new Sha1Generator();
  }

  @Test
  public void should_find_hash_of_empty_file() throws IOException {
    String hash = sha1.find("");

    assertThat(hash).isEqualTo("da39a3ee5e6b4b0d3255bfef95601890afd80709");
  }

  @Test
  public void should_find_hash() throws IOException {
    String hash = sha1.find("The quick brown fox jumps over the lazy dog");

    assertThat(hash).isEqualTo("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12");
  }

  @Test
  public void should_ignore_carriage_returns() throws IOException {
    assertThat(sha1.find("LINE1\nLINE2\n"))
        .isEqualTo(sha1.find("LINE1\r\nLINE2\r\n"))
        .isEqualTo(sha1.find("LINE1\rLINE2\r"))
        .isEqualTo(sha1.find("LINE1\rLINE2\r\n"))
        .isEqualTo("63193e8d522822e6e209172c9e6c204b9ab7efce");
  }

  @Test
  public void should_be_backward_compatible() throws IOException {
    File file = TestUtils.getResource("UpdateCenter.txt");

    String hash = sha1.find(FileUtils.readFileToString(file, "UTF-8"));

    assertThat(hash).isEqualTo("0ebb4882683fb4209e9aeb7278dec282a9ca3c3c");
  }
}
