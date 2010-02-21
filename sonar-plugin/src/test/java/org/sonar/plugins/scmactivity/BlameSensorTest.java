/*
 * Copyright (C) 2010 Evgeny Mandrikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.scmactivity;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsMeasure;
import org.sonar.api.test.IsResource;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.sonar.api.resources.Resource.QUALIFIER_CLASS;
import static org.sonar.api.resources.Resource.SCOPE_ENTITY;

/**
 * @author Evgeny Mandrikov
 */
public class BlameSensorTest {
  private static final String RESOURCE_KEY = "org.example.HelloWorld";

  private ExtScmManager scmManager;
  private SensorContext context;
  private BlameSensor sensor;

  @Before
  public void setUp() {
    scmManager = mock(ExtScmManager.class);
    ScmRepository repository = mock(ScmRepository.class);
    context = mock(SensorContext.class);
    sensor = spy(new BlameSensor(scmManager, repository, context));
  }

  /**
   * See SONARPLUGINS-368
   *
   * @throws Exception if something wrong
   */
  @Test
  public void testScmException() throws Exception {
    //noinspection ThrowableInstanceNeverThrown
    doThrow(new ScmException("ERROR"))
        .when(sensor)
        .analyseBlame((File) any(), (String) any(), (Resource) any());

    sensor.analyse(new File("."), new JavaFile(RESOURCE_KEY));

    verifyNoMoreInteractions(context);
  }

  @Test
  public void testAnalyse() throws Exception {
    when(
        scmManager.blame((ScmRepository) any(), (ScmFileSet) any(), (String) any())
    ).thenReturn(
        new BlameScmResult("fake", Arrays.asList(
            new BlameLine(new Date(13), "2", "godin"),
            new BlameLine(new Date(10), "1", "godin")
        ))
    );

    sensor.analyseBlame(new File("."), "HelloWorld.java", new JavaFile(RESOURCE_KEY));

    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.LAST_ACTIVITY, BlameSensor.formatLastActivity(new Date(13))))
    );
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.REVISION, "2"))
    );
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.BLAME_AUTHORS_DATA))
    );
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.BLAME_DATE_DATA))
    );
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, RESOURCE_KEY)),
        argThat(new IsMeasure(ScmActivityMetrics.BLAME_REVISION_DATA))
    );
    verifyNoMoreInteractions(context);
  }

  @Test
  public void test() throws Exception {
    when(
        scmManager.blame((ScmRepository) any(), (ScmFileSet) any(), (String) any())
    ).thenReturn(
        new BlameScmResult("command", "Provider message", "output", false)
    );

    try {
      sensor.analyseBlame(new File("."), "HelloWorld.java", new JavaFile(RESOURCE_KEY));
    } catch (ScmException e) {
      Assert.assertThat(e.getMessage(), is("Provider message"));
    }
    verifyNoMoreInteractions(context);
  }
}
