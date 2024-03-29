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

package io.innospots.connector.schema.reader;


import io.innospots.base.data.schema.SchemaField;
import io.innospots.base.data.schema.SchemaRegistry;
import io.innospots.base.data.schema.reader.ISchemaRegistryReader;
import io.innospots.base.exception.ResourceException;
import io.innospots.base.exception.ValidatorException;
import io.innospots.connector.schema.operator.SchemaFieldOperator;
import io.innospots.connector.schema.operator.SchemaRegistryOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author Smars
 * @date 2021/2/14
 */
@Slf4j
public class SchemaRegistryReader implements ISchemaRegistryReader {

    private final SchemaRegistryOperator schemaRegistryOperator;

    private final SchemaFieldOperator schemaFieldOperator;

    public SchemaRegistryReader(SchemaRegistryOperator schemaRegistryOperator,
                                SchemaFieldOperator schemaFieldOperator
    ) {
        this.schemaRegistryOperator = schemaRegistryOperator;
        this.schemaFieldOperator = schemaFieldOperator;
    }

    @Override
    public List<SchemaRegistry> listSchemaRegistries(Integer credentialId, boolean includeField) {

        List<SchemaRegistry> schemaRegistries = this.schemaRegistryOperator.listSchemaRegistries(credentialId);
        if (includeField && CollectionUtils.isNotEmpty(schemaRegistries)) {
            for (SchemaRegistry schemaRegistry : schemaRegistries) {
                schemaRegistry.setSchemaFields(schemaFieldOperator.listByRegistryId(schemaRegistry.getRegistryId()));
            }
        }

        return schemaRegistries;
    }


    @Override
    public SchemaRegistry getSchemaRegistry(Integer credentialId, String registryCode, Integer registryId) {

        SchemaRegistry schemaRegistry = null;

        if (registryId != null) {
            schemaRegistry = this.schemaRegistryOperator.getSchemaRegistryById(registryId);
            if (schemaRegistry == null) {
                throw ResourceException.buildNotExistException(this.getClass(), "registry not exist, credentialId: " + credentialId + " , registryId: " + registryId);
            }
            List<SchemaField> schemaFields = this.schemaFieldOperator.listByRegistryId(registryId);
            schemaRegistry.setSchemaFields(schemaFields);
        } else if (registryCode != null) {
            schemaRegistry = this.schemaRegistryOperator.getSchemaRegistryByCode(registryCode);
            if (schemaRegistry == null) {
                throw ResourceException.buildNotExistException(this.getClass(), "registry not exist, credentialId: " + credentialId + " , registryCode: " + registryCode);
            }
            List<SchemaField> schemaFields = this.schemaFieldOperator.listByRegistryId(schemaRegistry.getRegistryId());
            schemaRegistry.setSchemaFields(schemaFields);
        } else {
            throw ValidatorException.buildMissingException(this.getClass(), "registryId and registryCode can't be empty at the same time.");
        }

        return schemaRegistry;
    }

}
