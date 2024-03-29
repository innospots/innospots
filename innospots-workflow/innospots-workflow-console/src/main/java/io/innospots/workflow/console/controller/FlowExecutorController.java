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

package io.innospots.workflow.console.controller;

import io.innospots.base.model.response.InnospotResponse;
import io.innospots.libra.base.log.OperateType;
import io.innospots.libra.base.log.OperationLog;
import io.innospots.libra.base.menu.ModuleMenu;
import io.innospots.libra.base.menu.ResourceItemOperation;
import io.innospots.workflow.core.debug.FlowNodeDebuggerBuilder;
import io.innospots.workflow.core.execution.flow.FlowExecutionBase;
import io.innospots.workflow.core.execution.node.NodeExecutionDisplay;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static io.innospots.base.model.response.InnospotResponse.success;
import static io.innospots.libra.base.controller.BaseController.PATH_ROOT_ADMIN;
import static io.innospots.libra.base.menu.ItemType.BUTTON;


/**
 * @author Smars
 * @date 2021/5/10
 */
@RestController
@RequestMapping(PATH_ROOT_ADMIN + "workflow")
@ModuleMenu(menuKey = "workflow-management")
@Tag(name = "Workflow Executor")
public class FlowExecutorController {

    @OperationLog(operateType = OperateType.EXECUTE, idParamPosition = 0)
    @PostMapping("execute/workflow-instance/{workflowInstanceId}")
    @Operation(summary = "execute flow")
    @ResourceItemOperation(key = "workflow-builder-execute", type = BUTTON, name = "${common.button.run}")
    public InnospotResponse<Map<String, NodeExecutionDisplay>> executeFlow(@Parameter(name = "workflowInstanceId") @PathVariable Long workflowInstanceId,
                                                                           @Parameter(name = "input") @RequestBody(required = false) Map<String, Object> input) {
        return executeNode(workflowInstanceId, null, input);
    }

    @OperationLog(operateType = OperateType.EXECUTE, idParamPosition = 0)
    @PostMapping("execute/workflow-instance/{workflowInstanceId}/node-instance/{nodeKey}/data")
    @Operation(summary = "execute this node using json input")
    @ResourceItemOperation(key = "workflow-builder-execute")
    public InnospotResponse<Map<String, NodeExecutionDisplay>> executeNode(@Parameter(name = "workflowInstanceId") @PathVariable Long workflowInstanceId,
                                                                           @Parameter(name = "nodeKey") @PathVariable String nodeKey,
                                                                           @Parameter(name = "input") @RequestBody(required = false) Map<String, Object> input) {
        return executeNode(workflowInstanceId, nodeKey, input == null ? null : newArrayList(input));
    }

    @OperationLog(operateType = OperateType.EXECUTE, idParamPosition = 0)
    @PostMapping("execute/workflow-instance/{workflowInstanceId}/node-instance/{nodeKey}/data-list")
    @Operation(summary = "execute this node using array input")
    @ResourceItemOperation(key = "workflow-builder-execute")
    public InnospotResponse<Map<String, NodeExecutionDisplay>> executeNode(@Parameter(name = "workflowInstanceId") @PathVariable Long workflowInstanceId,
                                                                           @Parameter(name = "nodeKey") @PathVariable String nodeKey,
                                                                           @Parameter(name = "inputs") @RequestBody(required = false) List<Map<String, Object>> inputs) {
        if (inputs == null) {
            inputs = new ArrayList<>();
        }
        return success(FlowNodeDebuggerBuilder.build("nodeDebugger").execute(workflowInstanceId, nodeKey, inputs));
    }


    @OperationLog(operateType = OperateType.EXECUTE, idParamPosition = 0)
    @PostMapping("execute/workflow-instance/{workflowInstanceId}/node-instance/output")
    @Operation(summary = "node execution output display")
    @ResourceItemOperation(key = "workflow-builder-execute")
    public InnospotResponse<Map<String, NodeExecutionDisplay>> nodeExecutionOutput(@Parameter(name = "workflowInstanceId") @PathVariable Long workflowInstanceId,
                                                                                   @Parameter(name = "nodeKeys") @RequestBody(required = false) List<String> nodeKeys) {
        nodeKeys = new ArrayList<>();
        return success(FlowNodeDebuggerBuilder.build("nodeDebugger").readNodeExecutions(workflowInstanceId, nodeKeys));
    }

    @GetMapping("current/{workflowInstanceId}")
    @Operation(summary = "current workflow execution")
    public InnospotResponse<FlowExecutionBase> currentFlowExecution(@PathVariable Long workflowInstanceId) {
        return success(FlowNodeDebuggerBuilder.build("nodeDebugger").currentExecuting(workflowInstanceId));
    }

    @OperationLog(operateType = OperateType.UPDATE, idParamPosition = 0)
    @PostMapping("stop/flow-execution/{workflowExecutionId}")
    @Operation(summary = "stop workflow execution")
    @ResourceItemOperation(key = "workflow-builder-execute")
    public InnospotResponse<FlowExecutionBase> currentFlowExecution(@PathVariable String workflowExecutionId) {
        return success(FlowNodeDebuggerBuilder.build("nodeDebugger").stop(workflowExecutionId));
    }


}
