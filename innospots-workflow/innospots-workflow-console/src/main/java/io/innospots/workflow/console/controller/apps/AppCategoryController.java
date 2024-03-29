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

import io.innospots.base.model.response.InnospotResponse;
import io.innospots.libra.base.controller.BaseController;
import io.innospots.libra.base.menu.ModuleMenu;
import io.innospots.libra.base.menu.ResourceItemOperation;
import io.innospots.workflow.console.mapper.apps.AppNodeGroupConvertMapper;
import io.innospots.workflow.console.operator.apps.AppFlowTemplateOperator;
import io.innospots.workflow.core.node.apps.AppFlowTemplate;
import io.innospots.workflow.core.node.apps.AppNodeGroupBaseInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.innospots.base.model.response.InnospotResponse.success;
import static io.innospots.libra.base.controller.BaseController.PATH_ROOT_ADMIN;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/3
 */
@RestController
@RequestMapping(PATH_ROOT_ADMIN + "apps/category")
@ModuleMenu(menuKey = "application")
@Tag(name = "App Node Category")
public class AppCategoryController extends BaseController {

    private AppFlowTemplateOperator appFlowTemplateOperator;

    public AppCategoryController(AppFlowTemplateOperator appFlowTemplateOperator) {
        this.appFlowTemplateOperator = appFlowTemplateOperator;
    }

    @GetMapping("list")
    @Operation(summary = "apps category list")
    public InnospotResponse<List<AppNodeGroupBaseInfo>> listCategories(
            @RequestParam(required = false, defaultValue = "true") boolean onlyConnector) {
        return InnospotResponse.success(
                AppNodeGroupConvertMapper.INSTANCE.modelToBaseList(
                        appFlowTemplateOperator.getTemplate(1, true, onlyConnector,false)
                                .getAppNodeGroups())
        );
    }

    @GetMapping("def-list/{templateCode}")
    @Operation(summary = "list applications node definition")
    public InnospotResponse<AppFlowTemplate> getTemplate(@PathVariable String templateCode) {
        return success(appFlowTemplateOperator.getTemplate(1, true));
    }

    @GetMapping("def-list")
    @Operation(summary = "list applications node definition")
    public InnospotResponse<AppFlowTemplate> getTemplate() {
        return success(appFlowTemplateOperator.getTemplate(1, true));
    }
}
