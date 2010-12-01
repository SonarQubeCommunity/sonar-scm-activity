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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;

public class ScmConfigurationTest {

  private Project project;
  private PropertiesConfiguration configuration;
  private ScmConfiguration scmConfiguration;

  @Before
  public void setUp() {
    configuration = new PropertiesConfiguration();
    project = new Project("project").setConfiguration(configuration);
    scmConfiguration = new ScmConfiguration(project);
  }

  @Test
  public void shouldBeDisabledByDefault() {
    assertThat(scmConfiguration.isEnabled(), is(false));
  }

  @Test
  public void shouldUsePureJavaByDefault() {
    assertThat(scmConfiguration.isPureJava(), is(true));
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

}
