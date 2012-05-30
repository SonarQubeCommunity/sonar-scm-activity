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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScmConfigurationTest {

  private ScmConfiguration scmConfiguration;
  private PropertiesConfiguration configuration;
  private final MavenScmConfiguration mavenConf = mock(MavenScmConfiguration.class);
  private final ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);

  @Before
  public void setUp() {
    configuration = new PropertiesConfiguration();
    scmConfiguration = new ScmConfiguration(projectFileSystem, configuration, mavenConf);
  }

  @Test
  public void shouldReturnUsername() {
    configuration.addProperty(ScmActivityPlugin.USER_PROPERTY, "godin");

    assertThat(scmConfiguration.getUser()).isEqualTo("godin");
  }

  @Test
  public void shouldReturnPassword() {
    configuration.addProperty(ScmActivityPlugin.PASSWORD_PROPERTY, "pass");

    assertThat(scmConfiguration.getPassword()).isEqualTo("pass");
  }

  @Test
  public void shouldReturnUrlFromConfiguration() {
    configuration.addProperty(ScmActivityPlugin.URL_PROPERTY, "http://test");

    assertThat(scmConfiguration.getUrl()).isEqualTo("http://test");
  }

  @Test
  public void shouldBeDisabledIfNoUrl() {
    configuration.addProperty(ScmActivityPlugin.ENABLED_PROPERTY, true);

    assertThat(scmConfiguration.isEnabled()).isFalse();
  }

  @Test
  public void shouldBeDisabledByDefault() {
    assertThat(scmConfiguration.isEnabled()).isFalse();
  }

  @Test
  public void shouldBeEnabled() {
    configuration.addProperty(ScmActivityPlugin.ENABLED_PROPERTY, true);
    configuration.addProperty(ScmActivityPlugin.URL_PROPERTY, "scm:svn:http:foo");

    assertThat(scmConfiguration.isEnabled()).isTrue();
  }

  @Test
  public void shouldGetMavenDeveloperUrlIfCredentials() {
    when(mavenConf.getDeveloperUrl()).thenReturn("scm:svn:https:writable");
    configuration.addProperty(ScmActivityPlugin.USER_PROPERTY, "godin");
    configuration.addProperty(ScmActivityPlugin.PASSWORD_PROPERTY, "pass");

    assertThat(scmConfiguration.getUrl()).isEqualTo("scm:svn:https:writable");
  }

  @Test
  public void shouldNotGetMavenDeveloperUrlIfNoCredentials() {
    when(mavenConf.getDeveloperUrl()).thenReturn("scm:svn:https:writable");
    when(mavenConf.getUrl()).thenReturn("scm:svn:https:readonly");

    assertThat(scmConfiguration.getUrl()).isEqualTo("scm:svn:https:readonly");
  }

  @Test
  public void shouldGetMavenUrlIfNoDeveloperUrl() {
    when(mavenConf.getUrl()).thenReturn("scm:svn:http:readonly");

    assertThat(scmConfiguration.getUrl()).isEqualTo("scm:svn:http:readonly");
  }

  @Test
  public void shouldOverrideMavenUrl() {
    when(mavenConf.getUrl()).thenReturn("scm:svn:http:readonly");
    configuration.addProperty(ScmActivityPlugin.URL_PROPERTY, "scm:svn:http:override");

    assertThat(scmConfiguration.getUrl()).isEqualTo("scm:svn:http:override");
  }

  @Test
  public void shouldGetScmProvider() {
    when(mavenConf.getUrl()).thenReturn("scm:svn:http:foo");

    assertThat(scmConfiguration.getScmProvider()).isEqualTo("svn");
  }

  @Test
  public void should_get_empty_scm_provider() {
    when(mavenConf.getUrl()).thenReturn(" ");

    assertThat(scmConfiguration.getScmProvider()).isNull();
  }

  @Test
  public void should_ignore_local_modifications() {
    configuration.addProperty(ScmActivityPlugin.IGNORE_LOCAL_MODIFICATIONS, true);

    assertThat(scmConfiguration.isIgnoreLocalModifications()).isTrue();
  }

  @Test
  public void shouldnt_ignore_local_modifications() {
    assertThat(scmConfiguration.isIgnoreLocalModifications()).isFalse();
  }

  @Test
  public void should_get_maven_url_in_non_maven_environment() {
    scmConfiguration = new ScmConfiguration(projectFileSystem, configuration);

    assertThat(scmConfiguration.getUrl()).isNull();
  }

  @Test
  public void should_get_source_dirs() {
    when(projectFileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File("src")));
    when(projectFileSystem.getTestDirs()).thenReturn(Arrays.asList(new File("test")));

    assertThat(scmConfiguration.getSourceDirs()).containsExactly(new File("src"), new File("test"));
  }
}
