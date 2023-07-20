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
import io.innospots.base.data.enums.ApiMethod;
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
public class OAuth2ApiConnectionMinder extends OAuth2ClientConnectionMinder {


    protected final String CODE = "code";

    protected final String APP_ID = "appid";

    protected final String AUTHORITY_URL = "authority_url";
    protected final String REFRESH_TOKEN = "refresh_token";
    protected final String ACCESS_TOKEN = "access_token";

    protected final String EXPIRES_IN = "expires_in";


    @Override
    public Object fetchSample(ConnectionCredential connectionCredential, String tableName) {
        String clientId = connectionCredential.v(CLIENT_ID);
        String clientSecret = connectionCredential.v(CLIENT_SECRET);
        String authorityUrl = connectionCredential.v(AUTHORITY_URL);
        String refreshToken = connectionCredential.v(REFRESH_TOKEN);
        String expiresIn = connectionCredential.v(EXPIRES_IN);
        String accessToken = connectionCredential.v(ACCESS_TOKEN);
        String accessTokenUrl = connectionCredential.v(ACCESS_TOKEN_URL);
        String grantType = "";
        //1. 判断refreshToken是否为空，不为空 ,如果过期或者expiresIn，则调用刷新TOKEN获取

        //2. 如果accessToken不为空，也没有过期，则返回true

        //3. 如果accessToken为空，refreshToken为空，则返回重定向URL地址



        return null;
    }

}
