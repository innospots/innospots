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

package io.innospots.base.data.operator.jdbc;

import io.innospots.base.data.operator.ISqlOperator;
import io.innospots.base.data.schema.SchemaColumn;
import io.innospots.base.exception.data.SqlDataException;
import io.innospots.base.model.DataBody;
import io.innospots.base.model.PageBody;
import io.innospots.base.model.SqlDataPageBody;
import io.innospots.base.model.response.InnospotResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.sql.DataSource;
import java.sql.JDBCType;
import java.util.*;


/**
 * @author Smars
 * @date 2021/5/5
 */
public class JdbcSqlOperator implements ISqlOperator {

    protected static final String LIMIT = "limit ";

    protected static final String SEPARATOR = ",";

    protected JdbcTemplate jdbcTemplate;


    public JdbcSqlOperator(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public InnospotResponse<DataBody<Map<String, Object>>> selectForObject(String sql) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        DataBody<Map<String, Object>> dataBody = new DataBody<>(CollectionUtils.isNotEmpty(result) ? result.get(0) : Collections.emptyMap());
        return InnospotResponse.success(dataBody);
    }

    @Override
    public InnospotResponse<PageBody> selectForList(String sql) {
        List<Map<String, Object>> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        SqlRowSetMetaData metaData = rowSet.getMetaData();
        List<SchemaColumn> schemaColumns = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            schemaColumns.add(new SchemaColumn(
                    metaData.getColumnName(i),
                    JDBCType.valueOf(metaData.getColumnType(i))
            ));
        }

        while (rowSet.next()) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (SchemaColumn schemaColumn : schemaColumns) {
                map.put(schemaColumn.getColumnName(), rowSet.getObject(schemaColumn.getColumnName()));
            }
            result.add(map);
        }

        SqlDataPageBody<Map<String, Object>> pageBody = new SqlDataPageBody<>();
        pageBody.setSchemaColumns(schemaColumns);
        pageBody.setList(result);
        return InnospotResponse.success(pageBody);
    }

    @Override
    public InnospotResponse<PageBody> selectForList(String sql, int page, int size) {
        String limit = LIMIT + (page - 1) * size + SEPARATOR + size;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql + limit);
        PageBody pageBody = new PageBody();
        pageBody.setList(result);
        pageBody.setCurrent((long) page);
        pageBody.setPageSize((long) size);
        return InnospotResponse.success(pageBody);
    }

    @Override
    public InnospotResponse<Integer> executeForSql(String sql) {
        int insert = 1;
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            throw SqlDataException.buildException(this.getClass(), "Execute sql fail", e);
        }
        return InnospotResponse.success(insert);
    }

    @Override
    public InnospotResponse<Integer> executeForSqlBatch(List<String> sql) {
        int[] execute;
        try {
            String[] sqlList = new String[sql.size()];
            sql.toArray(sqlList);
            execute = jdbcTemplate.batchUpdate(sqlList);
        } catch (Exception e) {
            throw SqlDataException.buildException(this.getClass(), "Batch execute sql fail", e);
        }
        return InnospotResponse.success(execute.length);
    }
}
