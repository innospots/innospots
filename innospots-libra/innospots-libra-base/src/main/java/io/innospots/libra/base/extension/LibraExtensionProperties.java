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

package io.innospots.libra.base.extension;

import io.innospots.libra.base.menu.ResourceItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Smars
 * @date 2021/12/2
 */
@Getter
@Setter
public class LibraExtensionProperties extends LibraExtensionInformation {

    protected List<ResourceItem> modules;

    /**
     * File name in the publishing package
     */
    protected List<String> zipFileNames;

    @Override
    public void fillI18n() {
        super.fillI18n();
        List<ResourceItem> resourceItems = new ArrayList<>();
        for (ResourceItem module : modules) {
            resourceItems.add(module.cloneI18nItem());
        }
        this.modules = resourceItems;
    }
}
