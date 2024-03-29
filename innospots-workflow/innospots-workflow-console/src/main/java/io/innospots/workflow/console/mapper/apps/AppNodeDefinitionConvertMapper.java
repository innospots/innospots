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

import io.innospots.base.json.JSONUtils;
import io.innospots.base.mapper.BaseConvertMapper;
import io.innospots.workflow.console.entity.apps.AppNodeDefinitionEntity;
import io.innospots.workflow.core.node.AppInfo;
import io.innospots.workflow.core.node.apps.AppConnectorConfig;
import io.innospots.workflow.core.node.apps.AppNodeDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * @author Wren
 * @date 2021-12-25 17:49:00
 */
@Mapper
public interface AppNodeDefinitionConvertMapper extends BaseConvertMapper {

    AppNodeDefinitionConvertMapper INSTANCE = Mappers.getMapper(AppNodeDefinitionConvertMapper.class);

    /**
     * FlowNodeDefinitionEntity to NodeDefinition
     *
     * @param entity
     * @return
     */
    AppNodeDefinition entityToModel(AppNodeDefinitionEntity entity);

    /**
     * NodeDefinition to FlowNodeDefinitionEntity
     *
     * @param model
     * @return FlowNodeDefinitionEntity
     */
    AppNodeDefinitionEntity modelToEntity(AppNodeDefinition model);

    AppNodeDefinitionEntity infoToEntity(AppInfo appInfo);

    AppInfo modelToSimple(AppNodeDefinition model);
    AppInfo entityToSimple(AppNodeDefinitionEntity entity);

    List<AppInfo> modelToInfoList(List<AppNodeDefinition> appNodeDefinitionList);

    /**
     * NodeDefinition fill to FlowNodeDefinitionEntity
     *
     * @param model
     * @param entity
     */
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "primitive", ignore = true)
    @Mapping(target = "icon", ignore = true)
    @Mapping(target = "description", ignore = true)
    void modelToEntity(AppNodeDefinition model, @MappingTarget AppNodeDefinitionEntity entity);

    @Mapping(target = "icon", ignore = true)
    void infoToEntity(AppInfo appInfo, @MappingTarget AppNodeDefinitionEntity entity);

    /**
     * FlowNodeDefinitionEntity Of list to NodeDefinition Of list
     *
     * @param entityList
     * @return
     */
    List<AppNodeDefinition> entityToModelList(List<AppNodeDefinitionEntity> entityList);


    default List<AppConnectorConfig> strToConnectorConfigs(String connectorConfig){
        return JSONUtils.toList(connectorConfig,AppConnectorConfig.class);
    }

    default String connectorConfigsToStr(List<AppConnectorConfig> connectorConfigs){
        return JSONUtils.toJsonString(connectorConfigs);
    }
}
