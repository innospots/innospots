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
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.innospots.base.enums.DataStatus;
import io.innospots.libra.base.entity.BaseEntity;
import io.innospots.workflow.core.enums.AppPrimitive;
import io.innospots.workflow.core.node.apps.AppNodeDefinition;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Raydian
 * @date 2020/12/14
 */
@Getter
@Setter
@Entity
@TableName(value = AppNodeDefinitionEntity.TABLE_NAME)
@Table(name = AppNodeDefinitionEntity.TABLE_NAME)
public class AppNodeDefinitionEntity extends BaseEntity {

    public static final String TABLE_NAME = "app_node_definition";

    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer nodeId;

    @Column(length = 32)
    private String name;

    @Column(length = 64)
    private String displayName;

    @Column(length = 32)
    private String code;

    @Column(length = 32)
    private String connectorName;

//    @Column(length = 32)
//    private String configCode;

    /**
     * connectorSchema default form config
     */
    @Column(columnDefinition = "TEXT")
    private String connectorConfigs;

//    @Column(length = 2048)
//    private String configs;

    @Column(length = 128)
    private String nodeType;

    @Column(length = 256)
    private String description;

    @Column(length = 32)
    @Enumerated(value = EnumType.STRING)
    private DataStatus status;

    @Column(length = 32)
    private String icon;

    @Column(length = 32)
    private String color;

    @Column(length = 256)
    private String style;

    @Column(length = 16)
    @Enumerated(value = EnumType.STRING)
    private AppPrimitive primitive;

    @Column(length = 16)
    @Enumerated(value = EnumType.STRING)
    private AppNodeDefinition.RunMethod runMethod;

    @Column(length = 512)
    private String inPorts;

    @Column(length = 512)
    private String outPorts;

    @Column
    private Boolean enableDelete;

    @Column
    private Boolean used;

    @Column(length = 32)
    private String appSource;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String config;


    @Column(columnDefinition = "MEDIUMTEXT")
    private String executionPreview;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String executeCode;

    /**
     * 非数据库字段，用户关联查询获取数据使用
     */
    @TableField(exist = false)
    private Integer nodeGroupId;


}
