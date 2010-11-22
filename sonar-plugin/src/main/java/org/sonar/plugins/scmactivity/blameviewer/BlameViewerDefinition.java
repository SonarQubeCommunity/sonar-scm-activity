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

package org.sonar.plugins.scmactivity.blameviewer;

import org.sonar.api.resources.Resource;
import org.sonar.api.web.*;
import org.sonar.plugins.scmactivity.blameviewer.client.BlamePanel;
import org.sonar.plugins.scmactivity.blameviewer.client.BlameViewer;

/**
 * @author Evgeny Mandrikov
 */
@ResourceQualifier({Resource.QUALIFIER_CLASS, Resource.QUALIFIER_FILE})
@ResourceScope({Resource.SCOPE_ENTITY})
@NavigationSection(NavigationSection.RESOURCE_TAB)
@DefaultTab(metrics = {
    BlamePanel.LAST_ACTIVITY,
    BlamePanel.REVISION,
    BlamePanel.BLAME_AUTHORS_DATA,
    BlamePanel.BLAME_DATE_DATA
})
@UserRole(UserRole.CODEVIEWER)
public class BlameViewerDefinition extends GwtPage {
  public String getTitle() {
    return "Blame";
  }

  public String getGwtId() {
    return BlameViewer.GWT_ID;
  }
}
