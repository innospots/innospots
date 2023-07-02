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

import io.innospots.base.utils.BeanUtils;
import io.innospots.workflow.core.execution.ExecutionInput;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeOutput;
import io.innospots.workflow.core.node.NodeParamField;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.*;

/**
 * @author Smars
 * @date 2021/3/16
 */
@Slf4j
public class AggregationNode extends BaseAppNode {


    private NodeParamField dimensionField;
    private NodeParamField listField;

    private List<AggregationComputeField> computeFields;

    public static final String FIELD_AGGREGATE = "aggregate_field";
    public static final String FIELD_DIMENSION_PAYLOAD = "dim_field_payload";
    public static final String FIELD_DIMENSION_LIST = "dim_field_list";
    public static final String FIELD_SOURCE_TYPE = "source_field_type";
    public static final String FIELD_PARENT_LIST = "list_parent_field";

    private String sourceFieldType;


    @Override
    protected void initialize(NodeInstance nodeInstance) {
        super.initialize(nodeInstance);
        validFieldConfig(nodeInstance, FIELD_SOURCE_TYPE);
        validFieldConfig(nodeInstance, FIELD_AGGREGATE);
        sourceFieldType = nodeInstance.valueString(FIELD_SOURCE_TYPE);
        if("payload".equals(sourceFieldType)){
            Map<String, Object> field = (Map<String, Object>) nodeInstance.value(FIELD_DIMENSION_PAYLOAD);
            if(field!=null){
                dimensionField = BeanUtils.toBean(field, NodeParamField.class);
            }
        }else if("list".equals(sourceFieldType)){
            Map<String, Object> field = (Map<String, Object>) nodeInstance.value(FIELD_DIMENSION_LIST);
            if(field!=null){
                dimensionField = BeanUtils.toBean(field, NodeParamField.class);
            }
            Map<String, Object> listFieldValue = (Map<String, Object>) nodeInstance.value(FIELD_PARENT_LIST);
            if(listFieldValue != null){
                listField = BeanUtils.toBean(listFieldValue, NodeParamField.class);
            }
        }

        computeFields = buildFields(nodeInstance);

    }


    @Override
    public void invoke(NodeExecution nodeExecution) {
        NodeOutput nodeOutput = new NodeOutput();
        nodeOutput.addNextKey(this.nextNodeKeys());
        nodeExecution.addOutput(nodeOutput);
        List<Map<String, Object>> items = new ArrayList<>();
        ArrayListValuedHashMap<String, Map<String, Object>> groupItems = new ArrayListValuedHashMap<>();
        for (ExecutionInput executionInput : nodeExecution.getInputs()) {
            for (Map<String, Object> item : executionInput.getData()) {
                groupItems.put(String.valueOf(item.get(dimensionField.getCode())), item);
            }//end for item
        }//end for execution input
        for (Map.Entry<String, Collection<Map<String, Object>>> entry : groupItems.asMap().entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put(dimensionField.getCode(), entry.getKey());
            for (AggregationComputeField computeField : computeFields) {
                item.put(computeField.getCode(), computeField.compute(entry.getValue()));
            }
            items.add(item);
        }
        nodeOutput.setResults(items);
    }


    public static List<AggregationComputeField> buildFields(NodeInstance nodeInstance) {
        List<Map<String, Object>> fieldMaps = (List<Map<String, Object>>) nodeInstance.value(FIELD_AGGREGATE);

        List<AggregationComputeField> computeFields = new ArrayList<>();
        if (CollectionUtils.isEmpty(fieldMaps)) {
            return computeFields;
        }
        for (Map<String, Object> fieldMap : fieldMaps) {
            AggregationComputeField cf = BeanUtils.toBean(fieldMap, AggregationComputeField.class);
            cf.initialize();
            computeFields.add(cf);
        }
        return computeFields;
    }


}
