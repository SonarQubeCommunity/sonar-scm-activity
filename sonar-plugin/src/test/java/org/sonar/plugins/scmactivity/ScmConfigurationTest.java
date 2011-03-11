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
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScmConfigurationTest {

  private MavenScmConfiguration mavenConf;
  private PropertiesConfiguration configuration;
  private ScmConfiguration scmConfiguration;

  @Before
  public void setUp() {
    mavenConf = mock(MavenScmConfiguration.class);
    configuration = new PropertiesConfiguration();
    scmConfiguration = new ScmConfiguration(new Project("key"), configuration, mavenConf);
  }

  @Test
  public void shouldReturnUsername() {
    configuration.addProperty(ScmActivityPlugin.USER_PROPERTY, "godin");
    assertThat(scmConfiguration.getUser(), is("godin"));
  }

  @Test
  public void shouldReturnPassword() {
    configuration.addProperty(ScmActivityPlugin.PASSWORD_PROPERTY, "pass");
    assertThat(scmConfiguration.getPassword(), is("pass"));
  }

  @Test
  public void shouldReturnUrlFromConfiguration() {
    configuration.addProperty(ScmActivityPlugin.URL_PROPERTY, "http://test");
    assertThat(scmConfiguration.getUrl(), is("http://test"));
  }

  @Test
  public void shouldBeDisabledIfNoUrl() {
    configuration.addProperty(ScmActivityPlugin.ENABLED_PROPERTY, true);
    assertThat(scmConfiguration.isEnabled(), is(false));
  }

  @Test
  public void shouldBeDisabledByDefault() {
    assertThat(scmConfiguration.isEnabled(), is(false));
  }


  @Test
  public void shouldBeEnabled() {
    configuration.addProperty(ScmActivityPlugin.ENABLED_PROPERTY, true);
    configuration.addProperty(ScmActivityPlugin.URL_PROPERTY, "scm:http:xxx");
    assertThat(scmConfiguration.isEnabled(), is(true));
  }


  @Test
  public void shouldGetMavenDeveloperUrlIfCredentials() {
    when(mavenConf.getDeveloperUrl()).thenReturn("scm:https:writable");
    configuration.addProperty(ScmActivityPlugin.USER_PROPERTY, "godin");
    configuration.addProperty(ScmActivityPlugin.PASSWORD_PROPERTY, "pass");

    assertThat(scmConfiguration.getUrl(), is("scm:https:writable"));
  }

  @Test
  public void shouldNotGetMavenDeveloperUrlIfNoCredentials() {
    when(mavenConf.getDeveloperUrl()).thenReturn("scm:https:writable");
    when(mavenConf.getUrl()).thenReturn("scm:https:readonly");
    
    assertThat(scmConfiguration.getUrl(), is("scm:https:readonly"));
  }

  @Test
  public void shouldGetMavenUrlIfNoDeveloperUrl() {
    when(mavenConf.getUrl()).thenReturn("scm:http:readonly");
    assertThat(scmConfiguration.getUrl(), is("scm:http:readonly"));
  }

  @Test
  public void shouldOverrideMavenUrl() {
    when(mavenConf.getUrl()).thenReturn("scm:http:readonly");
    configuration.addProperty(ScmActivityPlugin.URL_PROPERTY, "scm:http:override");

    assertThat(scmConfiguration.getUrl(), is("scm:http:override"));
  }
}
