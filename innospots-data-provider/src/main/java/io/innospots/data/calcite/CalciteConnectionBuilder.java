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

//import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.config.NullCollation;
import org.apache.calcite.jdbc.CalciteConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Smars
 * @date 2021/10/23
 */
@Slf4j
public class CalciteConnectionBuilder {


    public static CalciteConnection buildCalciteConnection() {
        return null;
    }

    public static CalciteConnection buildCalciteConnection(String schema, String tableName, List<Map<String, Object>> items) {
        try {
            Class.forName("org.apache.calcite.jdbc.Driver");

            Properties info = new Properties();
//            info.setProperty("lex", "JAVA");
            info.setProperty(CalciteConnectionProperty.DEFAULT_NULL_COLLATION.camelName(), NullCollation.LAST.name());
            info.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), "false");
            Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
            CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            calciteConnection.getRootSchema().add(schema, MemorySchema.create(tableName, items));
            return calciteConnection;
        } catch (ClassNotFoundException | SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /*
    public static void main(String[] args) throws Exception {
//        final JdbcMeta jdbcMeta = new JdbcMeta("jdbc:hsqldb:res:scott",
//                "SCOTT", "SCOTT");
//        LocalService service = new LocalService(jdbcMeta);
//
//        HttpServer server = new HttpServer.Builder()
//                .withHandler(service, Driver.Serialization.PROTOBUF)
//                .withPort(8687)
//                .build();
//        server.start();
//
//        Thread.sleep(1000000L);

        Class.forName("org.apache.calcite.jdbc.Driver");
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();

        Class.forName("com.mysql.cj.jdbc.Driver");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/any_dw_ds_101");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        Schema schema = JdbcSchema.create(rootSchema, "hr", dataSource,
                null, "name");

        schema.getTableNames().forEach(s -> System.out.println(s));

    }

     */
}
