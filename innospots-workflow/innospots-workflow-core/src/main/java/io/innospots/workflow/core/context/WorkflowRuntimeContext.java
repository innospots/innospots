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

package io.innospots.workflow.core.context;


import io.innospots.workflow.core.execution.flow.FlowExecution;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Smars
 * @date 2021/3/10
 */
@Getter
@Setter
public class WorkflowRuntimeContext {

    private FlowExecution flowExecution;

    private Map<String, Object> contexts = new ConcurrentHashMap<>();

    /**
     *
     */
    private String path;


    private Map<String, Object> response = new LinkedHashMap<>();

    public static String contextResourcePath = ".execution_contexts";

    private WorkflowRuntimeContext() {
    }

    public static WorkflowRuntimeContext build(FlowExecution flowExecution) {
        WorkflowRuntimeContext workflowRuntimeContext = new WorkflowRuntimeContext();
        workflowRuntimeContext.flowExecution = flowExecution;
        return workflowRuntimeContext;
    }

    public void addContext(Map<String, Object> context) {
        if (context != null) {
            contexts.putAll(context);
        }
    }

    public void addResponse(String key) {
        if (this.response == null) {
            this.response = new LinkedHashMap<>();
        }
        /*
        if(flowExecution!=null){
            this.response.put(key,flowExecution.get(key));
        }
         */
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RuntimeContext{");
        sb.append("flowExecution=").append(flowExecution);
        sb.append(", path='").append(path).append('\'');
        sb.append(", response=").append(response);
        sb.append('}');
        return sb.toString();
    }
}
