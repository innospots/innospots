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

package io.innospots.libra.kernel.module.workspace.model;

import io.innospots.base.data.dataset.Dataset;
import io.innospots.libra.base.model.BaseDashboard;
import io.innospots.libra.kernel.module.page.model.Widget;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TODO 当作特殊的page
 *
 * @author Alfred
 * @date 2022/1/30
 */
@Getter
@Setter
@Schema(title = "workspace model")
public class Workspace extends BaseDashboard {

    @Schema(title = "user id")
    private Integer userId;

    @Schema(title = "widget list")
    List<Widget> widgets;

    @Schema(title = "view list")
    List<Dataset> views;

    @Schema(title = "widget delete ids")
    List<Integer> widgetDeleteIds;
}
