package org.sonar.plugins.scmactivity;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivityWidgetTest {
  @Test
  public void testGetTemplatePath() {
    String path = new ScmActivityWidget().getTemplatePath();
    assertThat(getClass().getResource(path), not(nullValue()));
  }
}
