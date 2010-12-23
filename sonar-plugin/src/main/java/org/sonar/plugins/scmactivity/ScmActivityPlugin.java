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
        key = ScmActivityPlugin.ENABLED_PROPERTY,
        defaultValue = ScmActivityPlugin.ENABLED_DEFAULT_VALUE + "",
        name = "Enabled",
        description = "",
        module = true,
        project = true,
        global = true
    ),
    @Property(
        key = ScmActivityPlugin.URL_PROPERTY,
        defaultValue = "",
        name = "SCM URL",
        description = "SCM URL. Leave blank to take this value from <i>pom.xml</i>. Example:" +
            "<i>scm:svn:https://svn.codehaus.org/sonar-plugins/trunk/scm-activity</i>",
        module = true,
        project = true,
        global = false
    ),
    @Property(
        key = ScmActivityPlugin.USER_PROPERTY,
        defaultValue = "",
        name = "User",
        description = "User to connect with SCM. Leave blank for anonymous.",
        module = false,
        project = true,
        global = true
    ),
    @Property(
        key = ScmActivityPlugin.PASSWORD_PROPERTY,
        defaultValue = "",
        name = "Password",
        description = "Password to connect with SCM. Leave blank for anonymous.",
        module = false,
        project = true,
        global = true
    ),
    @Property(
        key = ScmActivityPlugin.PREFER_PURE_JAVA_PROPERTY,
        defaultValue = ScmActivityPlugin.PREFER_PURE_JAVA_DEFAULT_VALUE + "",
        name = "Prefer pure Java implementations",
        description = "",
        module = false,
        project = true,
        global = true
    ) })
public class ScmActivityPlugin implements Plugin {
  public static final String URL_PROPERTY = "sonar.scm-activity.url";
  public static final String ENABLED_PROPERTY = "sonar.scm-activity.enabled";
  public static final boolean ENABLED_DEFAULT_VALUE = false;
  public static final String USER_PROPERTY = "sonar.scm-activity.user.secured";
  public static final String PASSWORD_PROPERTY = "sonar.scm-activity.password.secured";
  public static final String PREFER_PURE_JAVA_PROPERTY = "sonar.scm-activity.prefer_pure_java";
  public static final boolean PREFER_PURE_JAVA_DEFAULT_VALUE = true;

  public String getKey() {
    return "scm-activity";
  }

  public String getName() {
    return "SCM Activity";
  }

  public String getDescription() {
    return "Collects information from SCM.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    return Arrays.asList(
        ScmConfiguration.class,
        ProjectScmManager.class,
        ScmActivityMetrics.class,
        ScmActivityWidget.class,
        ScmActivitySensor.class,
        ProjectActivityDecorator.class,
        BlameViewerDefinition.class
        );
  }
}
