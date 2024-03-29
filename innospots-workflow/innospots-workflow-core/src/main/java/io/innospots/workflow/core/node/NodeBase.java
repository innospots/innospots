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

package io.innospots.workflow.core.node;

import io.innospots.workflow.core.node.apps.AppConnectorConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Raydian
 * @date 2020/12/30
 */
@Getter
@Setter
public class NodeBase extends AppInfo {

    private static final Logger logger = getLogger(NodeBase.class);


    @Schema(title = "the name of connector schema")
    protected String connectorName;

//    @Schema(title = "credential form config code")
//    protected String configCode;

    @Schema(title = "app credential connector form configs")
    protected List<AppConnectorConfig> connectorConfigs;

    @Schema(title = "node color")
    protected String color;


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", nodeType='").append(nodeType).append('\'');
        sb.append(", primitive=").append(primitive);
        sb.append(", icon='").append(icon).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
