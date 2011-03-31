/*
 * Sonar SCM Activity Plugin :: Maven SCM Ext
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

package org.apache.maven.scm.provider.git.gitexe.command.blame;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.util.AbstractConsumer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * See SONARPLUGINS-861
 */
public class FixedGitBlameConsumer extends AbstractConsumer {
  private static final String GIT_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss Z";
  private List lines = new ArrayList();

  public FixedGitBlameConsumer(ScmLogger logger) {
    super(logger);
  }

  public void consumeLine(String line) {
    String parts[] = line.split("\t", 4);
    String revision = parts[0];
    String author = parts[1].substring(1);
    String dateTimeStr = parts[2];

    Date dateTime = parseDate(dateTimeStr, null, GIT_TIMESTAMP_PATTERN);
    getLines().add(new BlameLine(dateTime, revision, author));

    if (!(getLogger().isDebugEnabled()))
      return;
    getLogger().debug(author + " " + dateTimeStr);
  }

  public List getLines() {
    return this.lines;
  }
}
