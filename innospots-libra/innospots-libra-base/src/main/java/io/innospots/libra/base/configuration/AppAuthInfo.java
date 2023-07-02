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

package io.innospots.libra.base.configuration;

import io.innospots.base.utils.Initializer;
import io.innospots.libra.base.matcher.UrlPathRequestMatcher;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Smars
 * @date 2021/2/17
 */
@Getter
@Setter
public class AppAuthInfo implements Initializer {

    private String signKey;

    private String name;

    private int tokenExpTimeMinute;

    private String secretKey;

    private List<String> allowPaths;

    private UrlPathRequestMatcher pathRequestMatcher;


    @Override
    public void initialize() {
        if (allowPaths != null) {
            pathRequestMatcher = new UrlPathRequestMatcher(allowPaths);
        }
    }

    public boolean match(HttpServletRequest httpServletRequest) {
        return pathRequestMatcher.matches(httpServletRequest);
    }
}
