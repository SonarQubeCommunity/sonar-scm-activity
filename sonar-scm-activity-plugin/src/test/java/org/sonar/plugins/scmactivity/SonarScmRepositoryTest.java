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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SonarScmRepositoryTest {
  ScmConfiguration conf = mock(ScmConfiguration.class);
  ScmManager manager = mock(ScmManager.class);
  ScmRepository repository = mock(ScmRepository.class);
  ScmProviderRepository provider = mock(ScmProviderRepository.class);
  SonarScmRepository repo;

  @Before
  public void setUp() {
    repo = new SonarScmRepository(manager, conf);
  }

  @Test
  public void should_set_credentials() throws Exception {
    when(conf.getUrl()).thenReturn("/url");
    when(conf.getUser()).thenReturn("godin");
    when(conf.getPassword()).thenReturn("pass");
    when(manager.makeScmRepository("/url")).thenReturn(repository);
    when(repository.getProviderRepository()).thenReturn(provider);

    ScmRepository scmRepository = repo.getScmRepository();

    assertThat(scmRepository).isSameAs(repo.getScmRepository());
    verify(provider).setUser("godin");
    verify(provider).setPassword("pass");
  }

  @Test
  public void should_not_set_credentials_for_blank_user() throws Exception {
    when(conf.getUser()).thenReturn("");
    when(conf.getPassword()).thenReturn("");
    when(conf.getUrl()).thenReturn("/url");
    when(manager.makeScmRepository("/url")).thenReturn(repository);

    ScmRepository scmRepository = repo.getScmRepository();

    assertThat(scmRepository).isSameAs(repo.getScmRepository());
    verify(provider, never()).setUser(anyString());
    verify(provider, never()).setPassword(anyString());
  }

  @Test
  public void should_set_credentials_for_blank_password() throws Exception {
    when(conf.getUrl()).thenReturn("/url");
    when(conf.getUser()).thenReturn("login");
    when(conf.getPassword()).thenReturn("");
    when(manager.makeScmRepository("/url")).thenReturn(repository);
    when(repository.getProviderRepository()).thenReturn(provider);

    ScmRepository scmRepository = repo.getScmRepository();

    assertThat(scmRepository).isSameAs(repo.getScmRepository());
    verify(provider).setUser("login");
    verify(provider).setPassword("");
  }
}
