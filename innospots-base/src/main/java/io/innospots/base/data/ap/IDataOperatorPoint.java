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

import io.innospots.base.condition.Factor;
import io.innospots.base.constant.ServiceConstant;
import io.innospots.base.data.operator.UpdateItem;
import io.innospots.base.model.DataBody;
import io.innospots.base.model.PageBody;
import io.innospots.base.model.response.InnospotResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据的增删改查操作
 *
 * @author Smars
 * @date 2021/5/3
 */
public interface IDataOperatorPoint {


    /**
     * 查询列表
     *
     * @param credentialId
     * @param tableName
     * @param condition
     * @param page
     * @param size
     * @return
     */
    @GetMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/query-list/{credentialId}/{tableName}/{page}/{size}")
    InnospotResponse<PageBody<Map<String, Object>>> queryForList(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @RequestBody List<Factor> condition,
            @PathVariable("page") int page,
            @PathVariable("size") int size);

    /**
     * 查询单条
     *
     * @param credentialId
     * @param tableName
     * @param condition
     * @return
     */
    @GetMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/query-object/{credentialId}/{tableName}")
    InnospotResponse<DataBody<Map<String, Object>>> queryForObject(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            List<Factor> condition);


    /**
     * 插入数据
     *
     * @param credentialId
     * @param tableName
     * @param data
     * @return
     */
    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/insert/{credentialId}/{tableName}")
    InnospotResponse<Integer> insert(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @RequestBody Map<String, Object> data
    );

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/insert-batch/{credentialId}/{tableName}")
    InnospotResponse<Integer> insertBatch(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @RequestBody List<Map<String, Object>> data
    );

    @PutMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/upsert/{credentialId}/{tableName}/{keyColumn}")
    InnospotResponse<Integer> upsert(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @PathVariable("keyColumn") String keyColumn,
            @RequestBody Map<String, Object> data
    );

    @PutMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/upsert-batch/{credentialId}/{tableName}/{keyColumn}")
    InnospotResponse<Integer> upsertForBatch(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @PathVariable("keyColumn") String keyColumn,
            @RequestBody List<Map<String, Object>> data
    );

    @PutMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/update/{credentialId}/{tableName}")
    InnospotResponse<Integer> update(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @RequestBody UpdateItem item
    );

    @PutMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/update-batch/{credentialId}/{tableName}")
    InnospotResponse<Integer> updateForBatch(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @RequestBody List<UpdateItem> items
    );


    @DeleteMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/delete/{credentialId}/{tableName}")
    InnospotResponse<Integer> delete(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @RequestBody List<Factor> condition
    );

    @DeleteMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/delete-batch/{credentialId}/{tableName}")
    InnospotResponse<Integer> deleteBatch(
            @PathVariable("credentialId") Integer credentialId,
            @PathVariable("tableName") String tableName,
            @RequestBody List<Factor> condition
    );

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/opt/execute")
    InnospotResponse<DataBody> execute(@RequestBody io.innospots.base.model.RequestBody requestBody);


}
