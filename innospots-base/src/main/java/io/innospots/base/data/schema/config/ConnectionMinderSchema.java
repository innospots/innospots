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

package io.innospots.base.data.schema.config;

import io.innospots.base.enums.ConnectType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/3/15
 */
@Getter
@Setter
public class ConnectionMinderSchema {

    private String name;

    private String icon;

    private Integer order;

    private Boolean enabled;

    /**
     * the nodeType of the application node definition
     */
    private String appNodeType;

    private ConnectType connectType;

    private String overview;

    private String description;

    private List<CredentialFormConfig> configs;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name='").append(name).append('\'');
        sb.append(", icon='").append(icon).append('\'');
        sb.append(", order=").append(order);
        sb.append(", enabled=").append(enabled);
        sb.append(", configs=").append(configs);
        sb.append('}');
        return sb.toString();
    }
}
