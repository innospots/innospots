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

package io.innospots.libra.kernel.module.menu.listener;

import io.innospots.libra.kernel.module.menu.model.MenuDelEvent;
import io.innospots.libra.kernel.module.system.operator.RoleResourceOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * MenuDelListener
 *
 * @author chenc
 * @date 2022/12/05
 */
@Slf4j
@Component
public class MenuDelListener {

    private final RoleResourceOperator roleResourceOperator;

    public MenuDelListener(RoleResourceOperator roleResourceOperator) {
        this.roleResourceOperator = roleResourceOperator;
    }

    @EventListener(value = MenuDelEvent.class)
    public void handleEvent(MenuDelEvent menuDelEvent) {
        if (CollectionUtils.isEmpty(menuDelEvent.getItemKeys())) {
            return;
        }
        roleResourceOperator.deleteRoleResourceByItemKeys(menuDelEvent.getItemKeys());
    }
}