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

package io.innospots.workflow.console.controller.apps;

import io.innospots.base.enums.DataStatus;
import io.innospots.base.model.PageBody;
import io.innospots.base.model.response.InnospotResponse;
import io.innospots.libra.base.controller.BaseController;
import io.innospots.libra.base.log.OperateType;
import io.innospots.libra.base.log.OperationLog;
import io.innospots.libra.base.menu.ResourceItemOperation;
import io.innospots.workflow.console.operator.apps.AppFlowTemplateOperator;
import io.innospots.workflow.console.operator.apps.AppNodeGroupOperator;
import io.innospots.workflow.core.node.apps.AppFlowTemplate;
import io.innospots.workflow.core.node.apps.AppFlowTemplateBase;
import io.innospots.workflow.core.node.apps.AppNodeGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.innospots.base.model.response.InnospotResponse.success;
import static io.innospots.libra.base.menu.ItemType.BUTTON;


/**
 * 应用模板，前端不单独维护，系统固定默认模板，用于其他应用扩展使用
 *
 * @author Smars
 * @date 2021/4/19
 */
//@RestController
//@RequestMapping(PATH_ROOT_ADMIN + "apps/workflow-template")
//@ModuleMenu(menuKey = "app-flow-template")
//@Tag(name = "Workflow Template")
public class AppFlowTemplateController extends BaseController {

    private AppFlowTemplateOperator appFlowTemplateOperator;
    private AppNodeGroupOperator appNodeGroupOperator;


    public AppFlowTemplateController(AppFlowTemplateOperator appFlowTemplateOperator, AppNodeGroupOperator appNodeGroupOperator) {
        this.appFlowTemplateOperator = appFlowTemplateOperator;
        this.appNodeGroupOperator = appNodeGroupOperator;
    }


    /**
     * create flow template
     *
     * @return WorkflowTemplate
     */
    @OperationLog(operateType = OperateType.CREATE, primaryField = "flowTplId")
    @PostMapping
    @Operation(summary = "create flow template")
    @ResourceItemOperation(type = BUTTON, icon = "create", name = "${common.button.create}")
    public InnospotResponse<AppFlowTemplateBase> createTemplate(@RequestBody AppFlowTemplateBase appFlowTemplateBase) {

        return success(appFlowTemplateOperator.createTemplate(appFlowTemplateBase));
    }

    /**
     * modify flow template
     *
     * @return Boolean
     */
    @OperationLog(operateType = OperateType.UPDATE, primaryField = "flowTplId")
    @PutMapping("")
    @Operation(summary = "update flow template")
    @ResourceItemOperation(type = BUTTON, icon = "update", name = "${common.button.save}")
    public InnospotResponse<Boolean> updateTemplate(@RequestBody AppFlowTemplateBase appFlowTemplateBase) {

        return success(appFlowTemplateOperator.updateTemplate(appFlowTemplateBase));
    }

    /**
     * modify flow template status
     *
     * @param flowTplId template id
     * @param status    template status  {@link DataStatus}
     * @return Boolean
     */
    @OperationLog(operateType = OperateType.UPDATE_STATUS, idParamPosition = 0)
    @PutMapping("{flowTplId}/{status}")
    @Operation(summary = "modify flow template status")
    @ResourceItemOperation(type = BUTTON, icon = "update", name = "${common.button.status}")
    public InnospotResponse<Boolean> updateStatus(@Parameter(name = "flowTplId", required = true) @PathVariable Integer flowTplId,
                                                  @Parameter(name = "status", required = true) @PathVariable DataStatus status) {

        return success(appFlowTemplateOperator.updateStatus(flowTplId, status));
    }

    /**
     * get flow template info
     *
     * @param flowTplId    template id
     * @param includeNodes include nodes flag
     * @return WorkflowTemplate
     */
    @GetMapping("{flowTplId}")
    @Operation(summary = "get flow template")
    @ResourceItemOperation(parent = "workflow-management")
    public InnospotResponse<AppFlowTemplate> getTemplate(@Parameter(name = "flowTplId", required = true) @PathVariable Integer flowTplId,
                                                         @Parameter(name = "includeNodes", required = false) @RequestParam(required = false) boolean includeNodes) {

        return success(appFlowTemplateOperator.getTemplate(flowTplId, includeNodes));
    }


    /**
     * get flow template page according to status and name
     *
     * @param name   template name
     * @param status template status
     * @param page   page
     * @param size   size
     * @return Page<WorkflowTemplate>
     */
    @GetMapping("page")
    @Operation(summary = "get flow template page according to status and name")
    public InnospotResponse<PageBody<AppFlowTemplateBase>> pageTemplates(@Parameter(name = "name") @RequestParam(required = false) String name,
                                                                         @Parameter(name = "status") @RequestParam(required = false) DataStatus status,
                                                                         @Parameter(name = "page") @RequestParam(defaultValue = "1") Integer page,
                                                                         @Parameter(name = "size") @RequestParam(defaultValue = "50") Integer size) {

        return success(appFlowTemplateOperator.pageTemplates(name, status, page, size));
    }

    @GetMapping("list/online")
    @Operation(summary = "list online flow templates")
    public InnospotResponse<List<AppFlowTemplateBase>> listTemplates() {
        return success(appFlowTemplateOperator.listOnlineFlowTemplates());
    }

    @OperationLog(operateType = OperateType.DELETE, idParamPosition = 0)
    @DeleteMapping("{flowTplId}")
    @Operation(summary = "delete template, groups and the node group relation at same time")
    @ResourceItemOperation(type = BUTTON, icon = "delete", name = "${common.button.delete}")
    public InnospotResponse<Boolean> deleteTemplate(@Parameter(name = "flowTplId") @PathVariable Integer flowTplId) {
        return success(appFlowTemplateOperator.deleteTemplate(flowTplId));
    }


    /**
     * create node group
     *
     * @param flowTplId template id
     * @param name      node group name
     * @param code      node group code
     * @param position  node group position default set Integer.MAX_VALUE
     * @return NodeGroup
     */
    @OperationLog(operateType = OperateType.CREATE, idParamPosition = 0)
    @PostMapping("{flowTplId}/node-group")
    @Operation(summary = "create node group")
    @ResourceItemOperation(type = BUTTON, icon = "create", name = "${page.category.add.title}")
    public InnospotResponse<AppNodeGroup> createNodeGroup(@Parameter(name = "flowTplId", required = true) @PathVariable Integer flowTplId,
                                                          @Parameter(name = "name", required = true) @RequestParam String name,
                                                          @Parameter(name = "code", required = true) @RequestParam String code,
                                                          @Parameter(name = "position", required = false) @RequestParam(required = false) Integer position) {
        return success(appNodeGroupOperator.createNodeGroup(flowTplId, name, code, position));
    }

    /**
     * modify node group
     *
     * @param flowTplId   template id
     * @param nodeGroupId node group id
     * @param name        node group name
     * @param code        node group code
     * @param position    node group position
     * @return Boolean
     */
    @OperationLog(operateType = OperateType.UPDATE, idParamPosition = 0)
    @PutMapping("{flowTplId}/node-group/{nodeGroupId}")
    @Operation(summary = "update node group")
    @ResourceItemOperation(type = BUTTON, icon = "update", name = "${page.category.edit.title}")
    public InnospotResponse<Boolean> updateNodeGroup(@Parameter(name = "flowTplId", required = true) @PathVariable Integer flowTplId,
                                                     @Parameter(name = "nodeGroupId", required = true) @PathVariable Integer nodeGroupId,
                                                     @Parameter(name = "name", required = true) @RequestParam String name,
                                                     @Parameter(name = "code", required = true) @RequestParam String code,
                                                     @Parameter(name = "position", required = true) @RequestParam Integer position) {
        return success(appNodeGroupOperator.updateNodeGroup(flowTplId, nodeGroupId, name, code, position));
    }

    /**
     * remove node group
     *
     * @param nodeGroupId node group id
     * @return Boolean
     */
    @OperationLog(operateType = OperateType.DELETE, idParamPosition = 0)
    @DeleteMapping("node-group/{nodeGroupId}")
    @Operation(summary = "remove node group")
    @ResourceItemOperation(type = BUTTON, icon = "delete", name = "${common.category.delete.title}")
    public InnospotResponse<Boolean> removeNodeGroup(@Parameter(name = "nodeGroupId", required = true) @PathVariable Integer nodeGroupId) {
        return success(appNodeGroupOperator.removeNodeGroup(nodeGroupId));
    }


    /**
     * save or modify NodeGroup and Node relation
     *
     * @param flowTplId
     * @param nodeGroupId
     * @param nodeIds     node ids type is list for example:[1,3,2]
     * @return
     */
    @OperationLog(operateType = OperateType.UPDATE, idParamPosition = 0)
    @PostMapping("{flowTplId}/node-group/{nodeGroupId}/node-ids")
    @Operation(summary = "save or modify NodeGroup and Node relation")
    @ResourceItemOperation(type = BUTTON, icon = "update", name = "${workflow.builder.params.title}")
    public InnospotResponse<Boolean> saveOrUpdateNodeGroupNode(@Parameter(name = "flowTplId", required = true) @PathVariable Integer flowTplId,
                                                               @Parameter(name = "nodeGroupId", required = true) @PathVariable Integer nodeGroupId,
                                                               @Parameter(name = "nodeIds", required = true) @RequestParam List<Integer> nodeIds) {
        return success(appNodeGroupOperator.saveOrUpdateNodeGroupNode(flowTplId, nodeGroupId, nodeIds));
    }

}
