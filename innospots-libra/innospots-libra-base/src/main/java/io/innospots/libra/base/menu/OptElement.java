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

package io.innospots.libra.base.menu;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Smars
 * @date 2021/12/2
 */
@Getter
@Setter
public class OptElement extends BaseItem {

    private UriMethod method;

    @Getter
    public enum UriMethod {

        /**
         *
         */
        PUT("put"),
        DELETE("delete"),
        GET("get"),
        POST("post"),
        ALL("all");

        private String name;

        UriMethod(String name) {
            this.name = name;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("itemKey='").append(itemKey).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", icon='").append(icon).append('\'');
        sb.append(", uri='").append(uri).append('\'');
        sb.append(", itemType=").append(itemType);
        sb.append(", openMode=").append(openMode);
        sb.append(", i18nNames=").append(i18nNames);
        sb.append(", method=").append(method);
        sb.append('}');
        return sb.toString();
    }
}