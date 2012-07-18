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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.SonarException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScmConfigurationTest {
  ScmConfiguration scmConfiguration;

  PropertiesConfiguration configuration = new PropertiesConfiguration();
  MavenScmConfiguration mavenConf = mock(MavenScmConfiguration.class);

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() {
    scmConfiguration = new ScmConfiguration(configuration, mavenConf);
  }

  @Test
  public void shouldReturnUsername() {
    configuration.addProperty(ScmActivityPlugin.USER, "godin");

    assertThat(scmConfiguration.getUser()).isEqualTo("godin");
  }

  @Test
  public void shouldReturnPassword() {
    configuration.addProperty(ScmActivityPlugin.PASSWORD, "pass");

    assertThat(scmConfiguration.getPassword()).isEqualTo("pass");
  }

  @Test
  public void shouldReturnUrlFromConfiguration() {
    configuration.addProperty(ScmActivityPlugin.URL, "http://test");

    assertThat(scmConfiguration.getUrl()).isEqualTo("http://test");
  }

  @Test
  public void shouldBeDisabled() {
    configuration.addProperty(ScmActivityPlugin.ENABLED, false);
    configuration.addProperty(ScmActivityPlugin.URL, "scm:svn:http:foo");

    assertThat(scmConfiguration.isEnabled()).isFalse();
  }

  @Test
  public void shouldBeDisabledIfNoUrl() {
    configuration.addProperty(ScmActivityPlugin.ENABLED, true);

    assertThat(scmConfiguration.isEnabled()).isFalse();
  }

  @Test
  public void shouldBeDisabledByDefault() {
    assertThat(scmConfiguration.isEnabled()).isFalse();
  }

  @Test
  public void shouldBeEnabled() {
    configuration.addProperty(ScmActivityPlugin.ENABLED, true);
    configuration.addProperty(ScmActivityPlugin.URL, "scm:svn:http:foo");

    assertThat(scmConfiguration.isEnabled()).isTrue();
  }

  @Test
  public void should_get_default_thread_count() {
    assertThat(scmConfiguration.getThreadCount()).isEqualTo(4);
  }

  @Test
  public void should_get_thread_count() {
    configuration.addProperty(ScmActivityPlugin.THREAD_COUNT, 8);

    assertThat(scmConfiguration.getThreadCount()).isEqualTo(8);
  }

  @Test
  public void should_use_single_thread() {
    configuration.addProperty(ScmActivityPlugin.THREAD_COUNT, 1);

    assertThat(scmConfiguration.getThreadCount()).isEqualTo(1);
  }

  @Test
  public void should_fail_on_invalid_thread_count() {
    configuration.addProperty(ScmActivityPlugin.THREAD_COUNT, 0);

    exception.expect(SonarException.class);
    exception.expectMessage("SCM Activity Plugin is configured to use [0] thread(s). The minimum is 1.");

    scmConfiguration.getThreadCount();
  }

  @Test
  public void should_accept_thread_count_too_high() {
    configuration.addProperty(ScmActivityPlugin.THREAD_COUNT, 1000);

    assertThat(scmConfiguration.getThreadCount()).isEqualTo(1000);
  }

  @Test
  public void shouldGetMavenDeveloperUrlIfCredentials() {
    when(mavenConf.getDeveloperUrl()).thenReturn("scm:svn:https:writable");
    configuration.addProperty(ScmActivityPlugin.USER, "godin");
    configuration.addProperty(ScmActivityPlugin.PASSWORD, "pass");

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
    configuration.addProperty(ScmActivityPlugin.URL, "scm:svn:http:override");

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
  public void should_get_maven_url_in_non_maven_environment() {
    scmConfiguration = new ScmConfiguration(configuration);

    assertThat(scmConfiguration.getUrl()).isNull();
  }
}
