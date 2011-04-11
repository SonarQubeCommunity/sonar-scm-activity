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
import org.junit.Test;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SonarScmRepositoryTest {

  @Test
  public void shouldSetCredentials() throws Exception {
    ScmConfiguration conf = mock(ScmConfiguration.class);
    when(conf.getUser()).thenReturn("godin");
    when(conf.getPassword()).thenReturn("pass");

    ScmManager manager = mock(ScmManager.class);
    ScmRepository repository = mock(ScmRepository.class);
    ScmProviderRepository provider = mock(ScmProviderRepository.class);
    when(manager.makeScmRepository(anyString())).thenReturn(repository);
    when(repository.getProviderRepository()).thenReturn(provider);

    SonarScmRepository repo = new SonarScmRepository(manager, conf);
    assertThat(repo.getScmRepository(), sameInstance(repo.getScmRepository()));

    verify(provider).setUser("godin");
    verify(provider).setPassword("pass");
  }

  @Test
  public void shouldNotSetCredentials() throws Exception {
    ScmConfiguration conf = mock(ScmConfiguration.class);

    ScmManager manager = mock(ScmManager.class);
    ScmRepository repository = mock(ScmRepository.class);
    ScmProviderRepository provider = mock(ScmProviderRepository.class);
    when(manager.makeScmRepository(anyString())).thenReturn(repository);
    when(repository.getProviderRepository()).thenReturn(provider);

    SonarScmRepository repo = new SonarScmRepository(manager, conf);
    assertThat(repo.getScmRepository(), sameInstance(repo.getScmRepository()));

    verify(provider, never()).setUser(anyString());
    verify(provider, never()).setPassword(anyString());
  }

}
