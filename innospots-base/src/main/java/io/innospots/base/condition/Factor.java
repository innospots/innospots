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

package io.innospots.base.condition;

import io.innospots.base.model.field.FieldValueType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Raydian
 * @version 1.0.0
 * @date 2020/11/3
 */
@Getter
@Setter
public class Factor {

    private String name;
    private String code;
    private Opt opt;
    private Object value;
    private FieldValueType valueType;


    public Factor(String code, Opt opt, Object value, FieldValueType valueType) {
        this.code = code;
        this.opt = opt;
        this.value = value;
        this.valueType = valueType;
    }

    public Factor() {
    }

    public Object value(Map<String, Object> input) {
        if (MapUtils.isEmpty(input)) {
            return null;
        }
        Object val = null;
        if (value instanceof String && ((String) value).startsWith("${")) {
            val = input.get(((String) value).substring(2, ((String) value).length() - 1));
        } else if (valueType == FieldValueType.FIELD_CODE) {
            val = input.get(value);
        } else {
            val = value;
        }
        return val;
    }

    public String valueKey(){
        String val = null;
        if (value instanceof String && ((String) value).startsWith("${")) {
            val = ((String) value).substring(2, ((String) value).length() - 1);
        } else {
            val = String.valueOf(value);
        }
        return val;
    }

    public boolean checkNull() {
        if (opt == null || valueType == null || code == null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkNull(List<Factor> list) {
        if (list != null && list.size() > 0) {
            for (Factor factor : list) {
                if (factor.checkNull()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", opt=").append(opt);
        sb.append(", value=").append(value);
        sb.append(", valueType=").append(valueType);
        sb.append('}');
        return sb.toString();
    }
}
