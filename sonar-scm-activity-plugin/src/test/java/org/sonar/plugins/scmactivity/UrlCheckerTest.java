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
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.utils.SonarException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlCheckerTest {

  private ScmConfiguration conf;
  private ScmManager manager;
  private UrlChecker checker;

  @Before
  public void setUp() throws Exception {
    conf = mock(ScmConfiguration.class);
    when(conf.isEnabled()).thenReturn(true);

    manager = new SonarScmManager(conf);
    checker = new UrlChecker(manager, conf);
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfBlank() {
    checker.check(" ");
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfBadlyFormed() {
    checker.check("http://foo");
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfProviderNotSupported() {
    checker.check("scm:synergy:foo");
  }

  @Test
  public void shouldAcceptSvnUrl() {
    checker.check("scm:svn:https://codehaus.org");
  }
}
