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

package io.innospots.base.data.schema.reader;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.innospots.base.data.schema.SchemaRegistry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Smars
 * @date 2021/6/26
 */
public class CachedSchemaRegistryReader implements ISchemaRegistryReader {

    private final ISchemaRegistryReader schemaRegistryReader;

    private final Cache<Integer, List<SchemaRegistry>> schemaRegistryListCache;

    private final Cache<String, SchemaRegistry> schemaRegistryCache;

    public CachedSchemaRegistryReader(ISchemaRegistryReader schemaRegistryReader, int cacheTimeoutSecond) {
        this.schemaRegistryReader = schemaRegistryReader;

        schemaRegistryListCache = Caffeine.newBuilder()
                .expireAfterAccess(cacheTimeoutSecond, TimeUnit.SECONDS)
                .build();

        schemaRegistryCache = Caffeine.newBuilder()
                .expireAfterAccess(cacheTimeoutSecond, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public List<SchemaRegistry> listSchemaRegistries(Integer credentialId, boolean includeField) {

        List<SchemaRegistry> schemaRegistries = schemaRegistryListCache.get(credentialId,
                // 始终保持数据源id对应的数据集列表信息是最新的全部数据
                key -> this.schemaRegistryReader.listSchemaRegistries(credentialId, includeField));

        if (CollectionUtils.isEmpty(schemaRegistries)) {
            return Collections.emptyList();
        }

        return schemaRegistries;
    }


    @Override
    public SchemaRegistry getSchemaRegistry(Integer credentialId, String registryCode, Integer registryId) {

        String schemaKey = credentialId + "_" + StringUtils.defaultString(registryCode, "") + "_";
        schemaKey += registryId != null ? registryId : "";
        // 从schemaRegistryCache获取
        return schemaRegistryCache.get(schemaKey,
                // 始终保持数据源id对应的数据集列表信息是最新的全部数据
                key -> this.schemaRegistryReader.getSchemaRegistry(credentialId, registryCode, registryId));

    }


}
