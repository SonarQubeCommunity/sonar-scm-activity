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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.SonarException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlCheckerTest {
  UrlChecker checker;

  ScmConfiguration configuration = mock(ScmConfiguration.class);

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    when(configuration.isEnabled()).thenReturn(true);

    checker = new UrlChecker(new SonarScmManager(configuration), configuration);
  }

  @Test
  public void shouldFailIfBlank() {
    when(configuration.getUrl()).thenReturn(" ");

    exception.expect(SonarException.class);
    exception.expectMessage("SCM URL must not be blank");

    checker.check();
  }

  @Test
  public void shouldFailIfBadlyFormed() {
    when(configuration.getUrl()).thenReturn("http://foo");

    exception.expect(SonarException.class);
    exception.expectMessage("URL does not respect the SCM URL format described in http://maven.apache.org/scm/scm-url-format.html");
    exception.expectMessage("[http://foo]");

    checker.check();
  }

  @Test
  public void shouldFailIfProviderNotSupported() {
    when(configuration.getUrl()).thenReturn("scm:synergy:foo");

    exception.expect(SonarException.class);
    exception.expectMessage("Unsupported SCM");
    exception.expectMessage("[synergy]");

    checker.check();
  }

  @Test
  public void shouldAcceptSvnUrl() {
    when(configuration.getUrl()).thenReturn("scm:svn:https://codehaus.org");

    checker.check();
  }
}
