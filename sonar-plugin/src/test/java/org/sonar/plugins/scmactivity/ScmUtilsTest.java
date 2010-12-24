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

import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ScmUtilsTest {
  @Test
  public void shouldReturnRelativePath() {
    File basedir = new File("/checkout/sonar-core");
    File file = new File("/checkout/sonar-core/src/main/java/Foo.java");
    assertThat(ScmUtils.getRelativePath(basedir, file), is("src/main/java/Foo.java"));
  }

  @Test
  public void shouldFixMissingRevision() {
    // missing revision
    ChangeSet changeSet = new ChangeSet();
    changeSet.setRevision(null);
    changeSet.addFile(new ChangeFile("file", "rev"));
    assertThat(ScmUtils.fixChangeSet(changeSet), is(true));
    assertThat(changeSet.getRevision(), is("rev"));
    // nothing to fix
    changeSet = new ChangeSet();
    changeSet.setRevision("rev");
    assertThat(ScmUtils.fixChangeSet(changeSet), is(true));
    // unable to fix
    changeSet = new ChangeSet();
    assertThat(ScmUtils.fixChangeSet(changeSet), is(false));
  }

}
