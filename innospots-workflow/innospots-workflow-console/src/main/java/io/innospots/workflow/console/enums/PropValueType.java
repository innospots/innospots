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

package io.innospots.workflow.console.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Raydian
 * @date 2020/11/28
 */
@Getter
public enum PropValueType {

    /**
     *
     */
    STRING(0, "string character", String.class, "VARCHAR"),
    INTEGER(1, "integer 32bit", Integer.class, "INT"),
    BOOLEAN(2, "boolean", Boolean.class, "INT"),
    LONG(3, "long integer 64bit", Long.class, "BIGINT"),
    DATE(4, "year-month-date", LocalDate.class, "DATE"),
    DATE_TIME(5, "year month date and timing on second", LocalDateTime.class, "DATETIME"),
    DOUBLE(6, "double", Double.class, "DOUBLE"),
    CURRENCY(7, "currency", BigDecimal.class, "DECIMAL"),
    TIMESTAMP(9, "long timestamp", Timestamp.class, "TIMESTAMP"),
    YEAR_MONTH(13, "yyyyMM", Integer.class, "INT"),
    YEAR_MONTH_DATE(14, "yyyyMMdd", Integer.class, "INT"),
    MONTH(15, "MM", Integer.class, "INT"),
    MONTH_DATE(16, "MMdd", Integer.class, "INT"),
    DAY_OF_MONTH(17, "dd", Integer.class, "INT");


    private Class<?> clazz;

    private Integer id;

    private String description;

    private String dbFieldType;

    PropValueType(Integer id, String description, Class<?> clazz, String dbFieldType) {
        this.id = id;
        this.description = description;
        this.clazz = clazz;
        this.dbFieldType = dbFieldType;
    }

}
