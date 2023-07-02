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

package io.innospots.libra.security.context;

import io.innospots.libra.security.auth.AuthToken;
import org.springframework.util.Assert;

/**
 * @author castor_ling
 * @date 2021/3/12
 */
public class SecurityContextHolder {

    private static final ThreadLocal<AuthToken> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    public static AuthToken getContext() {
        AuthToken ctx = CONTEXT_HOLDER.get();

        /*
        if (ctx == null) {
            ctx = new AuthToken();
            contextHolder.set(ctx);
        }
         */

        return ctx;
    }

    public static void setContext(AuthToken context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        CONTEXT_HOLDER.set(context);
    }

}
