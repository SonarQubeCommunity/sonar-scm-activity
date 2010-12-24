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

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensorTest {
  private static final String SCM_CONNECTION = "scm:svn:https://localhost";

  private Project project;
  private ScmActivitySensor sensor;

  @Before
  public void setUp() {
    project = new Project("");
    ScmConfiguration scmConfiguration = new ScmConfiguration(project);
    ProjectScmManager scmManager = new ProjectScmManager(project, scmConfiguration);
    sensor = new ScmActivitySensor(scmConfiguration, scmManager, null);
  }

  /**
   * See SONARPLUGINS-350
   */
  @Test
  public void noExecutionIfNotLatestAnalysis() {
    project.setLatestAnalysis(false);

    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void noExecutionIfDisabled() {
    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(ScmActivityPlugin.ENABLED_PROPERTY, false);
    project
        .setLatestAnalysis(true)
        .setConfiguration(configuration);

    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void noExecutionIfScmNotDefined() {
    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(ScmActivityPlugin.ENABLED_PROPERTY, true);
    MavenProject pom = new MavenProject();
    project
        .setLatestAnalysis(true)
        .setPom(pom)
        .setConfiguration(configuration);

    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void shouldExecuteOnProject() {
    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(ScmActivityPlugin.ENABLED_PROPERTY, true);
    MavenProject pom = new MavenProject();
    Scm scm = new Scm();
    scm.setConnection(SCM_CONNECTION);
    pom.setScm(scm);
    project
        .setLatestAnalysis(true)
        .setPom(pom)
        .setConfiguration(configuration);

    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void testToString() {
    assertThat(sensor.toString(), is("ScmActivitySensor"));
  }
}
