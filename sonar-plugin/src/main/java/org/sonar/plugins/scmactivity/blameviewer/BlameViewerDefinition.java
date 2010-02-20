/*
 * Copyright (C) 2010 Evgeny Mandrikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
