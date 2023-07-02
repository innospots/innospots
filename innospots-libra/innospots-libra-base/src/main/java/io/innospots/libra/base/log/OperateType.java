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

package io.innospots.libra.base.log;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2021/12/12
 */
public enum OperateType {

    /**
     *
     */
    CREATE("${common.button.create}"),
    ADD("${common.button.add}"),
    UPDATE("${common.button.edit}"),
    READ("${common.button.read}"),
    SEARCH("${common.button.search}"),
    FETCH("${common.button.fetch}"),
    LIST("${common.button.list}"),
    RECYCLE("${common.button.recycle}"),
    DELETE("${common.button.delete}"),
    UPDATE_STATUS("${common.button.status}"),
    PUBLISH("${common.button.publish}"),
    ONLINE("${common.button.online}"),
    OFFLINE("${common.button.offline}"),
    ACTIVE("${common.button.active}"),
    INACTIVE("${common.button.inactive}"),
    SCHEDULE("${common.button.schedule}"),
    EXECUTE("${common.button.execute}"),
    IMPORT("${common.button.import}"),
    EXPORT("${common.button.export}"),
    UPLOAD("${common.button.upload}"),
    DOWNLOAD("${common.button.download}"),
    INSTALL("${common.button.install}"),
    AUTHORIZE("${common.button.authorize}");

    private String label;

    OperateType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
