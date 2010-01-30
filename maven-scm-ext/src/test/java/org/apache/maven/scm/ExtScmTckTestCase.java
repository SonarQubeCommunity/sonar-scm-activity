package org.apache.maven.scm;

import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.manager.ExtScmManagerFactory;

/**
 * @author Evgeny Mandrikov
 */
public abstract class ExtScmTckTestCase extends ScmTckTestCase {
  protected abstract boolean isPureJava();

  @Override
  protected ExtScmManager getScmManager() throws Exception {
    return ExtScmManagerFactory.getScmManager(isPureJava());
  }
}
