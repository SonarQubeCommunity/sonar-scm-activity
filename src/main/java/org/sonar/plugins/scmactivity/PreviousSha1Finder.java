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

import com.google.common.collect.Iterables;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.TimeMachine;
import org.sonar.api.batch.TimeMachineQuery;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Resource;

import java.util.List;

public class PreviousSha1Finder implements BatchExtension {
  private final TimeMachine timeMachine;

  public PreviousSha1Finder(TimeMachine timeMachine) {
    this.timeMachine = timeMachine;
  }

  public String find(Resource resource) {
    List<Measure> measures = timeMachine.getMeasures(queryPreviousHash(resource));
    if (measures.isEmpty()) {
      return "";
    }

    return Iterables.getOnlyElement(measures).getData();
  }

  private static TimeMachineQuery queryPreviousHash(Resource resource) {
    return new TimeMachineQuery(resource)
        .setMetrics(ScmActivityMetrics.SCM_HASH)
        .setOnlyLastAnalysis(true);
  }
}
