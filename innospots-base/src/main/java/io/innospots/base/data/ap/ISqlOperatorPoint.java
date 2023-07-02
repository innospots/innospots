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
import io.innospots.base.model.DataBody;
import io.innospots.base.model.PageBody;
import io.innospots.base.model.response.InnospotResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 以sql语句的方式执行数据操作, 查询语句均放在post方法体中
 *
 * @author Smars
 * @date 2021/5/3
 */
public interface ISqlOperatorPoint {

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/sql/query-object/{datasourceId}")
    InnospotResponse<DataBody<Map<String, Object>>> queryForObject(
            @PathVariable("datasourceId") Integer datasourceId,
            @RequestBody String sql
    );

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/sql/query-list/{datasourceId}")
    InnospotResponse<PageBody> queryForList(
            @PathVariable("datasourceId") Integer datasourceId,
            @RequestBody String sql
    );

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/sql/query-list/{datasourceId}/{page}/{size}")
    InnospotResponse<PageBody> queryForList(
            @PathVariable("datasourceId") Integer datasourceId,
            @PathVariable("page") int page,
            @PathVariable("size") int size,
            @RequestBody String sql
    );

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/sql/exec/{datasourceId}")
    InnospotResponse<Integer> executeForSql(
            @PathVariable("datasourceId") Integer datasourceId,
            @RequestBody String sql
    );

    @PostMapping(ServiceConstant.PATH_ROOT_DATA_API + "/sql/exec-batch/{datasourceId}")
    InnospotResponse<Integer> executeForSqlBatch(
            @PathVariable("datasourceId") Integer datasourceId,
            @RequestBody List<String> sql
    );

}
