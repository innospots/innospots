/*
 *  Copyright © 2021-2023 Innospots (http://www.innospots.com)
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.innospots.workflow.core.node.builder;

import io.innospots.workflow.core.enums.BuildStatus;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Smars
 * @date 2021/3/23
 */
public class NodeBuildCounter {

    private AtomicInteger loadedNodeCount = new AtomicInteger();

    private AtomicInteger failBuildNodeCount = new AtomicInteger();

    public int count(String nodeKey, BuildStatus buildStatus) {

        if (buildStatus == BuildStatus.DONE) {
            loadedNodeCount.incrementAndGet();
        } else if (buildStatus == BuildStatus.FAIL) {
            failBuildNodeCount.incrementAndGet();
        }
        return loadedNodeCount.get();
    }

    public int loadedNodeSize() {
        return loadedNodeCount.get();
    }

    public int failNodeSize() {
        return failBuildNodeCount.get();
    }
}
