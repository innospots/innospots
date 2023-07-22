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
import io.innospots.base.exception.AuthenticationException;
import io.innospots.base.exception.data.HttpConnectionException;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.store.CacheStoreManager;
import io.innospots.base.utils.ApplicationContextUtils;
import io.innospots.base.utils.HttpClientBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDateTime;
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
    protected final String REDIRECT_URL = "redirect_url";
    protected final String TOKEN_TIME = "token_time";
    protected final String SCOPE = "scope";
    protected final String STATE = "state";

    protected final String EXPIRES_IN = "expires_in";
    protected final String GRANT_TYPE = "grant_type";

    private final String OAUTH_CALLBACK = PathConstant.ROOT_PATH + "oauth2/callback/";


    @Override
    public Object test(ConnectionCredential connectionCredential) {

        String clientId = connectionCredential.v(CLIENT_ID);
        String clientSecret = connectionCredential.v(CLIENT_SECRET);
        String authorityUrl = connectionCredential.v(AUTHORITY_URL);
        String refreshToken = connectionCredential.v(REFRESH_TOKEN);
        String expiresIn = connectionCredential.v(EXPIRES_IN);
        int expireSecond = expiresIn != null ? Integer.parseInt(expiresIn) : 0;
        String accessToken = connectionCredential.v(ACCESS_TOKEN);
        String ts = connectionCredential.v(TOKEN_TIME);
        long tokenTs = ts != null ? Long.parseLong(ts) : 0;
        String accessTokenUrl = connectionCredential.v(ACCESS_TOKEN_URL);
        String redirectAddress = connectionCredential.v(REDIRECT_ADDRESS);
        String scopes = connectionCredential.v(SCOPE);
        String state = connectionCredential.v(STATE);

        String code = connectionCredential.v(CODE);

        String redirectUrl = redirectAddress != null ? redirectAddress + OAUTH_CALLBACK + connectionCredential.getAppNodeCode() : null;

        Map<String, Object> resp = null;

        if (code != null && redirectUrl != null) {
            resp = fetchAccessToken(accessTokenUrl, clientId, clientSecret, code, redirectUrl,state);
        }

        //1. 判断refreshToken是否为空，不为空 ,如果过期或者expiresIn，则调用刷新TOKEN获取
        if (refreshToken != null && ts != null &&
                Instant.ofEpochMilli(tokenTs).plusSeconds(expireSecond).isBefore(Instant.now())) {
            resp = refreshToken(connectionCredential.getAppNodeCode(), accessTokenUrl, refreshToken, clientId, clientSecret);
        }

        //2. 如果accessToken为空，refreshToken为空，则返回重定向URL地址
        if (refreshToken == null || accessToken == null) {
            resp = openAuthorize(authorityUrl, clientId, clientSecret, redirectUrl, scopes);
        }

        //3. 如果accessToken不为空，也没有过期，则返回true
        if (accessToken != null) {
            return true;
        }

        return resp;
    }

    private Map<String, Object> fetchAccessToken(String accessTokenUrl, String clientId, String clientSecret, String code, String redirectUrl,String appCode) {
        UrlBuilder rb = UrlBuilder.of().addQuery(CLIENT_ID, clientId)
                .addQuery(CLIENT_SECRET, clientSecret)
                .addQuery(GRANT_TYPE, "authorization_code")
                .addQuery(REDIRECT_URL, redirectUrl)
                .addQuery(CODE, code);
        TokenHolder holder = buildTokenHolder(accessTokenUrl,rb.getQueryStr());
        return cacheToken(holder,clientId,appCode);
    }

    private Map<String, Object> refreshToken(String appCode, String accessTokenUrl, String refreshToken, String clientId, String clientSecret) {

        UrlBuilder rb = UrlBuilder.of().addQuery(CLIENT_ID, clientId)
                .addQuery(CLIENT_SECRET, clientSecret)
                .addQuery(GRANT_TYPE, "refresh_token")
                .addQuery("refresh_token", refreshToken);
        TokenHolder holder = buildTokenHolder(accessTokenUrl,rb.getQueryStr());

        return cacheToken(holder,clientId,appCode);
    }

    private Map<String, Object> openAuthorize(String authorityUrl,
                                              String clientId,
                                              String clientSecret,
                                              String redirectUrl, String scopes) {
        String state = RandomStringUtils.randomAlphabetic(6).toLowerCase();

        Map<String, Object> resp = new HashMap<>();
        resp.put(CLIENT_SECRET, clientSecret);
        resp.put(CLIENT_ID, clientId);
        resp.put(REDIRECT_URL, redirectUrl);

        UrlBuilder rb = UrlBuilder.of(authorityUrl).addQuery(CLIENT_ID, clientId)
                .addQuery("response_type", "code")
                .addQuery("redirect_uri", redirectUrl)
                .addQuery(STATE, state);
        if (scopes != null) {
            rb.addQuery(SCOPE, scopes);
        }
        resp.put(AUTHORITY_URL, rb.build());
        resp.put(STATE, state);
        CacheStoreManager.save(state, JSONUtils.toJsonString(resp));
        resp.remove(CLIENT_SECRET);
        return resp;
    }

    private Map<String,Object> cacheToken(TokenHolder holder,String clientId,String appCode){
        Map<String, Object> resp = new HashMap<>();
        String token = holder.fetchToken(false);

        if (token != null) {
            resp.put(ACCESS_TOKEN, token);
            resp.put(EXPIRES_IN, holder.getCacheTime());
            resp.put(TOKEN_TIME, System.currentTimeMillis());
            if (holder.getResponse() != null && holder.getResponse().getBody() instanceof Map) {
                resp.putAll((Map<? extends String, ?>) holder.getResponse().getBody());
            }
        }else{
            throw AuthenticationException.buildTokenInvalidException(this.getClass(),"obtain token is fail ",appCode,clientId);
        }
        CacheStoreManager.save(clientId + "_" + appCode, JSONUtils.toJsonString(resp));
        resp.remove(ACCESS_TOKEN);

        return resp;
    }


    private TokenHolder buildTokenHolder(String url,String params) {
        TokenHolder holder = new TokenHolder();
        holder.setAddress(url);
        holder.setApiMethod(ApiMethod.POST);
        holder.setJsonPath("$.access_token");
        holder.setTokenLoc(TokenHolder.TokenLocation.PARAM);
        holder.setQueryParam(params);
        return holder;
    }
}
