package org.sonar.plugins.scmactivity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivityPluginTest {
  private ScmActivityPlugin plugin;

  @Before
  public void setUp() throws Exception {
    plugin = new ScmActivityPlugin();
  }

  @Test
  public void testGetExtensions() throws Exception {
    assertEquals(plugin.getExtensions().size(), 4);
  }
}
