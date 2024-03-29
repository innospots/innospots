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

package io.innospots.workflow.console.entity.apps;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.innospots.libra.base.entity.PBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * node group and node relation table
 *
 * @author Raydian
 * @date 2020/12/14
 */
@Getter
@Setter
@Entity
@TableName(value = AppNodeGroupNodeEntity.TABLE_NAME)
@Table(name = AppNodeGroupNodeEntity.TABLE_NAME, indexes = {
        @Index(columnList = "flowTplId", name = "idx_flow_tpl_node")
})
public class AppNodeGroupNodeEntity extends PBaseEntity {

    public static final String TABLE_NAME = "app_node_group_node";

    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer nodeGroupNodeId;
    /**
     * flow template id
     */
    @Column
    private Integer flowTplId;
    /**
     * node group id
     */
    @Column
    private Integer nodeGroupId;

    /**
     * node id
     */
    @Column
    private Integer nodeId;

    public AppNodeGroupNodeEntity() {
    }

    public AppNodeGroupNodeEntity(Integer flowTplId, Integer nodeGroupId, Integer nodeId) {
        this.flowTplId = flowTplId;
        this.nodeGroupId = nodeGroupId;
        this.nodeId = nodeId;
    }
}
