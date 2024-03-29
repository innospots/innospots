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

package io.innospots.workflow.core.execution;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/2/4
 */
@Getter
@Setter
public class ExecutionInput {

    private String sourceKey;

    private List<Map<String, Object>> data = new ArrayList<>();

    private List<ExecutionResource> resources;

    public ExecutionInput() {
    }

    public ExecutionInput(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public void addInput(Map<String, Object> input) {
        this.data.add(input);
    }

    public void addInput(List<Map<String, Object>> inputs) {
        this.data.addAll(inputs);
    }

    public void addResource(ExecutionResource resource) {
        if (this.resources == null) {
            this.resources = new ArrayList<>();
        }
        this.resources.add(resource);
    }

    public void addResource(List<ExecutionResource> resources) {
        if (this.resources == null) {
            this.resources = new ArrayList<>();
        }
        if (resources != null) {
            this.resources.addAll(resources);
        }
    }

    public ExecutionInput copy() {
        ExecutionInput input = new ExecutionInput();
        input.sourceKey = this.getSourceKey();
        input.resources = this.resources;
        return input;
    }

    public boolean isEmptyResource() {
        return this.resources == null || this.resources.isEmpty();
    }

}
