package io.innospots.workflow.node.app.utils;

import io.innospots.base.condition.EmbedCondition;
import io.innospots.base.condition.Mode;
import io.innospots.base.exception.ConfigException;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.re.IExpression;
import io.innospots.base.re.aviator.AviatorExpression;
import io.innospots.base.utils.BeanUtils;
import io.innospots.workflow.core.node.field.NodeParamField;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import io.innospots.workflow.node.app.execute.AggregationComputeField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Smars
 * @date 2023/8/19
 */
public class NodeInstanceUtils {

    public static NodeParamField buildParamField(NodeInstance nodeInstance,String fieldName){
        NodeParamField nodeParamField = null;
        Map<String, Object> listFieldValue = (Map<String, Object>) nodeInstance.value(fieldName);
        if(listFieldValue != null){
            nodeParamField = BeanUtils.toBean(listFieldValue, NodeParamField.class);
        }
        return nodeParamField;
    }

    public static List<NodeParamField> buildParamFields(NodeInstance nodeInstance,String fieldName){
        List<NodeParamField> nodeParamFields = null;
        List<Map<String, Object>> fields = (List<Map<String, Object>>) nodeInstance.value(fieldName);
        if(fields!=null){
            nodeParamFields = BeanUtils.toBean(fields, NodeParamField.class);
        }
        return nodeParamFields;
    }

    public static <T> IExpression<T> buildExpression(NodeInstance nodeInstance, String fieldName, BaseAppNode appNode){
        EmbedCondition embedCondition = buildCondition(nodeInstance,fieldName,appNode);
        IExpression expression = null;
        if(embedCondition!=null){
            String exp = embedCondition.getStatement();
            if(StringUtils.isNotEmpty(exp)){
                expression = new AviatorExpression(exp, null);
            }
        }
        return expression;
    }

    public static EmbedCondition buildCondition(NodeInstance nodeInstance, String fieldName, BaseAppNode appNode) {
        Object v = nodeInstance.getData().get(fieldName);
        EmbedCondition condition = null;
        if (v == null) {
            return null;
        }
        try {
            condition = JSONUtils.parseObject((Map<String, Object>)v, EmbedCondition.class);
            if (condition == null) {
                return null;
            }
            condition.setMode(Mode.SCRIPT);
            condition.initialize();
        } catch (Exception e) {
            throw ConfigException.buildTypeException(appNode.getClass(), "if template invalid format, nodeKey:" + appNode.nodeKey() + ", error: " + e.getMessage());
        }

        return condition;
    }

    public static List<AggregationComputeField> buildAggregationComputeFields(NodeInstance nodeInstance,String fieldName) {
        List<Map<String, Object>> fieldMaps = (List<Map<String, Object>>) nodeInstance.value(fieldName);

        List<AggregationComputeField> computeFields = new ArrayList<>();
        if (CollectionUtils.isEmpty(fieldMaps)) {
            return computeFields;
        }
        for (Map<String, Object> fieldMap : fieldMaps) {
            AggregationComputeField cf = AggregationComputeField.build(fieldMap);
            cf.initialize();
            computeFields.add(cf);
        }
        return computeFields;
    }
}
