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

import com.google.common.collect.ImmutableMap;
import io.innospots.base.data.http.HttpData;
import io.innospots.base.data.http.HttpDataConnectionMinder;
import io.innospots.base.data.http.HttpDataExecutor;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.base.exception.data.HttpConnectionException;
import io.innospots.base.utils.HttpClientBuilder;
import org.apache.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/14
 */
public class OAuth2ApiConnectionMinder extends HttpDataConnectionMinder {

    private final String CLIENT_ID = "client_id";
    private final String CLIENT_SECRET = "client_secret";
    private final String CODE = "code";
    private final String ACCESS_TOKEN_URL = "access_token_url";


    @Override
    protected Supplier<Map<String, String>> headers() {
        return ()->{
            HashMap<String, String> headers = new HashMap<>();
            if (this.connectionCredential != null) {
                // TODO connectionCredential成员变量的作用？
                String token = this.connectionCredential.v(HttpDataExecutor.KEY_TOKEN);
                HttpClientBuilder.fillBearerAuthHeader(token, headers);
            }
            return headers;
        };
    }


    @Override
    public Object fetchSample(ConnectionCredential connectionCredential, String tableName) {
        Map<String, Object> config = connectionCredential.getConfig();
        String clientId = String.valueOf(config.get(CLIENT_ID));
        String clientSecret = String.valueOf(config.get(CLIENT_SECRET));
        String code = String.valueOf(config.get(CODE));
        String accessTokenUrl = String.valueOf(config.get(ACCESS_TOKEN_URL));

        Map<String, Object> param = ImmutableMap.of(
                CLIENT_ID, clientId,
                CLIENT_SECRET, clientSecret,
                CODE, code
        );

        HttpData httpData = httpConnection.get(accessTokenUrl, param, Collections.emptyMap());
        if (httpData.getStatus() != HttpStatus.SC_OK) {
            throw HttpConnectionException.buildException(this.getClass(), connectionCredential, httpData);
        }
        return httpData.getBody();
    }
}
