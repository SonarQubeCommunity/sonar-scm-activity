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

import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ProjectScmManagerTest {

  private ScmConfiguration configuration;
  private ScmManager scmManager;
  private ScmRepository repository;
  private ScmProviderRepository providerRepository;
  private ProjectScmManager projectScmManager;

  @Before
  public void setUp() throws Exception {
    configuration = mock(ScmConfiguration.class);
    scmManager = mock(ScmManager.class);
    repository = mock(ScmRepository.class);
    providerRepository = mock(ScmProviderRepository.class);
    when(scmManager.makeScmRepository(anyString())).thenReturn(repository);
    when(repository.getProviderRepository()).thenReturn(providerRepository);

    projectScmManager = spy(new ProjectScmManager(null, configuration));
    doReturn(scmManager).when(projectScmManager).getScmManager();
  }

  @Test
  public void shouldSetCredentials() throws Exception {
    when(configuration.getUser()).thenReturn("godin");
    when(configuration.getPassword()).thenReturn("pass");

    assertThat(projectScmManager.getScmRepository(), sameInstance(repository));

    verify(providerRepository).setUser("godin");
    verify(providerRepository).setPassword("pass");
  }

  @Test
  public void shouldNotSetCredentials() throws Exception {
    assertThat(projectScmManager.getScmRepository(), sameInstance(repository));

    verify(providerRepository, never()).setUser(anyString());
    verify(providerRepository, never()).setPassword(anyString());
  }

  @Test
  public void shouldBeDisabled() {
    when(configuration.isEnabled()).thenReturn(true).thenReturn(false);
    when(configuration.getUrl()).thenReturn("").thenReturn("scm:svn:http//localhost");

    assertThat(projectScmManager.isEnabled(), is(false));
    assertThat(projectScmManager.isEnabled(), is(false));
  }

  @Test
  public void shouldBeEnabled() {
    when(configuration.isEnabled()).thenReturn(true);
    when(configuration.getUrl()).thenReturn("scm:svn:http//localhost");

    assertThat(projectScmManager.isEnabled(), is(true));
  }

}
