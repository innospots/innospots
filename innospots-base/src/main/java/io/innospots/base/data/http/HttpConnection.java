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

import io.innospots.base.data.enums.ApiAuth;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.utils.HttpClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * http连接管理
 *
 * @author Smars
 * @date 2021/8/31
 */
@Slf4j
public class HttpConnection {

    private CloseableHttpClient httpClient;

    private ConnectionCredential connectionCredential;

    private Map<String,String> defaultParams = new HashMap<>();

    private Map<String,Object> defaultBody = new HashMap<>();

    public HttpConnection(ConnectionCredential connectionCredential) {
        this(connectionCredential, null);
    }

    public HttpConnection(ConnectionCredential connectionCredential, Map<String, String> headers) {
        this.connectionCredential = connectionCredential;
        this.httpClient = HttpClientBuilder.build(15 * 1000, 15, headers);
    }

    public HttpConnection(ConnectionCredential connectionCredential,
                          Map<String, String> headers,
                          Map<String, String> defaultParams,
                          Map<String, Object> defaultBody
                          ) {
        this.connectionCredential = connectionCredential;
        this.httpClient = HttpClientBuilder.build(15 * 1000, 15, headers);
        this.defaultParams = defaultParams;
        this.defaultBody = defaultBody;
    }

    public HttpConnection() {
        this(null);
    }

    /*
    private Map<String, String> authHeaders() {
        if (this.connectionCredential == null) {
            return Collections.emptyMap();
        }
        ApiAuth apiAuth = ApiAuth.parse(this.connectionCredential.getConfig());
        Map<String, String> headerMap = new HashMap<>();
        if (apiAuth == ApiAuth.BASIC_AUTH) {
            String username = this.connectionCredential.v(HttpDataExecutor.KEY_USERNAME);
            String password = this.connectionCredential.v(HttpDataExecutor.KEY_PASSWORD);
            HttpClientBuilder.fillBasicAuthHeader(username, password, headerMap, StandardCharsets.UTF_8);
        } else if (apiAuth == ApiAuth.BEARER_TOKEN) {
            String token = this.connectionCredential.v(HttpDataExecutor.KEY_TOKEN);
            HttpClientBuilder.fillBearerAuthHeader(token, headerMap);
        }

        return headerMap;
    }

     */

    public ConnectionCredential connectionCredential() {
        return this.connectionCredential;
    }

    public HttpData get(String url, Map<String, Object> params, Map<String, String> headers) {
        return HttpClientBuilder.doGet(httpClient, url, params, headers);
    }

    public HttpData post(String url, Map<String, Object> params, String requestBody, Map<String, String> headers) {
        return post(url, params, requestBody, headers, null);
    }


    public HttpData post(String url, Map<String, Object> params, String requestBody, Map<String, String> headers, HttpContext httpContext) {
        return HttpClientBuilder.doPost(httpClient, url, headers, params, requestBody, httpContext);
    }

    public HttpData post(String url, Map<String, Object> params, Map<String,Object> jsonBody, Map<String, String> headers, HttpContext httpContext) {
        return HttpClientBuilder.doPost(httpClient, url, headers, params, JSONUtils.toJsonString(jsonBody), httpContext);
    }

    public HttpData postForm(String url, Map<String, Object> query, Map<String, Object> params, Map<String, String> headers) {
        return HttpClientBuilder.doPost(httpClient, url, query, params, headers);
    }

}
