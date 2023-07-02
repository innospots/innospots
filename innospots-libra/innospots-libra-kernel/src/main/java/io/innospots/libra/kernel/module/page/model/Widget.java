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

package io.innospots.libra.kernel.module.page.model;

import io.innospots.base.constant.Constants;
import io.innospots.base.model.BaseModelInfo;
import io.innospots.libra.kernel.module.page.enums.WidgetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@Schema(title = "widget")
public class Widget extends BaseModelInfo {

    @Schema(title = "widget id")
    private Integer id;

    @Schema(title = "widget parent id")
    private Integer parentId;

    @Schema(title = "page id")
    private Integer dashboardId;

    @Schema(title = "widget key")
    private String widgetKey;

    @Schema(title = "name")
    @Size(max = 32, message = "name length max 32")
    @Pattern(regexp = Constants.NAME_REGEX, message = "name, only supports: contains a maximum of 32 characters, including letters, digits, and underscores (_), and chinese.")
    private String name;

    @Schema(title = "Widget type")
    private WidgetType type;

    @Schema(title = "description")
    private String description;

    @Schema(title = "config")
    private String config;

    @Schema(title = "dataset ids")
    private List<String> viewIds;

    private List<RelationWidget> relations;
}
