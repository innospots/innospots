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

package io.innospots.libra.security.auth.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Smars
 * @date 2021/2/15
 */
@Getter
@Setter
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = -8445943548965154778L;

    private String username;
    private String password;
    private String securityCode;
    private String sign;
    /**
     * current login organization
     */
    private Integer orgId;
    private Long ts;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.ts = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoginRequest{");
        sb.append("username='").append(username).append('\'');
        sb.append(", sign='").append(sign).append('\'');
        sb.append(", ts=").append(ts);
        sb.append('}');
        return sb.toString();
    }
}
