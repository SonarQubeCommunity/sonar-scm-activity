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

import org.apache.commons.lang.time.DateUtils;
import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.junit.Test;
import org.sonar.plugins.scmactivity.ProjectStatus.FileStatus;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProjectStatusTest {

  @Test
  public void shouldTrackFileChanges() {
    File basedir = new File("/checkout/sonar-core");
    File modifiedFile = new File(basedir, "src/main/java/org/sonar/jpa/entity/SchemaMigration.java");
    File unmodifiedFile = new File(basedir, "src/main/java/org/sonar/jpa/entity/Unmodified.java");
    ProjectStatus changes = new ProjectStatus(basedir, Arrays.asList(modifiedFile, unmodifiedFile));

    ChangeSet changeSet = mockChangeSet(0, "godin", "6177",
        "/trunk/sonar-core/src/main/java/org/sonar/jpa/entity/SchemaMigration.java",
        "/trunk/sonar-plugin-api/src/main/java/org/sonar/api/rules/Rule.java",
        "/trunk/sonar-server/src/main/webapp/WEB-INF/db/migrate/165_set_nullable_rule_config_key.rb");
    changes.analyzeChangeSet(changeSet);

    // unmodified file
    assertThat(changes.getFileStatus(unmodifiedFile).isModified(), is(false));
    // modified file
    FileStatus fileStatus = changes.getFileStatus(modifiedFile);
    assertThat(fileStatus.isModified(), is(true));
    assertThat(fileStatus.getChanges(), is(1));
    assertThat(changes.getDate().getDay(), is(new Date().getDay()));
    assertThat(changes.getRevision(), is("6177"));
    assertThat(changes.getAuthor(), is("godin"));
  }

  @Test
  public void shouldTrackProjectChanges() {
    File basedir = new File("/checkout/sonar-core");
    ProjectStatus changes = new ProjectStatus(basedir, Collections.<File> emptyList());

    changes.analyzeChangeSet(mockChangeSet(-1, "simon", "1"));
    changes.analyzeChangeSet(mockChangeSet(0, "godin", "2"));

    assertThat(changes.isModified(), is(true));
    assertThat(changes.getChanges(), is(2));
    assertThat(changes.getDate().getDay(), is(new Date().getDay()));
    assertThat(changes.getRevision(), is("2"));
    assertThat(changes.getAuthor(), is("godin"));
  }

  private ChangeSet mockChangeSet(int days, String author, String revision, String... filenames) {
    Date date = DateUtils.addDays(new Date(), days);
    ChangeSet changeSet = new ChangeSet(date, "comment", author, null);
    changeSet.setRevision(revision);
    for (String filename : filenames) {
      changeSet.addFile(new ChangeFile(filename, revision));
    }
    return changeSet;
  }

}
