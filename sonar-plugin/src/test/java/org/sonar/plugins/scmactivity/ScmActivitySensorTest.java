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

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensorTest {
  private static final String SCM_CONNECTION = "scm:svn:https://localhost";
  private static final String SCM_USER = "godin";
  private static final String SCM_PASSWORD = "secretpassword";

  private ScmActivitySensor sensor;

  @Before
  public void setUp() {
    sensor = new ScmActivitySensor();
  }

  /**
   * See SONARPLUGINS-350
   */
  @Test
  public void noExecutionIfNotLatestAnalysis() {
    Project project = new Project("")
        .setLatestAnalysis(false);

    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void noExecutionIfDisabled() {
    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(ScmActivityPlugin.ENABLED_PROPERTY, false);
    Project project = new Project("")
        .setLatestAnalysis(true)
        .setConfiguration(configuration);

    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void noExecutionIfScmNotDefined() {
    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(ScmActivityPlugin.ENABLED_PROPERTY, true);
    MavenProject pom = new MavenProject();
    Project project = new Project("")
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
    Project project = new Project("")
        .setLatestAnalysis(true)
        .setPom(pom)
        .setConfiguration(configuration);

    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }

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
    Project project = new Project("")
        .setConfiguration(configuration)
        .setPom(pom);
    ScmRepository actual = sensor.getRepository(scmManager, project);

    assertSame(repository, actual);
    verify(providerRepository).setUser(SCM_USER);
    verify(providerRepository).setPassword(SCM_PASSWORD);
  }

  @Test
  public void testGetRepositoryUnsecured() throws Exception {
    ScmManager scmManager = mock(ScmManager.class);
    Scm scm = mock(Scm.class);
    ScmRepository repository = mock(ScmRepository.class);
    when(scm.getConnection()).thenReturn(SCM_CONNECTION);
    when(scmManager.makeScmRepository(SCM_CONNECTION)).thenReturn(repository);

    MavenProject pom = new MavenProject();
    pom.setScm(scm);
    Project project = new Project("")
        .setConfiguration(new BaseConfiguration())
        .setPom(pom);

    ScmRepository actual = sensor.getRepository(scmManager, project);

    assertSame(repository, actual);
  }

  @Test
  public void testToString() {
    assertThat(sensor.toString(), is("ScmActivitySensor"));
  }
} 
