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

package io.innospots.base.configuration;

import com.zaxxer.hikari.HikariDataSource;
import io.innospots.base.data.operator.jdbc.JdbcDataOperator;
import io.innospots.base.data.operator.jdbc.JdbcSqlOperator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Raydian
 * @date 2021/1/10
 */
@EnableConfigurationProperties(InnospotDbProperties.class)
@Configuration
public class DatasourceConfiguration {


    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(InnospotDbProperties dbProperties) {
        return new HikariDataSource(dbProperties);
    }


    @Bean
    @ConditionalOnMissingBean(JdbcSqlOperator.class)
    public JdbcSqlOperator defaultSqlOperator(DataSource dataSource) {
        return new JdbcSqlOperator(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(JdbcDataOperator.class)
    public JdbcDataOperator defaultDataOperator(DataSource dataSource) {
        return new JdbcDataOperator(dataSource);
    }

}
