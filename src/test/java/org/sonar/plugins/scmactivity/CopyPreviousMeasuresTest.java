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

import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.batch.TimeMachineQuery;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsMeasure;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.refEq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CopyPreviousMeasuresTest {
  TimeMachine timeMachine = mock(TimeMachine.class);
  Resource resource = mock(Resource.class);
  SensorContext context = mock(SensorContext.class);

  @Test
  public void should_copy_previous_measures() {
    when(timeMachine.getMeasures(refEq(expectedQuery(resource)))).thenReturn(expectedMeasures("Measure1", "Measure2", "Measure3"));

    CopyPreviousMeasures copy = new CopyPreviousMeasures(resource);
    copy.execute(timeMachine, context);

    verify(context).saveMeasure(same(resource), argThat(new IsMeasure(CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE, "Measure1")));
    verify(context).saveMeasure(same(resource), argThat(new IsMeasure(CoreMetrics.SCM_REVISIONS_BY_LINE, "Measure2")));
    verify(context).saveMeasure(same(resource), argThat(new IsMeasure(CoreMetrics.SCM_AUTHORS_BY_LINE, "Measure3")));
  }

  static TimeMachineQuery expectedQuery(Resource resource) {
    return new TimeMachineQuery(resource)
        .setOnlyLastAnalysis(true)
        .setMetrics(
            CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE,
            CoreMetrics.SCM_REVISIONS_BY_LINE,
            CoreMetrics.SCM_AUTHORS_BY_LINE);
  }

  static List<Measure> expectedMeasures(String value1, String value2, String value3) {
    return Arrays.asList(
        new Measure(CoreMetrics.SCM_LAST_COMMIT_DATETIMES_BY_LINE, value1),
        new Measure(CoreMetrics.SCM_REVISIONS_BY_LINE, value2),
        new Measure(CoreMetrics.SCM_AUTHORS_BY_LINE, value3));
  }
}
