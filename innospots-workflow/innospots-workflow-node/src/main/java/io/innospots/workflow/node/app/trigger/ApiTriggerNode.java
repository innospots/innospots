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

package io.innospots.workflow.node.app.trigger;

import cn.hutool.core.codec.Base64;
import io.innospots.base.model.field.ParamField;
import io.innospots.base.utils.BeanUtils;
import io.innospots.workflow.core.execution.ExecutionInput;
import io.innospots.workflow.core.execution.ExecutionResource;
import io.innospots.workflow.core.execution.ExecutionStatus;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeOutput;
import io.innospots.workflow.core.execution.operator.IExecutionContextOperator;
import io.innospots.workflow.core.node.app.TriggerNode;
import io.innospots.workflow.core.node.instance.NodeInstance;
import io.innospots.workflow.core.webhook.FlowWebhookConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * api trigger node is a webhook, which has not previous node
 * config webhook response field
 *
 * @author Smars
 * @date 2021/4/24
 */
public class ApiTriggerNode extends TriggerNode {

    public static final String FIELD_API_PATH = "path";

    public static final String FIELD_REQUEST_TYPE = "requestType";
    public static final String FIELD_AUTH_TYPE = "authType";
    public static final String FIELD_AUTH_BODY = "authBody";
    public static final String FIELD_RESPONSE_MODE = "responseMode";
    public static final String FIELD_RESPONSE_CODE = "responseCode";
    public static final String FIELD_RESPONSE_DATA = "responseData";
    public static final String FIELD_RESPONSE_FIELDS = "responseFields";
    public static final String FIELD_REQUEST_INPUTS = "requestInputs";

    private FlowWebhookConfig flowWebhookConfig;


    @Override
    protected void initialize(NodeInstance nodeInstance) {
        super.initialize(nodeInstance);
        validFieldConfig(nodeInstance, FIELD_API_PATH);
        validFieldConfig(nodeInstance, FIELD_AUTH_TYPE);
        validFieldConfig(nodeInstance, FIELD_RESPONSE_CODE);
        validFieldConfig(nodeInstance, FIELD_REQUEST_TYPE);
        validFieldConfig(nodeInstance, FIELD_RESPONSE_MODE);
        validFieldConfig(nodeInstance, FIELD_RESPONSE_DATA);
        eventBody.put("node_key", this.nodeKey());
        flowWebhookConfig = new FlowWebhookConfig();
        flowWebhookConfig.setRequestMethod(FlowWebhookConfig.RequestMethod.valueOf(nodeInstance.valueString(FIELD_REQUEST_TYPE)));
        flowWebhookConfig.setPath(nodeInstance.valueString(FIELD_API_PATH));
        flowWebhookConfig.setAuthType(FlowWebhookConfig.AuthType.valueOf(nodeInstance.valueString(FIELD_AUTH_TYPE)));
        flowWebhookConfig.setResponseMode(FlowWebhookConfig.ResponseMode.valueOf(nodeInstance.valueString(FIELD_RESPONSE_MODE)));
        flowWebhookConfig.setResponseCode(nodeInstance.valueString(FIELD_RESPONSE_CODE));
        List<Map<String, Object>> responseField = (List<Map<String, Object>>) nodeInstance.value("responseFields");
        if (responseField != null) {
            List<ParamField> params = BeanUtils.toBean(responseField, ParamField.class);
            flowWebhookConfig.setResponseFields(params);
        }
        Object v = nodeInstance.value(FIELD_AUTH_BODY);
        if (v != null) {
            flowWebhookConfig.setAuthBody((Map<String, Object>) v);
        }
    }

    @Override
    public void invoke(NodeExecution nodeExecution) {
        if (!validateAuthentication(nodeExecution)) {
            nodeExecution.setStatus(ExecutionStatus.FAILED);
            nodeExecution.setMessage("鉴权失败");
           return;
        }
        List<ExecutionInput> inputs = nodeExecution.getInputs();
        NodeOutput nodeOutput = new NodeOutput();
        for (ExecutionInput input : inputs) {
            for (Map<String, Object> item : input.getData()) {
                nodeOutput.addResult(item);
            }
            if (CollectionUtils.isNotEmpty(input.getResources())) {
                List<ExecutionResource> outputResources = IExecutionContextOperator.saveExecutionResources(input.getResources(), nodeExecution.getContextDataPath());
                for (int i = 0; i < outputResources.size(); i++) {
                    nodeOutput.addResource(i,outputResources.get(i));
                }
            }
        }
        nodeOutput.addNextKey(this.nextNodeKeys());
        nodeExecution.addOutput(nodeOutput);
    }

    private boolean validateAuthentication(NodeExecution nodeExecution) {
        Integer revision = nodeExecution.getRevision();
        if (revision <= 0) {
            return true;
        }
        String username = this.ni.getData().get("authBody.username").toString();
        String password = this.ni.getData().get("authBody.password").toString();
        String token = this.ni.getData().get("authBody.token").toString();
        String authType = null;
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            authType = FlowWebhookConfig.AuthType.BASIC_AUTH.name();

        } else if (StringUtils.isNotEmpty(token)) {
            authType = FlowWebhookConfig.AuthType.BEARER_AUTH.name();
        }
        if (StringUtils.isEmpty(authType)) {
            return true;
        }
        boolean flag = false;
        List<ExecutionInput> inputs = nodeExecution.getInputs();
        for (ExecutionInput input : inputs) {
            for (Map<String, Object> item : input.getData()) {
                LinkedHashMap headerMap = (LinkedHashMap) item.get("headers");
                if (MapUtils.isEmpty(headerMap)) {
                    continue;
                }
                String value = headerMap.get("Authorization").toString();
                if ((StringUtils.equalsIgnoreCase(authType, FlowWebhookConfig.AuthType.BASIC_AUTH.name())
                        && StringUtils.equalsIgnoreCase("Basic " + Base64.encode(username + ":" + password), value))
                        || (StringUtils.equalsIgnoreCase(authType, FlowWebhookConfig.AuthType.BEARER_AUTH.name()) && StringUtils.equalsIgnoreCase("Bearer " + token, value))) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public String apiPath() {
        return flowWebhookConfig != null ? flowWebhookConfig.getPath() : null;
    }


    public FlowWebhookConfig getFlowWebhookConfig() {
        return flowWebhookConfig;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("webhook path=").append(flowWebhookConfig.getPath());
        sb.append(" ,node key=").append(flowWebhookConfig.getPath());
        sb.append(" ,nodeType=").append(this.ni.getNodeType());
        sb.append('}');
        return sb.toString();
    }
}
