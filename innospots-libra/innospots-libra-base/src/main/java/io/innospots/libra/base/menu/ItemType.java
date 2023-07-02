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

package io.innospots.libra.base.menu;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author Raydian
 * @date 2021/1/17
 */
@Getter
public enum ItemType {

    /**
     *
     */
    MENU("menu", "menu group"),
    CATEGORY("category", "category group"),
    PAGE("page", "libra page"),
    API("api", "api, not show in the page"),
    ICON("icon", "icon link"),
    LINK("link", "text link"),
    BUTTON("button", "button link");

    private final String name;

    private final String msg;

    ItemType(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }

    public static ItemType getItemTypeByName(String name) {
        return Arrays.stream(ItemType.values()).filter(f -> f.name.equals(name)).findFirst().orElse(null);
    }
}