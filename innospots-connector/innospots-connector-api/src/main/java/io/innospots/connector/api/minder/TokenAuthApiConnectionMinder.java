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

package io.innospots.connector.api.minder;

import io.innospots.base.data.enums.ApiMethod;
import io.innospots.base.data.http.HttpDataConnectionMinder;
import io.innospots.base.data.schema.ConnectionCredential;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/14
 */
@Slf4j
public class TokenAuthApiConnectionMinder extends HttpDataConnectionMinder {


    private static final String TOKEN_ADDRESS = "access_token_url";
    private static final String QUERY_PARAM = "query_param";
    private static final String POST_BODY = "post_body";
    private static final String REQUEST_METHOD = "request_method";
    private static final String TOKEN_LOCATION = "token_location";
    private static final String TOKEN_PARAM = "token_param";
    private static final String CACHE_TIME = "cache_time";
    private static final String TOKEN_JSON_PATH = "token_json_path";

    protected TokenHolder tokenHolder;

    @Override
    public void open() {
        tokenHolder = buildTokenHolder(connectionCredential);
        super.open();

    }

    @Override
    protected Supplier<Map<String, String>> headers() {
        return () -> {
            HashMap<String, String> headers = new HashMap<>();
            if (tokenHolder.getTokenLoc() == TokenHolder.TokenLocation.HEADER) {
                headers.put(tokenHolder.getTokenParam(), tokenHolder.fetchToken(true));
            }
            return headers;
        };
    }

    @Override
    protected Supplier<Map<String, Object>> defaultParams() {
        return () -> {
            HashMap<String, Object> params = new HashMap<>();
            if (tokenHolder.getTokenLoc() == TokenHolder.TokenLocation.PARAM) {
                params.put(tokenHolder.getTokenParam(), tokenHolder.fetchToken(true));
            }
            return params;
        };
    }

    @Override
    protected Supplier<Map<String, Object>> defaultBody() {
        return () -> {
            HashMap<String, Object> body = new HashMap<>();
            if (tokenHolder.getTokenLoc() == TokenHolder.TokenLocation.BODY) {
                body.put(tokenHolder.getTokenParam(), tokenHolder.fetchToken(true));
            }
            return body;
        };
    }


    @Override
    public Object test(ConnectionCredential connectionCredential) {
        TokenHolder holder = buildTokenHolder(connectionCredential);
        String token = holder.fetchToken(false);
        if(log.isDebugEnabled()){
            log.debug("test token :{}",holder);
        }
        if (token == null) {
            return false;
        } else {
            return true;
        }
    }

    protected TokenHolder buildTokenHolder(ConnectionCredential connectionCredential){

        TokenHolder tokenHolder = new TokenHolder();

        String address = connectionCredential.v(TOKEN_ADDRESS);
        ApiMethod apiMethod = ApiMethod.valueOf(connectionCredential.v(REQUEST_METHOD));
        Object ct = connectionCredential.value(CACHE_TIME);
        int cacheTime;
        if (ct == null) {
            cacheTime = 600;
        } else {
            cacheTime = Integer.parseInt(ct.toString());
        }

        String tokenParam = connectionCredential.v(TOKEN_PARAM);
        String jsonPath = connectionCredential.v(TOKEN_JSON_PATH);
        TokenHolder.TokenLocation tokenLoc = TokenHolder.TokenLocation.valueOf(connectionCredential.v(TOKEN_LOCATION));
        String queryParam = connectionCredential.v(QUERY_PARAM);
        String postBody = connectionCredential.v(POST_BODY);
        tokenHolder.setAddress(address);
        tokenHolder.setApiMethod(apiMethod);
        tokenHolder.setCacheTime(cacheTime);
        tokenHolder.setTokenParam(tokenParam);
        tokenHolder.setJsonPath(jsonPath);
        tokenHolder.setTokenLoc(tokenLoc);
        tokenHolder.setQueryParam(queryParam);
        tokenHolder.setPostBody(postBody);

        return tokenHolder;
    }

}
