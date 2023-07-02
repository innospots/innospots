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

package io.innospots.workflow.core.node;


import io.innospots.base.enums.DataStatus;
import io.innospots.workflow.core.enums.AppPrimitive;
import io.innospots.workflow.core.enums.AppSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


/**
 * @author Raydian
 * @date 2020/11/28
 */
@Setter
@Getter
public class AppInfo {


    protected Integer nodeId;

    @NotNull(message = "app name cannot be empty")
    @Schema(title = "app name")
    protected String name;

    @NotNull(message = "app code cannot be empty")
    @Schema(title = "app code")
    protected String code;

    @NotNull(message = "app primitive type cannot be empty")
    @Schema(title = "app primitive type")
    protected AppPrimitive primitive;

    @NotNull(message = "app group cannot be empty")
    @Schema(title = "app group")
    protected Integer nodeGroupId;

    @Schema(title = "app icon image base64")
    protected String icon;

    @NotNull(message = "app description cannot be empty")
    @Schema(title = "app description")
    protected String description;

    protected Boolean used;

    protected AppSource appSource;

    @Schema(title = "node class name")
    protected String nodeType;

    @Schema(title = "node status, ONLINE or OFFLINE")
    protected DataStatus status;
}
