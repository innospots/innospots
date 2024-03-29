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

package io.innospots.workflow.core.execution.reader;

import io.innospots.base.model.PageBody;
import io.innospots.workflow.core.execution.flow.FlowExecutionBase;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeExecutionDisplay;
import io.innospots.workflow.core.execution.operator.IFlowExecutionOperator;
import io.innospots.workflow.core.execution.operator.INodeExecutionOperator;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the node execution display in the flow canvas
 *
 * @author Smars
 * @version 1.0.0
 * @date 2022/2/26
 */
public class NodeExecutionReader {

    private INodeExecutionOperator INodeExecutionOperator;

    private IFlowExecutionOperator IFlowExecutionOperator;

    public NodeExecutionReader(INodeExecutionOperator INodeExecutionOperator,
                               IFlowExecutionOperator IFlowExecutionOperator) {
        this.INodeExecutionOperator = INodeExecutionOperator;
        this.IFlowExecutionOperator = IFlowExecutionOperator;
    }

    public Map<String, NodeExecutionDisplay> readExecutionByFlowExecutionId(String flowExecutionId, List<String> nodeKeys) {
        return readExecutionByFlowExecutionId(flowExecutionId, nodeKeys, true);
    }

    public Map<String, NodeExecutionDisplay> readExecutionByFlowExecutionId(String flowExecutionId, List<String> nodeKeys, boolean includeContext) {


        List<NodeExecution> nodeExecutions = INodeExecutionOperator.
                getNodeExecutionsByFlowExecutionId(flowExecutionId, nodeKeys, includeContext);

        if (CollectionUtils.isEmpty(nodeExecutions)) {
            return Collections.emptyMap();
        }

        Map<String, NodeExecutionDisplay> nodeDisplays = new HashMap<>(nodeExecutions.size());
        for (NodeExecution nodeExecution : nodeExecutions) {
            nodeDisplays.put(nodeExecution.getNodeKey(), NodeExecutionDisplay.build(nodeExecution));
        }

        return nodeDisplays;
    }

    public Map<String, NodeExecutionDisplay> readLatestNodeExecutionByFlowInstanceId(Long workflowInstanceId, Integer revision, List<String> nodeKeys) {

        PageBody<FlowExecutionBase> flowExecutions = IFlowExecutionOperator.pageLatestFlowExecutions(workflowInstanceId, revision, 0, 1);

        if (CollectionUtils.isEmpty(flowExecutions.getList())) {
            return Collections.emptyMap();
        }

        return readExecutionByFlowExecutionId(flowExecutions.getList().get(0).getFlowExecutionId(), nodeKeys);
    }

    public NodeExecutionDisplay findNodeExecution(String nodeExecutionId, int page, int size) {
        return NodeExecutionDisplay.build(INodeExecutionOperator.getNodeExecutionById(nodeExecutionId, true, page, size));
    }


}
