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

import com.google.common.collect.ImmutableList;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.List;

@Properties({
  @Property(
    key = ScmActivityPlugin.ENABLED,
    defaultValue = "" + ScmActivityPlugin.ENABLED_DEFAULT,
    name = "Enable loading of SCM activity. It requires to connect to SCM server.",
    description = "",
    module = true,
    project = true,
    global = true
  ),
  @Property(
    key = ScmActivityPlugin.URL,
    defaultValue = "",
    name = "SCM URL",
    description = "SCM URL. The format is described in <a href='http://maven.apache.org/scm/scm-url-format.html'>this page</a>. Example:" +
      "<i>scm:svn:https://svn.codehaus.org/sonar-plugins/trunk/scm-activity</i>",
    module = true,
    project = true,
    global = false
  ),
  @Property(
    key = ScmActivityPlugin.USER,
    defaultValue = "",
    name = "User",
    description = "User to connect with SCM. Leave blank for anonymous. This property is usually defined in settings of project.",
    module = false,
    project = true,
    global = true
  ),
  @Property(
    key = ScmActivityPlugin.PASSWORD,
    defaultValue = "",
    name = "Password",
    description = "Password to connect with SCM. This property is usually defined in settings of project.",
    module = false,
    project = true,
    global = true
  ),
  @Property(
    key = ScmActivityPlugin.THREAD_COUNT,
    defaultValue = "" + ScmActivityPlugin.THREAD_COUNT_DEFAULT,
    name = "Thread count",
    description = "Number of threads used to speed-up the retrieval of authors by line (aka blame information)." +
      "The default behavior is to use 4 threads.",
    module = true,
    project = true,
    global = true
  )})
public final class ScmActivityPlugin implements Plugin {
  public static final String ENABLED = "sonar.scm.enabled";
  public static final String URL = "sonar.scm.url";
  public static final String USER = "sonar.scm.user.secured";
  public static final String PASSWORD = "sonar.scm.password.secured";
  public static final String THREAD_COUNT = "sonar.scm.threadCount";
  public static final boolean ENABLED_DEFAULT = false;
  public static final int THREAD_COUNT_DEFAULT = 4;

  public String getKey() {
    return "scm-activity";
  }

  public String getName() {
    return "SCM Activity";
  }

  public String getDescription() {
    return "Collects information from SCM.";
  }

  @SuppressWarnings("unchecked")
  public List getExtensions() {
    return ImmutableList.of(
        Blame.class,
        BlameVersionSelector.class,
        FileToResource.class,
        MavenScmConfiguration.class,
        PreviousSha1Finder.class,
        ScmActivityMetrics.class,
        ScmActivitySensor.class,
        ScmConfiguration.class,
        Sha1Generator.class,
        SonarScmManager.class,
        ScmFacade.class,
        UrlChecker.class);
  }
}
