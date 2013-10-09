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

import com.google.common.collect.ImmutableList;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.List;

public final class ScmActivityMetrics implements Metrics {
  /**
   * @since 1.4
   */
  public static final String SCM_HASH_KEY = "scm.hash";

  /**
   * @since 1.4. Hidden since 1.5
   */
  public static final Metric SCM_HASH = new Metric
      .Builder(SCM_HASH_KEY, "Hash", Metric.ValueType.STRING)
          .setDomain(CoreMetrics.DOMAIN_SCM)
          .setHidden(true)
          .create();

  public List<Metric> getMetrics() {
    return ImmutableList.of(SCM_HASH);
  }
}
