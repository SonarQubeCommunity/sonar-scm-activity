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
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensorTest {
  private static final String SCM_CONNECTION = "scm:svn:https://localhost";
  private static final String SCM_USER = "godin";
  private static final String SCM_PASSWORD = "secretpassword";

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

  @Ignore
  @Test
  public void testGetRepositorySecured() throws Exception {
    ScmManager scmManager = mock(ScmManager.class);
    Scm scm = mock(Scm.class);
    ScmRepository repository = mock(ScmRepository.class);
    ScmProviderRepository providerRepository = mock(ScmProviderRepository.class);
    when(scm.getDeveloperConnection()).thenReturn(SCM_CONNECTION);
    when(repository.getProviderRepository()).thenReturn(providerRepository);
    when(scmManager.makeScmRepository(SCM_CONNECTION)).thenReturn(repository);

    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(ScmActivityPlugin.USER_PROPERTY, SCM_USER);
    configuration.setProperty(ScmActivityPlugin.PASSWORD_PROPERTY, SCM_PASSWORD);
    MavenProject pom = new MavenProject();
    pom.setScm(scm);
    project
        .setConfiguration(configuration)
        .setPom(pom);
    // ScmRepository actual = sensor.getRepository(scmManager);

    // assertSame(repository, actual);
    // verify(providerRepository).setUser(SCM_USER);
    // verify(providerRepository).setPassword(SCM_PASSWORD);
  }

  @Ignore
  @Test
  public void testGetRepositoryUnsecured() throws Exception {
    ScmManager scmManager = mock(ScmManager.class);
    Scm scm = mock(Scm.class);
    ScmRepository repository = mock(ScmRepository.class);
    when(scm.getConnection()).thenReturn(SCM_CONNECTION);
    when(scmManager.makeScmRepository(SCM_CONNECTION)).thenReturn(repository);

    MavenProject pom = new MavenProject();
    pom.setScm(scm);
    project
        .setConfiguration(new BaseConfiguration())
        .setPom(pom);

    // ScmRepository actual = sensor.getRepository(scmManager);

    // assertSame(repository, actual);
  }

  @Test
  public void testToString() {
    assertThat(sensor.toString(), is("ScmActivitySensor"));
  }
}
