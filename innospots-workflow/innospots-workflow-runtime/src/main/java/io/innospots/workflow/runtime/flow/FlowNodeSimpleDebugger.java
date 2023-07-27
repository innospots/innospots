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

package io.innospots.workflow.runtime.flow;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import io.innospots.base.utils.DateTimeUtils;
import io.innospots.workflow.console.operator.instance.WorkflowBuilderOperator;
import io.innospots.workflow.core.context.WorkflowRuntimeContext;
import io.innospots.workflow.core.debug.AppDebugPayload;
import io.innospots.workflow.core.engine.FlowEngineManager;
import io.innospots.workflow.core.enums.FlowStatus;
import io.innospots.workflow.core.execution.ExecutionResource;
import io.innospots.workflow.core.execution.ExecutionStatus;
import io.innospots.workflow.core.debug.FlowNodeDebugger;
import io.innospots.workflow.core.execution.flow.FlowExecution;
import io.innospots.workflow.core.execution.node.NodeExecution;
import io.innospots.workflow.core.execution.node.NodeExecutionBase;
import io.innospots.workflow.core.execution.node.NodeExecutionDisplay;
import io.innospots.workflow.core.execution.operator.IFlowExecutionOperator;
import io.innospots.workflow.core.execution.reader.NodeExecutionReader;
import io.innospots.workflow.core.flow.BuildProcessInfo;
import io.innospots.workflow.core.flow.WorkflowBaseBody;
import io.innospots.workflow.core.node.instance.NodeInstance;
import io.innospots.workflow.core.webhook.WorkflowResponse;
import io.innospots.workflow.node.app.trigger.ApiTriggerNode;
import io.innospots.workflow.runtime.engine.BaseFlowEngine;
import io.innospots.workflow.runtime.response.DefaultResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/2/13
 */
@Slf4j
public class FlowNodeSimpleDebugger implements FlowNodeDebugger {


    private NodeExecutionReader nodeExecutionReader;

    private IFlowExecutionOperator flowExecutionOperator;

    private WorkflowBuilderOperator workFlowBuilderOperator;

    private Cache<Long, String> executionCache = Caffeine.newBuilder().build();

    public FlowNodeSimpleDebugger(WorkflowBuilderOperator workFlowBuilderOperator,
                                  NodeExecutionReader nodeExecutionReader,
                                  IFlowExecutionOperator flowExecutionOperator) {
        this.workFlowBuilderOperator = workFlowBuilderOperator;
        this.nodeExecutionReader = nodeExecutionReader;
        this.flowExecutionOperator = flowExecutionOperator;
    }

    @Override
    public Map<String, NodeExecutionDisplay> execute(Long workflowInstanceId, String nodeKey, List<Map<String, Object>> inputs) {
        workFlowBuilderOperator.saveCacheToDraft(workflowInstanceId);

        inputs = convertApiInput(inputs);

        BaseFlowEngine flowEngine = (BaseFlowEngine) FlowEngineManager.eventFlowEngine();
        BuildProcessInfo buildProcessInfo = flowEngine.prepare(workflowInstanceId, 0, true);
        log.info("build info:{}", buildProcessInfo);
        Map<String, NodeExecutionDisplay> result = new LinkedHashMap<>();
        if (buildProcessInfo.getStatus() != FlowStatus.LOADED) {
            log.error("flow prepare failed, {}", buildProcessInfo);
            if (buildProcessInfo.getBuildException() != null) {
                NodeExecutionDisplay display = new NodeExecutionDisplay();
                display.setNodeKey(nodeKey);
                display.addLog("startTime", DateTimeUtils.normalizeDateTime(buildProcessInfo.getStartTime()));
                display.addLog("endTime", DateTimeUtils.normalizeDateTime(buildProcessInfo.getEndTime()));
                display.addLog("status", ExecutionStatus.FAILED);
                display.addLog("error", buildProcessInfo.errorMessage());
                result.put(nodeKey, display);
            } else {
                for (Map.Entry<String, Exception> exceptionEntry : buildProcessInfo.getErrorInfo().entrySet()) {
                    NodeExecutionDisplay display = new NodeExecutionDisplay();
                    display.addLog("startTime", DateTimeUtils.normalizeDateTime(buildProcessInfo.getStartTime()));
                    display.addLog("endTime", DateTimeUtils.normalizeDateTime(buildProcessInfo.getEndTime()));
                    display.addLog("status", ExecutionStatus.FAILED);
                    display.setNodeKey(exceptionEntry.getKey());
                    display.addLog("error", buildProcessInfo.getBuildMessage(exceptionEntry.getKey()));
                    result.put(display.getNodeKey(), display);
                }
            }
            //return result;
        }

        FlowExecution flowExecution = fillFlowExecution(inputs, workflowInstanceId);


        //endNodeKey
        log.info("flow execution: {}", flowExecution);
        //executionCache.put(workflowInstanceId,flowExecution.getFlowExecutionId());
        flowExecution.setEndNodeKey(nodeKey);
        flowEngine.execute(flowExecution);

        WorkflowBaseBody workflowBaseBody = workFlowBuilderOperator.getFlowInstanceDraftOrCache(workflowInstanceId);
        Map<String, NodeInstance> nodeCache = null;
        try {
            nodeCache = workflowBaseBody.getNodes().stream().collect(Collectors.toMap(NodeInstance::getNodeKey, Function.identity()));
        } catch (Exception e) {
            log.error(e.getMessage());
            for (NodeInstance node : workflowBaseBody.getNodes()) {
                log.error(node.toString());
            }
            throw e;
        }
        List<NodeExecution> nodeExecutions = new ArrayList<>(flowExecution.getNodeExecutions().values());
        nodeExecutions.sort(Comparator.comparingInt(NodeExecutionBase::getSequenceNumber));
        LinkedHashMap<String, String> outMap = new LinkedHashMap<>();
        for (NodeExecution nodeExecution : nodeExecutions) {
            NodeExecutionDisplay executionDisplay = NodeExecutionDisplay.build(nodeExecution);
            NodeInstance nodeInstance = nodeCache.get(nodeExecution.getNodeKey());
            result.put(nodeExecution.getNodeKey(), executionDisplay);
            outMap.put(nodeInstance.simpleInfo(), nodeExecution.getNodeExecutionId());
        }

        if (log.isDebugEnabled()) {
            log.debug("node executions:{}", outMap);
            log.debug("execute path: {}", String.join("-->", outMap.keySet()));
        }
        NodeInstance nodeInstance = nodeCache.get(nodeKey);
        NodeExecutionDisplay executionDisplay = result.get(nodeKey);
        if (executionDisplay != null && nodeInstance != null && flowExecution.getStatus() == ExecutionStatus.COMPLETE) {
            nodeInstance.setOutputFields(executionDisplay.getOutputFields());
            workFlowBuilderOperator.saveFlowInstanceToCache(workflowBaseBody);
            workFlowBuilderOperator.saveCacheToDraft(workflowInstanceId);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        //update out field
        executionCache.invalidate(workflowInstanceId);
        return result;
//        return nodeExecutionDisplayReader.readExecutionByFlowExecutionId(workflowInstanceId,flowExecution.getFlowExecutionId(),null);
    }

    private List<Map<String, Object>> convertApiInput(List<Map<String, Object>> rawInputs) {
        List<Map<String, Object>> nList = new ArrayList<>();
        for (Map<String, Object> rawInput : rawInputs) {
            if (rawInput.containsKey("contentType") &&
                    (rawInput.containsKey("params") || rawInput.containsKey("headers") || rawInput.containsKey("body"))){
                Object v = rawInput.get("params");
                Map<String, Object> params = convertData(v);
                rawInput.put("params", params);
                v = rawInput.get("headers");
                Map<String, Object> headers = convertData(v);
                rawInput.put("headers", headers);
                v = rawInput.get("body");
                Map<String, Object> body = convertData(v);
                rawInput.put("body", body);
            }
            nList.add(rawInput);
        }//end for
        return nList;
    }

    private Map<String, Object> convertData(Object data) {
        Map<String, Object> mm = new HashMap<>();
        if (data instanceof List) {
            List<Map<String, Object>> item = (List<Map<String, Object>>) data;
            for (Map<String, Object> m : item) {
                mm.put(String.valueOf(m.get("name")), m.get("value"));
            }
        }
        return mm;
    }

    @Override
    public NodeExecutionDisplay execute(AppDebugPayload appDebugPayload) {
        return AppNodeDebugger.execute(appDebugPayload);
    }

    @Override
    public ExecutionResource updateTestFile(MultipartFile uploadFile, boolean force) {
        return AppNodeDebugger.updateTestFile(uploadFile, force);
    }


    @Override
    public FlowExecution currentExecuting(Long workflowInstanceId) {
        String flowExecutionId = executionCache.getIfPresent(workflowInstanceId);
        if (flowExecutionId == null) {
            return null;
        }
        return flowExecutionOperator.getFlowExecutionById(flowExecutionId, false);
    }

    @Override
    public Map<String, NodeExecutionDisplay> readNodeExecutions(Long workflowInstanceId, List<String> nodeKeys) {

        return nodeExecutionReader.readLatestNodeExecutionByFlowInstanceId(workflowInstanceId, 0, nodeKeys);
    }

    @Override
    public WorkflowResponse testWebhook(String flowKey, Map<String, Object> input) {
        return testWebhook(flowKey, Lists.newArrayList(input));
    }

    @Override
    public WorkflowResponse testWebhook(String flowKey, List<Map<String, Object>> inputs) {
        WorkflowBaseBody workflowBaseBody = workFlowBuilderOperator.getWorkflowBodyByKey(flowKey, 0, false);
        BaseFlowEngine flowEngine = (BaseFlowEngine) FlowEngineManager.eventFlowEngine();
        Long workflowInstanceId = workflowBaseBody.getWorkflowInstanceId();
        BuildProcessInfo buildProcessInfo = flowEngine.prepare(workflowInstanceId, 0, false);
        log.info("build info:{}", buildProcessInfo);
        WorkflowResponse workflowResponse = new WorkflowResponse();
        workflowResponse.setFlowKey(flowKey);
        workflowResponse.setRevision(0);
        if (buildProcessInfo.getStatus() != FlowStatus.LOADED) {
            log.error("flow prepare failed, {}", buildProcessInfo);
            workflowResponse.setResponseTime(LocalDateTime.now());
            return workflowResponse;
        }

        FlowExecution flowExecution = fillFlowExecution(inputs, workflowInstanceId);

        WorkflowRuntimeContext workflowRuntimeContext = WorkflowRuntimeContext.build(flowExecution);

        //endNodeKey
        log.info("flow execution: {}", flowExecution);
        flowEngine.execute(flowExecution);
        Flow flow = flowEngine.flow(workflowInstanceId, 0);
        ApiTriggerNode triggerNode = (ApiTriggerNode) flow.startNodes().get(0);

        //update out field
        return new DefaultResponseBuilder().build(workflowRuntimeContext, triggerNode);
    }

    @Override
    public FlowExecution stop(String flowExecutionId) {
        BaseFlowEngine flowEngine = (BaseFlowEngine) FlowEngineManager.eventFlowEngine();
        return flowEngine.stop(flowExecutionId);
    }

    private FlowExecution fillFlowExecution(List<Map<String, Object>> inputs, Long workflowInstanceId) {
        FlowExecution flowExecution = null;
        if (CollectionUtils.isEmpty(inputs) || inputs.get(0).isEmpty()) {
            FlowExecution lastFlowExecution = flowExecutionOperator.getLatestFlowExecution(workflowInstanceId, 0, true);
            if (lastFlowExecution != null) {
                flowExecution = FlowExecution.buildNewFlowExecution(
                        workflowInstanceId, 0, false, false);
                flowExecution.setInput(lastFlowExecution.getInput());
            }
        }

        if (flowExecution == null) {
            flowExecution = FlowExecution.buildNewFlowExecution(
                    workflowInstanceId, 0, false, false, inputs);
        }

        return flowExecution;
    }
}
