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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/1/30
 */
@Getter
@Setter
@Schema(title = "Role Resource Request")
public class RoleResourceRequest {

    /**
     * 按最终权限状态提交，统一菜单权限和操作权限的数据结构
     */
    @Deprecated
    @Schema(title = "add menu resource item, key is menu resource item primary id, the values is the list that contain role id will be added")
    private Map<Integer, List<Integer>> newItems;

    /** 按最终权限状态提交，统一菜单权限和操作权限的数据结构 */
//    @Deprecated
//    @Schema(title = "remove menu resource item, the elements is the list that contain role resource id will be removed")
//    private List<Integer> removeItems;

    /**
     * 按最终权限状态提交，统一菜单权限和操作权限的数据结构
     */
//    @Deprecated
//    @Schema(title = "add menu resource item, key is role id, the values is the list that contain item key will be added")
//    private Map<Integer, List<String>> newOperateItems;

    @Schema(title = "update role resource type")
    private RoleResourceInfo.RoleResourceType roleResourceType;

    @Schema(title = "current menu or operate authorities, the key is itemKey and the value is the collection of roleId")
    private Map<String, List<Integer>> menuItemRoles;

}