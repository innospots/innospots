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

package io.innospots.workflow.node.app.execute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.innospots.base.condition.EmbedCondition;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.model.field.BaseField;
import io.innospots.base.model.field.FieldValueType;
import io.innospots.base.re.IExpression;
import io.innospots.base.re.aviator.AviatorExpression;
import io.innospots.base.re.function.aggregation.AggregationFunctionType;
import io.innospots.base.utils.BeanUtils;
import io.innospots.base.utils.Initializer;
import io.innospots.workflow.core.node.NodeParamField;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * aggregation compute field
 *
 * @author Smars
 * @date 2021/8/22
 */
@Getter
@Setter
@Slf4j
public class AggregationComputeField extends BaseField implements Initializer {


    private NodeParamField summaryField;

    private EmbedCondition condition;

    @JsonIgnore
    private IExpression<Object> conditionExpression;

    private AggregationFunctionType functionType;

    private String expr;

    public static AggregationComputeField build(Map<String,Object> fieldMap){
        AggregationComputeField computeField = new AggregationComputeField();
        computeField.name = (String) fieldMap.get("name");
        computeField.comment = (String) fieldMap.get("comment");
        computeField.code = (String) fieldMap.get("code");
        if(fieldMap.containsKey("valueType")){
            computeField.valueType = FieldValueType.valueOf((String) fieldMap.get("valueType"));
        }
        computeField.functionType = AggregationFunctionType.valueOf((String) fieldMap.get("functionType"));
        Map<String,Object> sf = (Map<String, Object>) fieldMap.get("summaryField");
        if(sf != null){
            computeField.summaryField = BeanUtils.toBean(sf,NodeParamField.class);
        }
        Map<String,Object> ct = (Map<String, Object>) fieldMap.get("condition");
        if(ct!=null){
            computeField.condition = JSONUtils.parseObject(ct, EmbedCondition.class);
        }

        return computeField;
    }


    @Override
    public void initialize() {
        if (condition != null) {
            condition.initialize();
            expr = condition.getStatement();
            if(StringUtils.isNotEmpty(expr)){
                conditionExpression = new AviatorExpression(expr, null);
            }
        }
        log.debug("field,{},{} expression:{},", code, name, expr);
    }

    public Object compute(Collection<Map<String, Object>> items) {
        Object val = null;
        try {
            switch (functionType) {
                case AVG:
                    val = avg(items);
                    break;
                case MAX:
                    val = max(items);
                    break;
                case MIN:
                    val = min(items);
                    break;
                case SUM:
                    val = sum(items);
                    break;
                case COUNT:
                    val = count(items);
                    break;
                case DISCOUNT:
                    val = distinctCount(items);
                    break;
                default:
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return val;
    }

    /**
     * population variance
     */
    private Double popVariance(Collection<Map<String, Object>> items) {
        double[] data = items.stream()
                .filter(this::matchItem)
                .mapToDouble(this::value).toArray();
        return varp(data);
    }

    private Double varp(double[] data) {
        double variance = 0;
        double avg = Arrays.stream(data).average().orElse(0);
        for (int i = 0; i < data.length; i++) {
            variance = variance + (Math.pow((data[i] - avg), 2));
        }
        variance = variance / data.length;
        return variance;
    }

    /**
     * population standard deviation 总体标准差
     *
     * @param data
     * @return
     */
    private double popStdDev(double[] data) {
        return Math.sqrt(varp(data));
    }

    /**
     * 样本方差
     *
     * @param data
     * @return
     */
    double variance(double[] data) {
        double variance = 0;
        double avg = Arrays.stream(data).average().orElse(0);
        for (int i = 0; i < data.length; i++) {
            variance = variance + (Math.pow((data[i] - avg), 2));
        }
        variance = variance / (data.length - 1);
        return variance;
    }

    /**
     * standard deviation 样本标准差
     */
    double stdDev(double[] data) {
        double std_dev;
        std_dev = Math.sqrt(variance(data));
        return std_dev;
    }

    private Object count(Collection<Map<String, Object>> items) {
        return items.stream().filter(this::matchItem).count();
    }

    private Object distinctCount(Collection<Map<String, Object>> items) {
        return items.stream().filter(this::matchItem)
                .map(item -> item.get(summaryField.getCode()))
                .distinct().count();
    }

    private Object min(Collection<Map<String, Object>> items) {
        return items.stream()
                .filter(this::matchItem)
                .map(item -> item.get(summaryField.getCode()))
                .min((o1, o2) -> {
                    if (o1 instanceof Comparable && o2 instanceof Comparable) {
                        return ((Comparable) o1).compareTo(o2);
                    }
                    return 0;
                }).orElse(null);
    }

    private Object middle(Collection<Map<String, Object>> items) {
        List<Object> list = items.stream()
                .filter(this::matchItem)
                .map(item -> item.get(summaryField.getCode())).sorted().collect(Collectors.toList());
        int ln = list.size();
        if (ln % 2 == 0) {
            return (Double.parseDouble(String.valueOf(list.get(ln / 2 - 1))) + Double.parseDouble((String) list.get(ln / 2))) / 2;
        } else {
            return list.get(list.size() / 2);
        }
    }

    private Object max(Collection<Map<String, Object>> items) {
        return items.stream()
                .filter(this::matchItem)
                .map(item -> item.get(summaryField.getCode()))
                .max((o1, o2) -> {
                    if (o1 instanceof Comparable && o2 instanceof Comparable) {
                        return ((Comparable) o1).compareTo(o2);
                    }
                    return 0;
                }).orElse(null);
    }

    private Object avg(Collection<Map<String, Object>> items) {
        Double avg = items.stream()
                .filter(this::matchItem)
                .mapToDouble(this::value).average().orElse(0);
        return avg;
    }

    private Object sum(Collection<Map<String, Object>> items) {
        Double sum = items.stream()
                .filter(this::matchItem)
                .mapToDouble(this::value).sum();
        Object v = 0;
        switch (summaryField.getValueType()) {
            case DECIMAL:
                v = BigDecimal.valueOf(sum);
                break;
            case DOUBLE:
                v = sum;
                break;
            case INTEGER:
            case NUMBER:
                v = sum.intValue();
                break;
            default:
                double e = sum - sum.intValue();
                if (e == 0) {
                    v = sum.intValue();
                }
        }
        return v;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", valueType=").append(valueType);
        sb.append(", expr='").append(expr).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private boolean matchItem(Map<String, Object> item) {
        if (conditionExpression != null) {
            return (Boolean) conditionExpression.execute(item);
        } else {
            return true;
        }
    }

    private Double value(Map<String, Object> item) {
        try {
            Object v = item.getOrDefault(summaryField.getCode(), 0d);
            if (v instanceof Double) {
                return (double) v;
            } else if(v !=null && v.toString().matches("[\\d]+[.]*[\\d]+")){
                return Double.parseDouble(v.toString());
            }else{
                return 0d;
            }
        }catch (Exception e){
            return 0d;
        }
    }


}
