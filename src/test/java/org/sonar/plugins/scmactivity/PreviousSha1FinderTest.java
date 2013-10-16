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
import org.junit.Test;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.batch.TimeMachineQuery;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreviousSha1FinderTest {
  PreviousSha1Finder previousSha1Finder;

  TimeMachine timeMachine = mock(TimeMachine.class);
  Resource resource = mock(Resource.class);

  @Before
  public void setUp() {
    previousSha1Finder = new PreviousSha1Finder(timeMachine);
  }

  @Test
  public void should_find_previous_sha1() {
    when(timeMachine.getMeasures(timeMachineQuery(resource, ScmActivityMetrics.SCM_HASH)))
        .thenReturn(Arrays.asList(sha1Measure("abcdef")));

    String sha1 = previousSha1Finder.find(resource);

    assertThat(sha1).isEqualTo("abcdef");
  }

  @Test
  public void shouldnt_find_missing_sha1() {
    String sha1 = previousSha1Finder.find(resource);

    assertThat(sha1).isEmpty();
  }

  static TimeMachineQuery timeMachineQuery(Resource resource, Metric metric) {
    return refEq(new TimeMachineQuery(resource).setMetrics(metric).setOnlyLastAnalysis(true));
  }

  static Measure sha1Measure(String sha1) {
    return new Measure(ScmActivityMetrics.SCM_HASH, sha1);
  }
}
