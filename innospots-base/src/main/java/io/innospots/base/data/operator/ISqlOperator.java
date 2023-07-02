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

package io.innospots.base.data.operator;

import io.innospots.base.model.DataBody;
import io.innospots.base.model.PageBody;
import io.innospots.base.model.response.InnospotResponse;

import java.util.List;
import java.util.Map;

/**
 * 以sql语句的方式执行数据操作, 查询语句均放在post方法体中
 *
 * @author Smars
 * @date 2021/5/3
 */
public interface ISqlOperator extends IOperator {

    InnospotResponse<DataBody<Map<String, Object>>> selectForObject(String sql);

    InnospotResponse<PageBody> selectForList(String sql);

    InnospotResponse<PageBody> selectForList(String sql, int page, int size);

    InnospotResponse<Integer> executeForSql(String sql);

    InnospotResponse<Integer> executeForSqlBatch(List<String> sql);

}
