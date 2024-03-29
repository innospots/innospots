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

package io.innospots.workflow.console.listener;

import io.innospots.workflow.console.entity.instance.NodeInstanceEntity;
import io.innospots.workflow.console.events.InstanceUpdateEvent;
import io.innospots.workflow.console.operator.apps.AppNodeDefinitionOperator;
import io.innospots.workflow.console.operator.instance.NodeInstanceOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jegy
 * @version 1.0.0
 * @date 2023/5/16
 */
@Slf4j
@Component
public class AppUseListener {

    public static final String APP_USE_ADD = "ADD";

    public static final String APP_USE_DELETE = "DELETE";

    private final NodeInstanceOperator nodeInstanceOperator;

    private final AppNodeDefinitionOperator nodeDefinitionOperator;

    public AppUseListener(NodeInstanceOperator nodeInstanceOperator, AppNodeDefinitionOperator nodeDefinitionOperator) {
        this.nodeInstanceOperator = nodeInstanceOperator;
        this.nodeDefinitionOperator = nodeDefinitionOperator;
    }

    @EventListener(InstanceUpdateEvent.class)
    public void handleEvent(InstanceUpdateEvent event) {
        Long workflowInstanceId = (Long) event.getSource();
        // 先distinct 获取flow_instance_node表中的 应用节点id集合
        List<NodeInstanceEntity> nodeInstanceEntities = nodeInstanceOperator.getInstanceNodeByFlowInstanceId(workflowInstanceId);
        List<Integer> nodeDefinitionIds = nodeInstanceEntities.stream().map(NodeInstanceEntity::getNodeDefinitionId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(nodeDefinitionIds)) {
            return;
        }
        // 更新节点应用定义表，所有的不再此应用节点集合中的应用节点，not in and used = true， used设置为false
        // 更新节点应用定义表，所有在此应用节点集合并且used为false的节点，used更新为true
        if (APP_USE_ADD.equalsIgnoreCase(event.getAppUseType())) {
            nodeDefinitionOperator.updateAppUsed(nodeDefinitionIds, Boolean.TRUE);

        } else if (APP_USE_DELETE.equalsIgnoreCase(event.getAppUseType())) {
            nodeDefinitionOperator.updateAppUsed(nodeDefinitionIds, Boolean.FALSE);
        }
    }
}
