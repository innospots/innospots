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

package io.innospots.base.enums;

/**
 * @author Raydian
 * @date 2020/11/28
 */
public enum DataStatus {

    /**
     * 数据状态 使用  ONLINE OR OFFLINE
     */
    ONLINE("online"),
    DRAFT("draft"),
    AUDITING("auditing"),
    AUDITED("audited"),
    AUDIT_REFUSE("refuse"),
    REMOVED("removed"),
    OFFLINE("offline");

    private String info;

    DataStatus(String info) {
        this.info = info;
    }

    public static Boolean OnlineOrOffline(DataStatus status) {
        return ONLINE.equals(status) || OFFLINE.equals(status);
    }

}