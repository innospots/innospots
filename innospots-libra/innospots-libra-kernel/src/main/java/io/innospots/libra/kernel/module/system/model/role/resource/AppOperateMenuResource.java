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

package io.innospots.libra.kernel.module.system.model.role.resource;

import io.innospots.base.json.annotation.I18n;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Wren
 */
@Getter
@Setter
@Schema(title = "operate app menu resource")
public class AppOperateMenuResource {

    private String appKey;
    @I18n
    private String appName;

    private List<OperateMenuResourceItem> operateMenuResourceItems;

    public AppOperateMenuResource() {

    }

    public AppOperateMenuResource(String appKey, String appName, List<OperateMenuResourceItem> operateMenuResourceItems) {
        this.appKey = appKey;
        this.appName = appName;
        this.operateMenuResourceItems = operateMenuResourceItems;
    }
}