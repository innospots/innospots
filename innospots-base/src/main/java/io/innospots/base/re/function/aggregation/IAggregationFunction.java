package io.innospots.base.re.function.aggregation;

import io.innospots.base.model.field.ParamField;
import io.innospots.base.re.IExpression;

import java.util.Collection;
import java.util.Map;

/**
 * @author Smars
 * @date 2023/8/22
 */
public interface  IAggregationFunction {


    Object compute(Collection<Map<String, Object>> items);

    boolean match(Map<String,Object> item);

    String summaryFieldCode();

    void initialize(ParamField summaryField, IExpression<?> condition);

    default Double value(Map<String, Object> item) {
        try {
            Object v = item.getOrDefault(summaryFieldCode(), 0d);
            if (v instanceof Double) {
                return (double) v;
            }else if(v instanceof Number){
                return ((Number) v).doubleValue();
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
