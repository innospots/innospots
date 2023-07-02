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

package io.innospots.libra.kernel.module.menu.model;

import io.innospots.libra.base.menu.BaseItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static io.innospots.libra.base.menu.OptElement.UriMethod;

/**
 * menu resource item, include menu, api, button and icon.
 *
 * @author Smars
 * @date 2021/7/18
 */
@Getter
@Setter
@Schema(title = "menu resource item")
public class MenuResourceItem extends BaseItem {


    @Schema(title = "primary id")
    private Integer resourceId;

    @Schema(title = "parent menu item resource id, primary id, the item is root menu, if parentId is zero")
    private Integer parentId;

    @Schema(title = "uri operation method")
    private UriMethod method;

    @Schema(title = "show in the menu if the value is true")
    private boolean showMenu;

    @Schema(title = "whether valid")
    private boolean status;

    @Schema(title = "sort value")
    private Integer orders;

    @Schema(title = "the name of the application")
    private String appName;

    @Schema(title = "the key of the menu")
    private String parentItemKeys;

    @Schema(title = "sub menu item")
    private List<MenuResourceItem> subItems;

//    @Schema(title = "the operations in this menu link page")
//    private List<MenuResourceItem> operations;


}
