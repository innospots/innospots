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

package io.innospots.workflow.console.mapper.apps;

import io.innospots.base.mapper.BaseConvertMapper;
import io.innospots.workflow.console.entity.apps.AppNodeGroupEntity;
import io.innospots.workflow.core.node.AppInfo;
import io.innospots.workflow.core.node.apps.AppNodeGroup;
import io.innospots.workflow.core.node.apps.AppNodeGroupBaseInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Wren
 * @date 2021-12-25 17:49:00
 */
@Mapper
public interface AppNodeGroupConvertMapper extends BaseConvertMapper {

    AppNodeGroupConvertMapper INSTANCE = Mappers.getMapper(AppNodeGroupConvertMapper.class);

    /**
     * FlowNodeGroupEntity to NodeGroup
     *
     * @param entity
     * @return NodeGroup
     */
    AppNodeGroup entityToModel(AppNodeGroupEntity entity);

    /**
     * NodeGroup to FlowNodeGroupEntity
     *
     * @param model
     * @return FlowNodeGroupEntity
     */
    AppNodeGroupEntity modelToEntity(AppNodeGroup model);

    default List<AppNodeGroupBaseInfo> modelToBaseList(List<AppNodeGroup> appNodeGroups) {
        if (appNodeGroups == null) {
            return Collections.emptyList();
        }
        List<AppNodeGroupBaseInfo> groupBaseInfos = new ArrayList<>();
        List<AppInfo> nodes = new ArrayList<>();
        AppNodeGroupBaseInfo allGroupInfo = null;
        for (AppNodeGroup appNodeGroup : appNodeGroups) {
            AppNodeGroupBaseInfo nodeGroupBaseInfo = modelToBase(appNodeGroup);
            if ("all".equals(appNodeGroup.getCode())) {
                allGroupInfo = nodeGroupBaseInfo;
            }
            groupBaseInfos.add(nodeGroupBaseInfo);
            if (nodeGroupBaseInfo.getNodes() != null) {
                nodes.addAll(nodeGroupBaseInfo.getNodes());
            }
        }//end for
        if (allGroupInfo != null) {
            allGroupInfo.setNodes(nodes);
        }
        return groupBaseInfos;
    }

    default AppNodeGroupBaseInfo modelToBase(AppNodeGroup nodeGroup) {
        if (nodeGroup == null) {
            return null;
        }
        AppNodeGroupBaseInfo baseInfo = new AppNodeGroupBaseInfo();
        baseInfo.setNodeGroupId(nodeGroup.getNodeGroupId());
        baseInfo.setCode(nodeGroup.getCode());
        baseInfo.setName(nodeGroup.getName());
        baseInfo.setPosition(nodeGroup.getPosition());
        if (CollectionUtils.isNotEmpty(nodeGroup.getNodes())) {
            baseInfo.setNodes(AppNodeDefinitionConvertMapper.INSTANCE.modelToInfoList(nodeGroup.getNodes()));
        }
        return baseInfo;
    }

    /**
     * NodeDefinition fill to FlowNodeDefinitionEntity
     *
     * @param model
     * @param entity
     */
    void modelToEntity(AppNodeGroup model, @MappingTarget AppNodeGroupEntity entity);

    /**
     * FlowNodeGroupEntity Of list to NodeGroup Of list
     *
     * @param entityList
     * @return List<NodeGroup>
     */
    List<AppNodeGroup> entityToModelList(List<AppNodeGroupEntity> entityList);
}
