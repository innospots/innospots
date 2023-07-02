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

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.innospots.base.data.schema.AppCredentialInfo;
import io.innospots.base.data.schema.ConnectionCredential;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.TimeUnit;


/**
 * Cache-based read encapsulation of data sources,
 * The decorator pattern extends the read operations of the data source
 *
 * @author Smars
 * @date 2021/6/26
 */
public class CachedConnectionCredentialReader implements IConnectionCredentialReader {

    private final LoadingCache<Integer, ConnectionCredential> infoCache;

    private final IConnectionCredentialReader connectionCredentialReader;

    public CachedConnectionCredentialReader(IConnectionCredentialReader credentialReader, int cacheTimeoutSecond) {
        this.connectionCredentialReader = credentialReader;

        infoCache = Caffeine.newBuilder()
                .expireAfterAccess(cacheTimeoutSecond, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, ConnectionCredential>() {
                    @Override
                    public @Nullable ConnectionCredential load(@NonNull Integer credentialId) throws Exception {
                        return connectionCredentialReader.readCredential(credentialId);
                    }
                });
    }


    @Override
    public ConnectionCredential readCredential(Integer credentialId) {
        return infoCache.get(credentialId);
    }

    @Override
    public ConnectionCredential readCredential(String credentialCode) {
        return connectionCredentialReader.readCredential(credentialCode);
    }

    @Override
    public ConnectionCredential fillCredential(AppCredentialInfo appCredentialInfo) {
        return connectionCredentialReader.fillCredential(appCredentialInfo);
    }


}
