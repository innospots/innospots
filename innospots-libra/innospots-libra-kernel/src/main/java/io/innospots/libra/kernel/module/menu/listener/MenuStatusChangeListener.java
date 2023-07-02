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

import io.innospots.libra.base.event.AppEvent;
import io.innospots.libra.base.extension.ExtensionStatus;
import io.innospots.libra.kernel.module.menu.operator.MenuManagementOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * AppStatusChangeListener
 *
 * @author Wren
 * @date 2022/5/10-21:24
 */
@Slf4j
@Component
public class MenuStatusChangeListener {

    private MenuManagementOperator menuManagementOperator;

    public MenuStatusChangeListener(MenuManagementOperator menuManagementOperator) {
        this.menuManagementOperator = menuManagementOperator;
    }

    /**
     * Application status change triggers application menu status change
     *
     * @param appEvent
     */
    @EventListener(AppEvent.class)
    public void handleEvent(AppEvent appEvent) {
        log.debug("handler event :{}", appEvent);
        try {
            Boolean status = null;
            if (appEvent.getStatus().equals(ExtensionStatus.AVAILABLE)) {
                status = true;
            } else if (appEvent.getStatus().equals(ExtensionStatus.DISABLED)) {
                status = false;
            }
            if (status != null) {
                long result = menuManagementOperator.updateMenuStatusByAppKey(appEvent.getAppKey(), status);
                log.info("handler event :{} update menu status by appKey result:{}", appEvent, result);
            }
        } catch (Exception e) {
            log.error("MenuStatusChangeListener handleEvent AppEvent error ", e);
        }


    }

}
