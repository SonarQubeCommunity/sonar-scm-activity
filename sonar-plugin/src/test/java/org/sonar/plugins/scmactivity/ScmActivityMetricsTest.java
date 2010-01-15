package org.sonar.plugins.scmactivity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivityMetricsTest {
  private ScmActivityMetrics metrics;

  @Before
  public void setUp() {
    metrics = new ScmActivityMetrics();
  }

  @Test
  public void testGetMetrics() throws Exception {
    assertEquals(metrics.getMetrics().size(), 2);
  }
}
