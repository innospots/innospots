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

import cn.hutool.core.net.url.UrlBuilder;
import com.google.common.collect.ImmutableMap;
import io.innospots.base.constant.PathConstant;
import io.innospots.base.crypto.IEncryptor;
import io.innospots.base.data.enums.ApiMethod;
import io.innospots.base.data.http.HttpData;
import io.innospots.base.data.http.HttpDataConnectionMinder;
import io.innospots.base.data.http.HttpDataExecutor;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.base.exception.data.HttpConnectionException;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.utils.ApplicationContextUtils;
import io.innospots.base.utils.HttpClientBuilder;
import org.apache.commons.lang3.RandomStringUtils;
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
    protected final String REDIRECT_ADDRESS = "redirect_address";
    protected final String TOKEN_TIME = "token_time";
    protected final String SCOPE = "scope";

    protected final String EXPIRES_IN = "expires_in";

    private final String OAUTH_CALLBACK = PathConstant.ROOT_PATH+"oauth2/callback";


    @Override
    public Object test(ConnectionCredential connectionCredential) {
        String clientId = connectionCredential.v(CLIENT_ID);
        String clientSecret = connectionCredential.v(CLIENT_SECRET);
        String authorityUrl = connectionCredential.v(AUTHORITY_URL);
        String refreshToken = connectionCredential.v(REFRESH_TOKEN);
        String expiresIn = connectionCredential.v(EXPIRES_IN);
        String accessToken = connectionCredential.v(ACCESS_TOKEN);
        String accessTokenUrl = connectionCredential.v(ACCESS_TOKEN_URL);
        String redirectUrl = connectionCredential.v(REDIRECT_ADDRESS);
        String scopes = connectionCredential.v(SCOPE);
        String grantType = "";
        //1. 判断refreshToken是否为空，不为空 ,如果过期或者expiresIn，则调用刷新TOKEN获取

        //2. 如果accessToken不为空，也没有过期，则返回true

        //3. 如果accessToken为空，refreshToken为空，则返回重定向URL地址

        return super.test(connectionCredential);
    }

    private Map<String,Object> refreshToken(String accessTokenUrl,String refreshToken,String clientId,String secretId){
        Map<String,Object> resp = new HashMap<>();
        UrlBuilder rb = UrlBuilder.of().addQuery(CLIENT_ID,clientId)
                .addQuery(CLIENT_SECRET,secretId)
                .addQuery("grant_type","refresh_token")
                .addQuery("refresh_token", refreshToken);
        TokenHolder holder = new TokenHolder();
        holder.setAddress(accessTokenUrl);
        holder.setApiMethod(ApiMethod.POST);
        holder.setJsonPath("$.access_token");
        holder.setTokenLoc(TokenHolder.TokenLocation.PARAM);
        holder.setQueryParam(rb.getQueryStr());
        String token = holder.fetchToken(false);
        IEncryptor encryptor = ApplicationContextUtils.getBean(IEncryptor.class);
        if(token != null){
            Map<String,Object> body = new HashMap<>();
            body.put(ACCESS_TOKEN,token);
            body.put(EXPIRES_IN,holder.getCacheTime());
            body.put(TOKEN_TIME,System.currentTimeMillis());

            resp.put("encryptFormValues",encryptor.encode(JSONUtils.toJsonString(body)));
        }
        return resp;
    }

    private Map<String,Object> openAuthorize(String authorityUrl,String clientId,String callBackUrl,String scopes){
        Map<String,Object> resp = new HashMap<>();

        UrlBuilder rb = UrlBuilder.of(authorityUrl).addQuery(CLIENT_ID,clientId)
                .addQuery("response_type","code")
                .addQuery("redirect_uri",callBackUrl)
                .addQuery("state", RandomStringUtils.randomAlphabetic(6).toLowerCase());
        if(scopes!=null){
            rb.addQuery(SCOPE,scopes);
        }
        resp.put(AUTHORITY_URL,rb.build());
        //TODO cache
        return resp;
    }

}
