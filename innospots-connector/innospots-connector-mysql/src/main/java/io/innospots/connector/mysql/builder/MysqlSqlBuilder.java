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

package io.innospots.connector.mysql.builder;

import datart.data.provider.calcite.dialect.MysqlSqlStdOperatorSupport;
import datart.provider.StdSqlOperator;
import io.innospots.data.provider.ISqlScriptBuilder;
import org.apache.calcite.sql.SqlDialect;

import java.util.Set;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/10/10
 */
public class MysqlSqlBuilder implements ISqlScriptBuilder {

    private final SqlDialect mysqlSqlStdOperatorSupport = new MysqlSqlStdOperatorSupport();

    @Override
    public String dbType() {
        return "MYSQL";
    }

    @Override
    public SqlDialect getSqlDialect() {
        return mysqlSqlStdOperatorSupport;
    }

    @Override
    public Set<StdSqlOperator> supportedStdFunctions() {
        return ((MysqlSqlStdOperatorSupport) mysqlSqlStdOperatorSupport).supportedOperators();
    }

}
