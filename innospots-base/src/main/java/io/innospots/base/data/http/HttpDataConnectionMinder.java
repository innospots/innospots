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

package io.innospots.base.data.http;


import io.innospots.base.data.minder.BaseDataConnectionMinder;
import io.innospots.base.data.operator.IExecutionOperator;
import io.innospots.base.data.operator.IOperator;
import io.innospots.base.data.schema.ApiSchemaRegistry;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.base.mapper.ApiSchemaRegistryConvertMapper;
import io.innospots.base.model.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Alfred
 * @date 2021-08-21
 */
@Slf4j
public class HttpDataConnectionMinder extends BaseDataConnectionMinder {

    private static final Logger logger = LoggerFactory.getLogger(HttpDataConnectionMinder.class);

    protected HttpConnection httpConnection;

    protected HttpDataExecutor httpDataExecutor;

    public static final HttpDataConnectionMinder DEFAULT_HTTP_CONNECTION_MINDER = new HttpDataConnectionMinder().init();


    /*
    public ApiSchemaRegistry getApiSchemaRegistry(String tableName) {
        return ApiSchemaRegistryConvertMapper.INSTANCE.schemaRegistryToApi(this.schemaRegistry(tableName));
    }

     */

    protected Map<String, String> authHeaders() {
        return new HashMap<>();
    }

    public HttpDataConnectionMinder init() {
        if (this.httpConnection != null) {
            open();
        } else {
            httpConnection = new HttpConnection();
        }
        return this;
    }

    @Override
    public void open() {
        if (httpConnection != null) {
            return;
        }
        httpConnection = new HttpConnection(connectionCredential, authHeaders());
    }


    @Override
    public boolean test(ConnectionCredential connectionCredential) {
        // TODO 先默认返回true
//        this.connectionCredential = connectionCredential;
//        HttpDataExecutor httpDataExecutor = new HttpDataExecutor(new HttpConnection(connectionCredential, authHeaders()),null);
//        RequestBody requestBody = new RequestBody();
//        return httpDataExecutor.execute(requestBody).getBody() != null;
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public String connector() {
        return "http-api";
    }

    public Object fetchSample(ConnectionCredential connectionCredential, ApiSchemaRegistry apiSchemaRegistry) {
        HttpDataExecutor httpDataExecutor = new HttpDataExecutor(httpConnection);
//        HttpDataExecutor httpDataExecutor = new HttpDataExecutor(new HttpConnection(connectionCredential, authHeaders()), apiSchemaRegistry);
        RequestBody requestBody = new RequestBody();
        return httpDataExecutor.execute(requestBody).getBody();
    }

    @Override
    public Object fetchSample(ConnectionCredential connectionCredential, String tableName) {

        HttpConnection httpConnection = new HttpConnection(connectionCredential);

//        HttpDataExecutor httpDataExecutor = new HttpDataExecutor(httpConnection, getApiSchemaRegistry(tableName));
        HttpDataExecutor httpDataExecutor = new HttpDataExecutor(httpConnection);
        RequestBody requestBody = new RequestBody();
        return httpDataExecutor.execute(requestBody).getBody();
    }

    public IOperator buildOperator() {
        return buildExecutionOperator();
    }

    public IExecutionOperator buildExecutionOperator() {
        if(httpDataExecutor == null){
            httpDataExecutor = new HttpDataExecutor(httpConnection);
        }
        return httpDataExecutor;
//        return new HttpDataExecutor(httpConnection, getApiSchemaRegistry(null));
    }
}
