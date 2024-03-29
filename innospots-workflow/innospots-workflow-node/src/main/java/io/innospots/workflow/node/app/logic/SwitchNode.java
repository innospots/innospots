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

package io.innospots.workflow.node.app.logic;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.innospots.base.condition.BaseCondition;
import io.innospots.base.exception.ConfigException;
import io.innospots.base.json.JSONUtils;
import io.innospots.base.re.IExpression;
import io.innospots.base.re.aviator.AviatorExpression;
import io.innospots.base.utils.Initializer;
import io.innospots.workflow.core.execution.ExecutionInput;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeOutput;
import io.innospots.workflow.core.node.app.BaseAppNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * scripts contain json array object, the class of which is SwitchCondition
 *
 * @author Raydian
 * @version 1.0.0
 * @date 2020/11/3
 */
@Slf4j
public class SwitchNode extends BaseAppNode {

    public static final String FIELD_CONDITIONS = "conditions";
    public static final String FIELD_CONDITION_FIELD = "conditionField";

    private SwitchCondition[] switchConditions;

    private List<String> defaultNextNodeKeys = new ArrayList<>();

    @Override
    protected void initialize(NodeInstance nodeInstance) {
        super.initialize(nodeInstance);
        switchConditions = buildConditions(nodeInstance);
        List<String> nextNodes = new ArrayList<>(this.nextNodeKeys());
        for (SwitchCondition switchCondition : switchConditions) {
            if (switchCondition.getBranch() == null) {
                defaultNextNodeKeys.addAll(nextNodes);
            }
            if (!nextNodes.isEmpty() && CollectionUtils.isNotEmpty(switchCondition.getNodeKeys())) {
                for (String nodeKey : switchCondition.getNodeKeys()) {
                    nextNodes.remove(nodeKey);
                }
            }

        }//end for switch condition
        defaultNextNodeKeys.addAll(nextNodes);
    }

    @Override
    public void invoke(NodeExecution nodeExecution) {
        Map<String, NodeOutput> outCache = new LinkedHashMap<>();
        NodeOutput defaultOutput = new NodeOutput("#default");
        if (switchConditions != null) {
            for (int i = 0; i < switchConditions.length; i++) {
                String nName = switchConditions[i].name(i + 1);
                NodeOutput nodeOutput = new NodeOutput(nName);
                nodeOutput.addNextKey(switchConditions[i].getNodeKeys());
                outCache.put(switchConditions[i].sourceAnchor, nodeOutput);
            }
            for (ExecutionInput executionInput : nodeExecution.getInputs()) {
                for (Map<String, Object> data : executionInput.getData()) {
                    boolean isMatch = false;
                    for (SwitchCondition switchCondition : switchConditions) {
                        if (switchCondition.match(data)) {
                            isMatch = true;
                            NodeOutput nodeOutput = outCache.get(switchCondition.sourceAnchor);
                            nodeOutput.addResult(data);
                            break;
                        }//end if switch condition
                    }//end for switch conditions

                    if (!isMatch) {
                        defaultOutput.addResult(data);
                    }
                }//end for data
            }//end execution input
            defaultOutput.addNextKey(this.defaultNextNodeKeys);

            outCache.values().forEach(nodeExecution::addOutput);
            nodeExecution.addOutput(defaultOutput);
        }//end if
        if (log.isDebugEnabled()) {
            log.debug("node execution, nodeOutput:{} {}", outCache, nodeExecution);
        }
    }


    private SwitchCondition[] buildConditions(NodeInstance nodeInstance) {
        Object v = nodeInstance.getData().get(FIELD_CONDITIONS);
        if (v == null) {
            throw ConfigException.buildMissingException(this.getClass(), "nodeKey:" + nodeKey() + ", field:" + FIELD_CONDITIONS);
        }
        SwitchCondition[] switchConditions = null;
        try {
            switchConditions = JSONUtils.parseObject(JSONUtils.toJsonString(v), SwitchCondition[].class);
            assert switchConditions != null;
            for (SwitchCondition switchCondition : switchConditions) {
                switchCondition.initialize();
                if (this.nodeAnchors() != null) {
                    switchCondition.setNodeKeys(this.nodeAnchors().get(switchCondition.sourceAnchor));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return switchConditions;
    }

    @Getter
    @Setter
    public static class SwitchCondition implements Initializer {
        @JsonIgnore
        private List<String> nodeKeys;
        private String sourceAnchor;
        private BaseCondition branch;
        @JsonIgnore
        private String condition;
        private String desc;

        @JsonIgnore
        private IExpression<Object> expression;

        public String name(int pos) {
            return desc != null ? desc : "#" + pos;
        }

        @Override
        public void initialize() {
            if (branch != null) {
                branch.initialize();
                this.condition = branch.getStatement();
                if (condition != null) {
                    expression = new AviatorExpression(this.condition, null);
                }
            }
        }

        public boolean match(Map<String, Object> data) {
            if (expression == null) {
                return false;
            }
            return (Boolean) expression.execute(data);
        }
    }

}
