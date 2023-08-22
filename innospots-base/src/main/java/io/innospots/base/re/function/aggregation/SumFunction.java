package io.innospots.base.re.function.aggregation;

import org.apache.commons.math3.stat.descriptive.summary.Sum;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * @author Smars
 * @date 2023/8/22
 */
public class SumFunction extends AbstractAggregationFunction{

    @Override
    public Object compute(Collection<Map<String, Object>> items) {
        Double sumVal = stcCompute(new Sum(),items);
        Object v = 0;
        switch (summaryField.getValueType()) {
            case DECIMAL:
                v = BigDecimal.valueOf(sumVal);
                break;
            case DOUBLE:
                v = sumVal;
                break;
            case INTEGER:
            case NUMBER:
                v = sumVal.intValue();
                break;
            default:
                double e = sumVal - sumVal.intValue();
                if (e == 0) {
                    v = sumVal.intValue();
                }
        }
        return v;
    }
}
