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

import java.util.List;

/**
 * System Basic Info
 */
@Getter
@Setter
@Schema(title = "System Basic Info")
public class SystemBasicInfo {

    @Schema(title = "operate system")
    private String os;

    @Schema(title = "kernel version")
    private String kernelVersion;

    @Schema(title = "CPU model")
    private String cpuModel;

    @Schema(title = "CPU cores")
    private String cpuCores;

    @Schema(title = "memory")
    private String memory;

    @Schema(title = "host name")
    private String hostName;

    @Schema(title = "disk space")
    private String diskSpace;

    @Schema(title = "domain name")
    private String domainName;

    @Schema(title = "dns server")
    private List<String> dnsServers;

    @Schema(title = "ip v4")
    private String ipv4;

    @Schema(title = "ip v6")
    private String ipv6;

    @Schema(title = "system boot time")
    private String bootTime;

    @Schema(title = "system manufacturer")
    private String SystemManufacturer;

    @Schema(title = "os manufacturer")
    private String osManufacturer;

    @Schema(title = "baseboard manufacturer")
    private String baseboardManufacturer;
}