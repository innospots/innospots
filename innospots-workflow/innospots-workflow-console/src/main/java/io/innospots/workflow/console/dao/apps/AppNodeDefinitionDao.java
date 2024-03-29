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

package io.innospots.workflow.console.dao.apps;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import io.innospots.base.enums.DataStatus;
import io.innospots.workflow.console.entity.apps.AppNodeDefinitionEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Raydian
 * @date 2021/1/16
 */
public interface AppNodeDefinitionDao extends BaseMapper<AppNodeDefinitionEntity> {

    /**
     * get node definition of flow template
     *
     * @param flowTplId
     * @return
     */
    @Select("select ngn.node_group_id,nd.* from app_node_group_node ngn left join app_node_definition nd " +
            "on ngn.node_id=nd.node_id where ngn.flow_tpl_id=#{flowTplId}")
    List<AppNodeDefinitionEntity> getNodeDefinitionByFlowTplId(Integer flowTplId);


    @Select("select ngn.node_group_id,nd.* from app_node_group_node ngn left join app_node_definition nd " +
            "on ngn.node_id=nd.node_id where ngn.flow_tpl_id=#{flowTplId} and nd.status=#{status}")
    List<AppNodeDefinitionEntity> getNodeDefinitionByFlowTplIdAndStatus(Integer flowTplId, DataStatus status);


    /**
     * 使用@Select注解，将SQL的where条件部分用${ew.customSqlSegment}代替
     */
    @Select("select s.* from app_node_definition s left join app_node_group_node t on s.node_id = t.node_id ${ew.customSqlSegment}")
    IPage<AppNodeDefinitionEntity> selectAppPage(IPage<AppNodeDefinitionEntity> page, @Param(Constants.WRAPPER) Wrapper queryWrapper);
}
