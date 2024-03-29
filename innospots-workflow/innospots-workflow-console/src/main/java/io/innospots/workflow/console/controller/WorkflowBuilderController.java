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
import io.innospots.libra.base.controller.BaseController;
import io.innospots.libra.base.log.OperateType;
import io.innospots.libra.base.log.OperationLog;
import io.innospots.libra.base.menu.ModuleMenu;
import io.innospots.libra.base.menu.ResourceItemOperation;
import io.innospots.workflow.console.operator.instance.WorkflowBuilderOperator;
import io.innospots.workflow.core.flow.WorkflowBaseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.innospots.base.model.response.InnospotResponse.success;
import static io.innospots.libra.base.controller.BaseController.PATH_ROOT_ADMIN;
import static io.innospots.libra.base.menu.ItemType.BUTTON;

/**
 * @author Smars
 * @date 2021/4/19
 */
@RestController
@RequestMapping(PATH_ROOT_ADMIN + "workflow/builder")
@ModuleMenu(menuKey = "workflow-management", uri = "/workflow/builder")
@Tag(name = "Workflow Builder")
public class WorkflowBuilderController extends BaseController {


    private WorkflowBuilderOperator workFlowBuilderOperator;

    public WorkflowBuilderController(WorkflowBuilderOperator workFlowBuilderOperator) {
        this.workFlowBuilderOperator = workFlowBuilderOperator;
    }

    /**
     * get flow instance by revision
     *
     * @param workflowInstanceId
     * @param revision
     * @param includeNodes
     * @return
     */
    @GetMapping("{workflowInstanceId}/revision/{revision}")
    @ResourceItemOperation(type = BUTTON, icon = "edit", name = "${common.button.edit}")
    @Operation(summary = "get flow instance by revision")
    public InnospotResponse<WorkflowBaseBody> getFlowInstanceByRevision(@PathVariable Long workflowInstanceId,
                                                                        @PathVariable Integer revision,
                                                                        @RequestParam(defaultValue = "true") Boolean includeNodes) {
        WorkflowBaseBody workflowBaseBody;
        if (revision == null || revision == 0) {
            workflowBaseBody = workFlowBuilderOperator.getFlowInstanceDraftOrCache(workflowInstanceId);
        } else {
            workflowBaseBody = workFlowBuilderOperator.getWorkflowBody(workflowInstanceId, revision, includeNodes);
        }
        return success(workflowBaseBody);
    }

    /**
     * get flow instance by draft
     *
     * @param workflowInstanceId
     * @param includeNodes
     * @return
     */
    @GetMapping("draft/{workflowInstanceId}")
    @Operation(summary = "get flow instance by draft")
    public InnospotResponse<WorkflowBaseBody> getFlowInstanceByDraft(@PathVariable Long workflowInstanceId,
                                                                     @RequestParam(defaultValue = "true") Boolean includeNodes) {
        return success(workFlowBuilderOperator.getFlowInstanceDraftOrCache(workflowInstanceId));
    }


    /**
     * get flow instance by lasted
     *
     * @param workflowInstanceId
     * @param includeNodes
     * @return
     */
    @GetMapping("lasted/{workflowInstanceId}")
    @Operation(summary = "get flow instance by lasted")
    public InnospotResponse<WorkflowBaseBody> getFlowInstanceByLasted(@PathVariable Long workflowInstanceId,
                                                                      @RequestParam(defaultValue = "true") Boolean includeNodes) {
        return success(workFlowBuilderOperator.getWorkflowBody(workflowInstanceId, null, includeNodes));
    }


    /**
     * save flow instance to cache
     *
     * @param workflowBaseBody
     * @return
     */
    @PostMapping("cache")
    @Operation(summary = "save flow instance to cache")
    @ResourceItemOperation(key = "workflow-builder-opt")
    public InnospotResponse<Boolean> saveCache(@Validated @RequestBody WorkflowBaseBody workflowBaseBody) {
        return success(workFlowBuilderOperator.saveFlowInstanceToCache(workflowBaseBody));
    }

    /*
    @Deprecated
    @GetMapping("/{workflowInstanceId}/node-key/{nodeKey}/output-field")
    @Operation(summary = "从保存的工作流程实例缓存信息中获取节点的输出字段")
    public InnospotResponse<List<Map<String,Object>>> getNodeOutputFieldOfInstance(@PathVariable Long workflowInstanceId, @PathVariable String nodeKey) {
        //从缓存信息中获取每个节点的outputField列表，
        return success(workFlowBuilderOperator.getNodeOutputFieldOfInstance(workflowInstanceId,nodeKey));
    }
     */


    @GetMapping("/{workflowInstanceId}/node-key/{nodeKey}/input-fields")
    @Operation(summary = "the input data fields, which current select node")
    public InnospotResponse<List<Map<String, Object>>> listNodeInputFields(
            @PathVariable Long workflowInstanceId,
            @PathVariable String nodeKey,
            @RequestParam(required = false) Set<String> sourceNodeKeys
    ) {
        return success(workFlowBuilderOperator.selectNodeInputFields(workflowInstanceId, nodeKey, sourceNodeKeys));
    }


    /**
     * save flow instance draft revision
     *
     * @return
     */
    @OperationLog(operateType = OperateType.UPDATE, primaryField = "workflowInstanceId")
    @PutMapping("draft")
    @Operation(summary = "save flow instance draft revision")
    @ResourceItemOperation(key = "workflow-builder-opt", type = BUTTON, icon = "save", name = "${common.button.save}")
    public InnospotResponse<WorkflowBaseBody> saveDraft(@Validated @RequestBody WorkflowBaseBody workflowBaseBody) {
        return success(workFlowBuilderOperator.saveDraft(workflowBaseBody));
    }


    /**
     * publish flow instance
     *
     * @param workflowInstanceId
     * @return
     */
    @OperationLog(operateType = OperateType.PUBLISH, idParamPosition = 0)
    @PostMapping("publish/{workflowInstanceId}")
    @Operation(summary = "publish flow instance")
    @ResourceItemOperation(type = BUTTON, icon = "save", name = "${common.button.publish}")
    public InnospotResponse<Boolean> publish(@PathVariable Long workflowInstanceId,
                                             @RequestParam(defaultValue = "") String description) {

        return success(workFlowBuilderOperator.publish(workflowInstanceId, description));
    }

}
