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

package io.innospots.base.data.enums;

import java.util.Map;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/5
 */
public enum ApiAuth {

    /**
     *
     */
    BASIC_AUTH,
    DIGEST_AUTH,
    BEARER_TOKEN,
    CUSTOM_AUTH,
    NO_AUTH,
    OAUTH2;

    public static final String KEY_AUTH_TYPE = "auth_type";


    public static ApiAuth parse(Map<String, Object> config) {
        if (config == null) {
            return NO_AUTH;
        }
        Object v = config.get(KEY_AUTH_TYPE);
        if (v == null) {
            return NO_AUTH;
        }
        try {
            return ApiAuth.valueOf(String.valueOf(v));
        } catch (Exception e) {
            return NO_AUTH;
        }
    }
}
