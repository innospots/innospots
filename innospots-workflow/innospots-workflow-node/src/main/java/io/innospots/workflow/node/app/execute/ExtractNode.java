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

import io.innospots.base.model.field.ParamField;
import io.innospots.base.utils.BeanUtils;
import io.innospots.workflow.core.execution.ExecutionInput;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeOutput;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

/**
 * @author Smars
 * @date 2021/3/16
 */
public class ExtractNode extends BaseAppNode {


    private ExtractMode extractMode;

    public static final String FIELD_EXTRACT_MODE = "extract_mode";
    public static final String FIELD_EXTRACT_FIELDS = "extract_fields";
    public static final String FIELD_EMBED_FIELDS = "embed_fields";

    private List<ParamField> extractFields;

    @Override
    protected void initialize(NodeInstance nodeInstance) {
        super.initialize(nodeInstance);
        validFieldConfig(nodeInstance, FIELD_EXTRACT_MODE);
        extractMode = ExtractMode.valueOf(nodeInstance.valueString(FIELD_EXTRACT_MODE));
        if (extractMode == ExtractMode.FIELD) {
            validFieldConfig(nodeInstance, FIELD_EXTRACT_FIELDS);
            List<Map<String, Object>> v = (List<Map<String, Object>>) nodeInstance.value(FIELD_EXTRACT_FIELDS);
            extractFields = BeanUtils.toBean(v, ParamField.class);
        } else if (extractMode == ExtractMode.EMBED) {
            validFieldConfig(nodeInstance, FIELD_EMBED_FIELDS);
            List<Map<String, Object>> v = (List<Map<String, Object>>) nodeInstance.value(FIELD_EMBED_FIELDS);
            extractFields = BeanUtils.toBean(v, ParamField.class);
        }


    }

    @Override
    public void invoke(NodeExecution nodeExecution) {
        NodeOutput nodeOutput = new NodeOutput();
        nodeOutput.addNextKey(this.nextNodeKeys());
        nodeExecution.addOutput(nodeOutput);
        Set<Map<String, Object>> uniSet = new LinkedHashSet<>();
        for (ExecutionInput executionInput : nodeExecution.getInputs()) {
            for (Map<String, Object> item : executionInput.getData()) {
                Map<String, Object> oItem = item;
                switch (extractMode) {
                    case REMOVE_EMPTY:
                        if (MapUtils.isEmpty(oItem)) {
                            continue;
                        } else {
                            uniSet.add(oItem);
                        }
                        break;
                    case REMOVE_DUPLICATE:
                        uniSet.add(oItem);
                        break;
                    case FIELD:
                    case EMBED:
                    default:
                        if (extractFields == null) {
                            continue;
                        }
                        if (extractFields.size() > 1) {
                            oItem = new LinkedHashMap<>(extractFields.size());
                            for (ParamField extractField : extractFields) {
                                Object v = item.get(extractField.getCode());

                                oItem.put(extractField.getCode(), v);
                            }
                            uniSet.add(oItem);
                        } else {
                            Object eItem = extractValue(extractFields.get(0), oItem);
                            if (eItem instanceof Map) {
                                uniSet.add((Map<String, Object>) eItem);
                            } else if (eItem instanceof Collection) {
                                uniSet.addAll((Collection<? extends Map<String, Object>>) eItem);
                            }
                        }
                        break;
                }//end switch

            }//end for

        }//end for input
        nodeOutput.setResults(new ArrayList<>(uniSet));
    }

    private Object extractValue(ParamField extractField, Map<String, Object> item) {
        Object rValue = null;
        Map<String, Object> eItem = new LinkedHashMap<>(extractFields.size());
        Object value = item.get(extractField.getCode());
        if (value instanceof Map) {
            eItem.putAll((Map<? extends String, ?>) value);
        } else if (value instanceof Collection) {
            List<Map<String, Object>> list = new ArrayList<>();
            boolean mapData = true;
            for (Object v : (Collection) value) {
                if (v instanceof Map) {
                    list.add((Map<String, Object>) v);
                } else {
                    mapData = false;
                }
            }//end for
            if (!mapData) {
                eItem.put(extractField.getCode(), value);
            } else {
                rValue = list;
            }
        } else {
            eItem.put(extractField.getCode(), item.get(extractField.getCode()));
        }
        if (rValue == null) {
            rValue = eItem;
        }
        return rValue;
    }


    public enum ExtractMode {
        /**
         *
         */
        FIELD,
        EMBED,
        REMOVE_DUPLICATE,
        REMOVE_EMPTY;
    }
}
