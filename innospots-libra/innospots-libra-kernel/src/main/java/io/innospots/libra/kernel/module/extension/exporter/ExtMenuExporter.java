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

package io.innospots.libra.kernel.module.extension.exporter;

import io.innospots.libra.base.menu.DynamicMenuItemExporter;
import io.innospots.libra.base.menu.ResourceItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2021/12/19
 */
@Component
public class ExtMenuExporter {

    private Map<String, DynamicMenuItemExporter> exporters = new HashMap<>();

    public static final Pattern MENU_PATTERN = Pattern.compile("(.+)([\\d]+)");

    public ExtMenuExporter(List<DynamicMenuItemExporter> menuItemExporters) {
        for (DynamicMenuItemExporter menuItemExporter : menuItemExporters) {
            exporters.put(menuItemExporter.parentMenuKey(), menuItemExporter);
        }
    }

    public List<ResourceItem> listMenuItems(String parentKey) {
        String keyId = null;
        DynamicMenuItemExporter itemExporter;
        if (StringUtils.contains(parentKey, "_")) {
            String itemKey = parentKey.split("_")[0];
            keyId = parentKey.split("_")[1];
            itemExporter = exporters.get(itemKey);

        } else {
            itemExporter = exporters.get(parentKey);
        }

        if (itemExporter == null) {
            return Collections.emptyList();
        }

        return itemExporter.export(keyId);
    }
}