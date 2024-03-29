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

import io.innospots.base.enums.DataStatus;
import io.innospots.base.model.PageBody;
import io.innospots.base.model.response.InnospotResponse;
import io.innospots.libra.base.controller.BaseController;
import io.innospots.libra.base.log.OperateType;
import io.innospots.libra.base.log.OperationLog;
import io.innospots.libra.base.menu.ModuleMenu;
import io.innospots.libra.base.menu.ResourceItemOperation;
import io.innospots.libra.base.model.QueryRequest;
import io.innospots.workflow.console.operator.execution.ExecutionManagerOperator;
import io.innospots.workflow.console.operator.instance.WorkflowBuilderOperator;
import io.innospots.workflow.console.operator.instance.WorkflowInstanceOperator;
import io.innospots.workflow.core.flow.WorkflowInfo;
import io.innospots.workflow.core.flow.instance.WorkflowInstance;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.innospots.base.model.response.InnospotResponse.success;
import static io.innospots.libra.base.controller.BaseController.PATH_ROOT_ADMIN;
import static io.innospots.libra.base.menu.ItemType.BUTTON;

/**
 * @author castor_ling
 * @date 2021-02-12
 */
@RestController
@RequestMapping(PATH_ROOT_ADMIN + "workflow/instance")
@ModuleMenu(menuKey = "workflow-management")
@Tag(name = "Workflow Instance")
public class WorkflowInstanceController extends BaseController {

    private final WorkflowInstanceOperator workflowInstanceOperator;

    private final WorkflowBuilderOperator workflowBuilderOperator;

    private final ExecutionManagerOperator executionManagerOperator;

    public WorkflowInstanceController(WorkflowInstanceOperator workflowInstanceOperator,
                                      WorkflowBuilderOperator workflowBuilderOperator,
                                      ExecutionManagerOperator executionManagerOperator) {
        this.workflowInstanceOperator = workflowInstanceOperator;
        this.workflowBuilderOperator = workflowBuilderOperator;
        this.executionManagerOperator = executionManagerOperator;
    }

    /**
     * create workflow
     *
     * @param workflow
     * @return
     */
    @PostMapping
    @ResourceItemOperation(type = BUTTON, icon = "create", name = "${workflow.main.button.add}")
    @OperationLog(operateType = OperateType.CREATE, primaryField = "workflowInstanceId")
    @Operation(summary = "create workflow")
    public InnospotResponse<WorkflowInstance> createWorkflow(@Parameter(name = "workflow") @Valid @RequestBody WorkflowInfo workflow) {
        WorkflowInstance workflowInstance = workflowInstanceOperator.createWorkflow(workflow);
        return success(workflowInstance);
    }

    /**
     * update workflow
     *
     * @param workflow
     * @return
     */
    @PutMapping
    @OperationLog(idParamPosition = 0, primaryField = "workflowInstanceId", operateType = OperateType.UPDATE)
    @ResourceItemOperation(type = BUTTON, icon = "update", name = "${common.button.save}")
    @Operation(summary = "update workflow")
    public InnospotResponse<Boolean> updateWorkflow(@Parameter(name = "workflow") @Valid @RequestBody WorkflowInstance workflow) {
        Boolean updateInfo = workflowInstanceOperator.updateWorkflow(workflow);
        return success(updateInfo);
    }

    /**
     * remove workflow to recycle bin
     *
     * @param workflowInstanceId
     * @return
     */
    @PutMapping("{workflowInstanceId}/recycle")
    @OperationLog(idParamPosition = 0, operateType = OperateType.RECYCLE)
    @Operation(summary = "remove workflow to recycle bin", description = "remove workflow to recycle bin")
    @ResourceItemOperation(type = BUTTON, icon = "delete", name = "${common.tooltip.recycle_bin}")
    public InnospotResponse<Boolean> removeWorkflowToRecycle(@Parameter(required = true, name = "workflowInstanceId") @PathVariable Long workflowInstanceId) {
        Boolean delete = workflowInstanceOperator.removeWorkflowToRecycle(workflowInstanceId);
        return success(delete);
    }

    @DeleteMapping("{workflowInstanceId}")
    @Operation(summary = "delete workflow from system")
    @ResourceItemOperation(type = BUTTON, icon = "delete", name = "${common.button.delete}")
    @OperationLog(idParamPosition = 0, operateType = OperateType.DELETE)
    public InnospotResponse<Boolean> deleteWorkflowInstance(@Parameter(required = true, name = "workflowInstanceId") @PathVariable Long workflowInstanceId) {
        Boolean delete = workflowBuilderOperator.deleteByWorkflowInstanceId(workflowInstanceId);
        if (delete) {
            executionManagerOperator.deleteExecutionLog(workflowInstanceId);
        }
        return success(delete);
    }

    /**
     * Update Strategy Status
     *
     * @return
     */
    @PutMapping("{workflowInstanceId}/{dataStatus}")
    @OperationLog(idParamPosition = 0, operateType = OperateType.UPDATE_STATUS)
    @Operation(summary = "update workflow status", description = "update workflow status")
    @ResourceItemOperation(type = BUTTON, icon = "update", name = "${common.text.status}")
    public InnospotResponse<Boolean> updateDataStatus(@Parameter(required = true, name = "workflowInstanceId") @PathVariable Long workflowInstanceId,
                                                      @Parameter(required = true, name = "dataStatus") @PathVariable DataStatus dataStatus) {
        Boolean update = workflowInstanceOperator.updateWorkflowStatus(workflowInstanceId, dataStatus);
        return success(update);
    }

    /**
     * view strategy
     *
     * @param workflowInstanceId
     * @return
     */
    @GetMapping("{workflowInstanceId}")
    @Operation(summary = "get workflowInfo")
    public InnospotResponse<WorkflowInstance> getWorkflowInstance(@Parameter(required = true, name = "workflowInstanceId") @PathVariable Long workflowInstanceId) {
        WorkflowInstance info = workflowInstanceOperator.getWorkflowInstance(workflowInstanceId);
        return success(info);
    }

    /**
     * page workflow
     *
     * @return
     */
    @GetMapping("page")
    @Operation(summary = "page workflows")
    public InnospotResponse<PageBody<WorkflowInstance>> pageWorkflows(QueryRequest request) {
        PageBody<WorkflowInstance> page = workflowInstanceOperator.pageWorkflows(request);
        return success(page);
    }

}