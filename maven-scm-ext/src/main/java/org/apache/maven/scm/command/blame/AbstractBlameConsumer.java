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

package org.apache.maven.scm.command.blame;

import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.util.AbstractConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public abstract class AbstractBlameConsumer extends AbstractConsumer {
  private List lines = new ArrayList();

  public AbstractBlameConsumer(ScmLogger logger) {
    super(logger);
  }

  public List getLines() {
    return lines;
  }
}
