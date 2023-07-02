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

package io.innospots.libra.kernel.module.system.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenc
 * @date 2021/2/7 16:51
 */
@Getter
public enum SystemRoleCode {
    /**
     *
     */
    PROJECT_ADMIN("project_admin", 2),
    SUPER_ADMIN("super_admin", 1);
//    SYSTEM_OPERATOR("system_operator"),
//    PROJECT_MEMBER("project_member");

    private static Map<String, SystemRoleCode> systemRoleCodeMap = new HashMap<>(4);

    private String code;

    private Integer roleId;

    SystemRoleCode(String code, Integer roleId) {
        this.code = code;
        this.roleId = roleId;
    }

    static {
        for (SystemRoleCode value : SystemRoleCode.values()) {
            systemRoleCodeMap.put(value.getCode(), value);
        }
    }

    public static SystemRoleCode getSystemRoleCodeByName(String roleCode) {
        return systemRoleCodeMap.get(roleCode);
    }

    public Integer getRoleId() {
        return roleId;
    }

    public String getCode() {
        return code;
    }
}
