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

package io.innospots.libra.security.jwt;

import io.innospots.libra.security.auth.AuthToken;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;

import static io.innospots.libra.security.jwt.JwtAuthManager.CLAIM_KEY_TENANT_ID;
import static io.innospots.libra.security.jwt.JwtAuthManager.CLAIM_KEY_USER_ID;

/**
 * @author Smars
 * @date 2021/2/15
 */
@Getter
@Setter
public class JwtToken extends AuthToken {

    protected Long timestamp;
    private Long expire;
    private String audience;
    private Integer lastPasswordResetType;

    public JwtToken(final String token, Long expire) {
        this.rawToken = token;
        this.expire = expire;
    }

    public void fillClaims(Claims claims) {
        this.setTimestamp(claims.getIssuedAt().getTime());
        this.setUserName(claims.getSubject());
        this.setUserId(claims.get(CLAIM_KEY_USER_ID, Integer.class));
        this.setOrgId(claims.get(CLAIM_KEY_TENANT_ID, Integer.class));
        this.setAudience(claims.getAudience());
    }

    public boolean isTokenNotExpired() {
        return expire > System.currentTimeMillis();
    }

}
