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

package io.innospots.libra.kernel.module.system.listener;

import io.innospots.libra.base.event.AvatarRemoveEvent;
import io.innospots.libra.base.event.NewAvatarEvent;
import io.innospots.libra.kernel.module.system.operator.AvatarResourceOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AvatarRemoveListener {
    private final AvatarResourceOperator avatarResourceOperator;

    public AvatarRemoveListener(AvatarResourceOperator avatarResourceOperator) {
        this.avatarResourceOperator = avatarResourceOperator;
    }

    @EventListener(value = AvatarRemoveEvent.class)
    public void handleEvent(AvatarRemoveEvent avatarRemoveEvent) {
        avatarResourceOperator.deleteResource(avatarRemoveEvent.getResourceId(),avatarRemoveEvent.getImageType());
    }
}