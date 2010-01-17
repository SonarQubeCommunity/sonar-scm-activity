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

package org.sonar.plugins.scmactivity;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.plugins.scmactivity.blameviewer.BlameViewerDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivityPlugin implements Plugin {
  public static final String KEY = "scm-activity";

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "SCM Activity";
  }

  public String getDescription() {
    return "Collects information from SCM.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    return Arrays.asList(
        ScmActivityMetrics.class,
        ScmActivityWidget.class,
        ScmActivitySensor.class,
        ProjectActivityDecorator.class,
        BlameViewerDefinition.class
    );
  }
}
