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

package io.innospots.workflow.core.execution.node;

import io.innospots.base.json.JSONUtils;
import io.innospots.base.model.field.ParamField;
import io.innospots.workflow.core.execution.ExecutionInput;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static io.innospots.base.model.field.FieldValueType.convertJavaTypeByValue;

/**
 * @author Smars
 * @date 2021/5/10
 */
@Getter
@Setter
@Schema(title = "Display of Node Execution ")
public class NodeExecutionDisplay {

    private String nodeKey;

    private String nodeExecutionId;

    private String flowExecutionId;

    @Schema(title = "input data")
    private List<ExecutionInput> inputs;
//    private List<Map<String, Object>> inputs;

    /**
     * result table data
     */
    @Schema(title = "execution output results array")
    private List<NodeOutput> outputs = new ArrayList<>();

    @Schema(title = "output columns fields")
    protected List<ParamField> outputFields;

    @Schema(title = "execution output")
    private Map<String, Object> logs = new LinkedHashMap<>();

    public static NodeExecutionDisplay build(NodeExecution nodeExecution) {
        if (nodeExecution == null) {
            return null;
        }
        NodeExecutionDisplay executionDisplay = new NodeExecutionDisplay();
        executionDisplay.flowExecutionId = nodeExecution.getFlowExecutionId();
        nodeExecution.setNodeKey(nodeExecution.getNodeKey());
        nodeExecution.setNodeExecutionId(nodeExecution.getNodeExecutionId());
//        executionDisplay.inputs = nodeExecution.flatInput();
        executionDisplay.inputs = nodeExecution.getInputs();
        executionDisplay.logs.put("nodeExecutionId", nodeExecution.getNodeExecutionId());
        executionDisplay.logs.put("nodeKey", executionDisplay.nodeKey);
        executionDisplay.logs.put("status", nodeExecution.getStatus());
        executionDisplay.logs.put("consume", nodeExecution.consume());
        executionDisplay.logs.put("startTime", nodeExecution.getStartTime());
        executionDisplay.logs.put("endTime", nodeExecution.getEndTime());
        executionDisplay.logs.put("sequence", nodeExecution.getSequenceNumber());
        executionDisplay.logs.put("output_table", JSONUtils.toJsonString(nodeExecution.outputLog()));
        executionDisplay.logs.put("message", nodeExecution.getMessage());
        executionDisplay.outputs = nodeExecution.getOutputs();
        executionDisplay.buildOutputField();

        return executionDisplay;
    }


    public void addLog(String key, Object value) {
        this.logs.put(key, value);
    }


    private void buildOutputField() {
        outputFields = new ArrayList<>();
        //all output data have the save fields
        if (outputs != null && !outputs.isEmpty()) {
            List<Map<String, Object>> listResult = outputs.get(0).getResults();
            if (!listResult.isEmpty()) {
                Map<String, Object> data = listResult.get(0);
                for (String key : data.keySet()) {
                    Object v = data.get(key);
                    ParamField pf = new ParamField(key, key, convertJavaTypeByValue(v));
                    if (v instanceof Map) {
                        //the value is map object
                        pf.setSubFields(parseFieldFromValue(pf.getCode(), (Map<?, ?>) v));
                    } else if (v instanceof Collection) {
                        Object obj = ((Collection<?>) v).stream().findFirst().orElse(null);
                        if (obj instanceof Map) {
                            pf.setSubFields(parseFieldFromValue(pf.getCode(), (Map<?, ?>) obj));
                        }
                    }
                    outputFields.add(pf);
                }
            }
        }
    }

    private List<ParamField> parseFieldFromValue(String parentCode, Map<?, ?> value) {
        List<ParamField> subFields = new ArrayList<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            String k = entry.getKey().toString();
            ParamField subField = new ParamField(k, k, convertJavaTypeByValue(entry.getValue()));
            subField.setParentCode(parentCode);
            subFields.add(subField);
        }
        return subFields;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("inputs=").append(inputs);
        sb.append(", outputs=").append(outputs);
        sb.append(", outputFields=").append(outputFields);
        sb.append(", logs='").append(logs).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
