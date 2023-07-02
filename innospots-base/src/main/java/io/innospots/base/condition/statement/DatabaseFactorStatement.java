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

package io.innospots.base.condition.statement;

import io.innospots.base.condition.Factor;
import io.innospots.base.condition.Mode;
import io.innospots.base.condition.Opt;
import io.innospots.base.model.field.FieldValueType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * database sql clause statement convertor
 *
 * @author Smars
 * @date 2021/8/11
 */
public class DatabaseFactorStatement implements IFactorStatement {

    private Mode mode = Mode.DB;

    @Override
    public String normalizeValue(Object value, FieldValueType valueType) {
        String v = "";
        if (value != null) {
            if (valueType.isNumber() || FieldValueType.FIELD_CODE == valueType) {
                v = value.toString();
            } else if (valueType == FieldValueType.DATE) {
                if (value instanceof Date) {
                    v = StringUtils.wrap(DateFormatUtils.format((Date) value, "yyyy-MM-dd"), "'");
                } else if (value instanceof LocalDate) {
                    v = StringUtils.wrap(((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE), "'");
                } else {
                    v = StringUtils.wrap(value.toString(), "'");
                }
            } else if (valueType == FieldValueType.DATE_TIME) {
                if (value instanceof Date) {
                    v = StringUtils.wrap(DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss"), "'");
                } else if (value instanceof LocalDateTime) {
                    v = StringUtils.wrap(((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "'");
                } else {
                    v = StringUtils.wrap(value.toString(), "'");
                }
            } else {
                //remove Single quotation mark  "'"
                value = value.toString().replaceAll("'", "");
                value = value.toString().replaceAll("\\\\", "\\\\\\\\");
                v = "'" + value + "'";
            }
        }
        return v;
    }

    @Override
    public Object normalizeValue(Factor factor) {
        return normalizeValue(factor.getValue(), factor.getValueType());
    }

    @Override
    public String statement(Factor factor) {

        String v = "";
        if (factor.getOpt() == Opt.IN) {
            if (factor.getValue() instanceof Collection) {
                v += " IN (";
                List<String> list = new ArrayList<>();
                for (Object vv : (Collection<?>) factor.getValue()) {
                    list.add(String.valueOf(normalizeValue(vv, factor.getValueType())));
                }//end for
                v += String.join(",", list);
                v += ")";
            }
        } else if (factor.getOpt() == Opt.BETWEEN) {
            if (factor.getValue() instanceof Collection) {
                int count = 0;
                String s = null;
                String e = null;
                for (Object vv : (Collection<?>) factor.getValue()) {
                    if (count == 0) {
                        s = String.valueOf(normalizeValue(vv, factor.getValueType()));
                    } else if (count == 1) {
                        e = String.valueOf(normalizeValue(vv, factor.getValueType()));
                    }
                    count++;
                }//end for
                v += " BETWEEN ";
                v += s;
                v += " AND ";
                v += e;
            }
        } else {
            v = " " + factor.getOpt().symbol(mode) + " " + normalizeValue(factor.getValue(), factor.getValueType()) + "";
        }

        return factor.getCode() + v;
    }
}
