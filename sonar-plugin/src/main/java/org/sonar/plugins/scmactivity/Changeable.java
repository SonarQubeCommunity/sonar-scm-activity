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

import java.util.Date;
import java.util.List;

public abstract class Changeable {

  private Date date = new Date(0);
  private String revision;
  private String author;
  private int changes;

  public void analyzeChangeSet(ChangeSet changeSet) {
    if (changeSet.getRevision() == null) {
      // This is a workaround for bug, which exists in GitExeScmProvider
      List files = changeSet.getFiles();
      if (files.size() == 0) {
        // This may happen if Git changelog can't be correctly parsed
        // for example when message was not provided for commit
        return;
      }
      ChangeFile file = (ChangeFile) files.get(0);
      changeSet.setRevision(file.getRevision());
    }
    changes++;
    Date changeSetDate = changeSet.getDate();
    if (changeSetDate.after(this.date)) {
      this.date = changeSetDate;
      this.revision = changeSet.getRevision();
      this.author = changeSet.getAuthor();
    }
  }

  /**
   * @return date of last change
   */
  public Date getDate() {
    return (Date) date.clone();
  }

  /**
   * @return revision of last change
   */
  public String getRevision() {
    return revision;
  }

  /**
   * @return author of last change
   */
  public String getAuthor() {
    return author;
  }

  /**
   * @return number of changes
   */
  public int getChanges() {
    return changes;
  }

  public boolean isModified() {
    return changes > 0;
  }

}
