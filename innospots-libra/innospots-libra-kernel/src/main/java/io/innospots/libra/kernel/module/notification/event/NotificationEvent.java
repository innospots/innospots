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

package io.innospots.libra.kernel.module.notification.event;

import io.innospots.base.json.annotation.I18n;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/8/4
 */
@Getter
@Setter
public class NotificationEvent {

    /**
     * application name
     */
    private String extName;

    private String extKey;

    /**
     * use class name if not define
     */
    private String moduleKey;

    private String moduleName;

    /**
     * use method name if not define
     */
    @I18n
    private String eventName;

    /**
     * use method name which underline named style if not define
     */
    private String eventCode;

    /**
     * condition expression
     */
    private String condition;

    public NotificationEvent(
            String extName, String extKey,
            String moduleName, String moduleKey, String eventName, String eventCode) {
        this.extName = extName;
        this.extKey = extKey;
        this.moduleName = moduleName;
        this.moduleKey = moduleKey;
        this.eventName = eventName;
        this.eventCode = eventCode;
    }

    public NotificationEvent() {
    }
}
