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

package io.innospots.connector.schema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.innospots.base.data.schema.SchemaRegistryType;
import io.innospots.libra.base.entity.PBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static io.innospots.connector.schema.entity.SchemaRegistryEntity.TABLE_NAME;

/**
 * @author Raydian
 * @date 2021/1/17
 */
@Setter
@Getter
@Entity
@TableName(value = TABLE_NAME)
@Table(name = TABLE_NAME, indexes = {@Index(name = "idx_datasource_reg", columnList = "credentialId")})
public class SchemaRegistryEntity extends PBaseEntity {

    public static final String TABLE_NAME = "ds_schema_registry";

    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer registryId;

    @Column(length = 128)
    private String name;

    @Column(length = 128)
    private String code;

    @Column
    private Integer credentialId;

    @Column
    private Integer categoryId;

    @Column(length = 256)
    private String description;

    @Column(length = 16)
    @Enumerated(value = EnumType.STRING)
    private SchemaRegistryType registryType;

    @Column(columnDefinition = "TEXT")
    private String configs;

    @Column(columnDefinition = "TEXT")
    private String script;


}
