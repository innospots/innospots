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

package io.innospots.data.calcite;

import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityHandler;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.jdbc.CalciteConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/10/23
 */
@Slf4j
public class MemoryQueryExecutor {

    private CalciteConnection calciteConnection;

    public MemoryQueryExecutor(CalciteConnection calciteConnection) {
        this.calciteConnection = calciteConnection;
    }

    public static MemoryQueryExecutor build(String schema, String tableName, List<Map<String, Object>> items) {
        return new MemoryQueryExecutor(CalciteConnectionBuilder.buildCalciteConnection(schema, tableName, items));
    }

    public Map<String, Object> queryForObject(String sql) throws SQLException {
        Entity entity = SqlExecutor.query(calciteConnection, sql, new EntityHandler());
        Map<String, Object> data = new HashMap<>();
        for (String fieldName : entity.getFieldNames()) {
            data.put(fieldName, entity.getObj(fieldName));
        }
        return data;
    }

    public List<Map<String, Object>> queryForList(String sql) throws SQLException {
        List<Entity> entities = SqlExecutor.query(calciteConnection, sql, new EntityListHandler());
        List<Map<String, Object>> results = new ArrayList<>();
        for (Entity entity : entities) {
            Map<String, Object> data = new HashMap<>();
            for (String fieldName : entity.getFieldNames()) {
                data.put(fieldName, entity.getObj(fieldName));
            }
            results.add(data);
        }//end for

        return results;
    }

    public void close() {
        try {
            calciteConnection.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }
}
