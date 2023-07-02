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

package io.innospots.libra.kernel.module.workspace.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * System Env Info
 */
@Getter
@Setter
@Schema(title = "System Env Info")
public class SystemEnvInfo {

    @Schema(title = "jdk version")
    private String jdkVersion;

    @Schema(title = "java home")
    private String javaHome;

    @Schema(title = "whether to install flink")
    private boolean installFlink = false;

    @Schema(title = "whether to install mysql")
    private boolean installMysql = false;

    @Schema(title = "whether to install kafka")
    private boolean installKafka = false;

    @Schema(title = "whether to install redis")
    private boolean installRedis = false;
}