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

package io.innospots.data.provider;

import datart.SelectColumn;
import datart.ViewExecuteParam;
import datart.base.exception.SqlParseError;
import datart.data.provider.calcite.SqlParserUtils;
import datart.data.provider.script.SqlScriptRender;
import datart.provider.*;
import datart.provider.script.ScriptRender;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/10/10
 */
public interface ISqlScriptBuilder {

    String COUNT_SQL = "SELECT COUNT(*) FROM (%s) V_T";

    String dbType();

    SqlDialect getSqlDialect();

    Set<StdSqlOperator> supportedStdFunctions();

    default Boolean functionValidate(String snippet) {
        try {
            SqlParserUtils.parseSnippet(snippet);
        } catch (SqlParseException e) {
            SqlParseError parseError = new SqlParseError(e);
            parseError.setDbType(dbType());
            parseError.setSql(snippet);
            throw parseError;
        }
        return true;
    }

    default ScriptRender getScriptRender(ViewExecuteParam viewExecuteParam) {

        Set<SelectColumn> columns = Collections.singleton(SelectColumn.of(null, "*"));
        List<ScriptVariable> variables = viewExecuteParam.getScriptVariables();

        QueryScript queryScript = QueryScript.builder()
                .test(false)
                .script(viewExecuteParam.getScript())
                .scriptType(ScriptType.SQL)
                .variables(variables)
                .schema(viewExecuteParam.getModel())
                .build();

        if (viewExecuteParam.getPageInfo().getPageNo() < 1) {
            viewExecuteParam.getPageInfo().setPageNo(1);
        }

        viewExecuteParam.getPageInfo().setPageSize(Math.min(viewExecuteParam.getPageInfo().getPageSize(), Integer.MAX_VALUE));

        ExecuteParam queryParam = ExecuteParam.builder()
                .columns(viewExecuteParam.getColumns())
                .keywords(viewExecuteParam.getKeywords())
                .functionColumns(viewExecuteParam.getFunctionColumns())
                .aggregators(viewExecuteParam.getAggregators())
                .filters(viewExecuteParam.getFilters())
                .groups(viewExecuteParam.getGroups())
                .orders(viewExecuteParam.getOrders())
                .pageInfo(viewExecuteParam.getPageInfo())
                .includeColumns(columns)
                .concurrencyOptimize(viewExecuteParam.isConcurrencyControl())
                .cacheEnable(viewExecuteParam.isCache())
                .cacheExpires(viewExecuteParam.getCacheExpires())
                .build();

        ScriptRender render = buildScriptRender(queryScript, queryParam);
        return render;
    }

    default ScriptRender buildScriptRender(QueryScript queryScript, ExecuteParam queryParam) {
        return new SqlScriptRender(queryScript
                , queryParam
                , this.getSqlDialect()
                , false);
    }

    default String buildCountSql(ViewExecuteParam viewExecuteParam) {
        SqlScriptRender render = (SqlScriptRender) this.getScriptRender(viewExecuteParam);
        String countSql;
        try {
            countSql = render.render(true, false, true);
        } catch (SqlParseException e) {
            SqlParseError parseError = new SqlParseError(e);
            parseError.setDbType(dbType());
            parseError.setSql(viewExecuteParam.getScript());
            throw parseError;
        }
        return String.format(COUNT_SQL, countSql);
    }

    default String buildSql(ViewExecuteParam viewExecuteParam) {
        SqlScriptRender render = (SqlScriptRender) this.getScriptRender(viewExecuteParam);
        String sql;
        try {
            if (viewExecuteParam.getPageInfo() != null) {
                sql = render.render(true, true, false);
            } else {
                sql = render.render(true, false, false);
            }
        } catch (Exception e) {
            SqlParseError parseError = new SqlParseError(e);
            parseError.setDbType(dbType());
            parseError.setSql(viewExecuteParam.getScript());
            throw parseError;
        }
        return sql;
    }

}
