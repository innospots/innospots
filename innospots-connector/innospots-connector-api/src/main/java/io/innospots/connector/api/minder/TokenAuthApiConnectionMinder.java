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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.innospots.base.data.enums.ApiMethod;
import io.innospots.base.data.http.HttpConnection;
import io.innospots.base.data.http.HttpData;
import io.innospots.base.data.http.HttpDataConnectionMinder;
import io.innospots.base.data.http.HttpDataExecutor;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.utils.HttpClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.noear.snack.ONode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/14
 */
public class TokenAuthApiConnectionMinder extends HttpDataConnectionMinder {


    public static final String TOKEN_ADDRESS = "token_address";
    public static final String REQUEST_METHOD = "request_method";
    public static final String TOKEN_LOCATION = "token_location";
    public static final String TOKEN_PARAM = "token_param";
    public static final String CACHE_TIME = "cache_time";
    public static final String TOKEN_JSON_PATH = "token_json_path";

    private TokenConfig tokenConfig;

    @Override
    public void open() {
        tokenConfig = new TokenConfig(connectionCredential);
        super.open();

    }

    @Override
    protected Supplier<Map<String, String>> headers() {
        return () -> {
            HashMap<String, String> headers = new HashMap<>();
            if (tokenConfig.tokenLoc == TokenLocation.HEADER) {
                headers.put(tokenConfig.getTokenParam(), tokenConfig.fetchToken(true));
            }
            return headers;
        };
    }

    @Override
    protected Supplier<Map<String, Object>> defaultParams() {
        return () -> {
            HashMap<String, Object> params = new HashMap<>();
            if (tokenConfig.tokenLoc == TokenLocation.PARAM) {
                params.put(tokenConfig.getTokenParam(), tokenConfig.fetchToken(true));
            }
            return params;
        };
    }

    @Override
    protected Supplier<Map<String, Object>> defaultBody() {
        return () -> {
            HashMap<String, Object> body = new HashMap<>();
            if (tokenConfig.tokenLoc == TokenLocation.BODY) {
                body.put(tokenConfig.getTokenParam(), tokenConfig.fetchToken(true));
            }
            return body;
        };
    }





    @Override
    public boolean test(ConnectionCredential connectionCredential) {
        return super.test(connectionCredential);
    }

    @Getter
    @Setter
    private static class TokenConfig {

        private ApiMethod apiMethod;

        private String tokenParam;

        private Integer cacheTime;

        private String jsonPath;

        private LocalDateTime expireTime;

        private TokenLocation tokenLoc;

        private String address;

        private String token;

        private HttpConnection httpConnection = new HttpConnection();

        public TokenConfig(ConnectionCredential connectionCredential) {

            address = connectionCredential.v(TOKEN_ADDRESS);
            apiMethod = ApiMethod.valueOf(connectionCredential.v(REQUEST_METHOD));
            Object ct = connectionCredential.value(CACHE_TIME);
            if (ct == null) {
                cacheTime = 180;
            } else {
                cacheTime = Integer.valueOf(ct.toString());
            }
            tokenParam = connectionCredential.v(TOKEN_PARAM);
            jsonPath = connectionCredential.v(TOKEN_JSON_PATH);
            tokenLoc = TokenLocation.valueOf(connectionCredential.v(TOKEN_LOCATION));
        }

        public String fetchToken(boolean cache) {

            LocalDateTime now = LocalDateTime.now();
            if (cache && expireTime != null && expireTime.isAfter(now) && token != null) {
                return token;
            }

            if (apiMethod == ApiMethod.GET) {
                HttpData httpData = httpConnection.get(address, null, null);
                token = extractToken(httpData);
            } else if (apiMethod == ApiMethod.POST) {
                HttpData httpData = httpConnection.post(address, null, null, null);
                token = extractToken(httpData);
            }
            if (token != null) {
                expireTime = LocalDateTime.now();
            }

            return token;
        }

        private String extractToken(HttpData httpData) {
            String t = null;
            if (httpData.getBody() instanceof Map) {
                ONode jsonNode = ONode.load(httpData.getBody());
                t = jsonNode.select(this.jsonPath).toObject();
            } else if (httpData.getBody() instanceof String) {
                ONode jsonNode = ONode.load(JSONUtils.toMap((String) httpData.getBody()));
                t = jsonNode.select(this.jsonPath).toObject();
            }
            return t;
        }

    }

    public enum TokenLocation {
        HEADER,
        BODY,
        PARAM;
    }
}
