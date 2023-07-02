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

package io.innospots.base.data.ap;

import io.innospots.base.constant.ServiceConstant;
import io.innospots.base.model.response.InnospotResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * queue data sender access point
 *
 * @author Smars
 * @date 2021/5/3
 */
public interface IDataSenderPoint {


    /**
     * send data
     *
     * @param datasourceId
     * @param tableName
     * @param data
     * @return
     */
    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/send/{datasourceId}/{tableName}")
    InnospotResponse<Map<String, Object>> send(
            @PathVariable("datasourceId") Integer datasourceId,
            @PathVariable("tableName") String tableName,
            @RequestBody Map<String, Object> data
    );

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/send-batch/{credentialId}/{tableName}")
    InnospotResponse<Map<String, Object>> sendBatch(
            @PathVariable("credentialId") Integer datasourceId,
            @PathVariable("tableName") String tableName,
            @RequestBody List<Map<String, Object>> data
    );


}
