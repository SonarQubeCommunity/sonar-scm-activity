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
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.scmactivity.blameviewer.BlameViewerDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
@Properties({
    @Property(
        key = ScmActivitySensor.ENABLED_PROPERTY,
        defaultValue = ScmActivitySensor.ENABLED_DEFAULT_VALUE + "",
        name = "Enabled",
        description = "",
        module = true,
        project = true,
        global = true
    ),
    @Property(
        key = ScmActivitySensor.URL_PROPERTY,
        defaultValue = "",
        name = "SCM URL",
        description = "SCM URL. Leave blank to take this value from <i>pom.xml</i>. Example:" +
            "<i>scm:svn:https://svn.codehaus.org/sonar-plugins/trunk/scm-activity</i>",
        module = true,
        project = true,
        global = false
    ),
    @Property(
        key = ScmActivitySensor.USER_PROPERTY,
        defaultValue = "",
        name = "User",
        description = "User to connect with SCM. Leave blank for anonymous.",
        module = false,
        project = true,
        global = true
    ),
    @Property(
        key = ScmActivitySensor.PASSWORD_PROPERTY,
        defaultValue = "",
        name = "Password",
        description = "Password to connect with SCM. Leave blank for anonymous.",
        module = false,
        project = true,
        global = true
    ),
    @Property(
        key = ScmActivitySensor.PREFER_PURE_JAVA_PROPERTY,
        defaultValue = ScmActivitySensor.PREFER_PURE_JAVA_DEFAULT_VALUE + "",
        name = "Prefer pure Java implementations",
        description = "",
        module = false,
        project = true,
        global = true
    )
})
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
