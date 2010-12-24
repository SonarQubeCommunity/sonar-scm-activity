package org.sonar.plugins.scmactivity;

import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.Test;

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
}
