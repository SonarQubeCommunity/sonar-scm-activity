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

import org.sonar.api.web.ResourceViewer;
import org.sonar.plugins.scmactivity.blameviewer.client.BlameViewer;

/**
 * @author Evgeny Mandrikov
 */
public class BlameViewerDefinition implements ResourceViewer {
  public String getTitle() {
    return "Blame";
  }

  public String getGwtId() {
    return BlameViewer.GWT_ID;
  }
}
