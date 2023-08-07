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

package io.innospots.workflow.runtime.config;


import io.innospots.base.configuration.BaseServiceConfiguration;
import io.innospots.base.configuration.DatasourceConfiguration;
import io.innospots.base.data.minder.DataConnectionMinderManager;
import io.innospots.base.quartz.QuartzScheduleManager;
import io.innospots.workflow.core.config.InnospotWorkflowProperties;
import io.innospots.workflow.core.debug.FlowNodeDebugger;
import io.innospots.workflow.core.execution.listener.IFlowExecutionListener;
import io.innospots.workflow.core.execution.operator.IFlowExecutionOperator;
import io.innospots.workflow.core.execution.operator.INodeExecutionOperator;
import io.innospots.workflow.core.execution.operator.IScheduledNodeExecutionOperator;
import io.innospots.workflow.core.execution.reader.NodeExecutionReader;
import io.innospots.workflow.core.flow.instance.IWorkflowCacheDraftOperator;
import io.innospots.workflow.core.loader.IWorkflowLoader;
import io.innospots.workflow.core.webhook.DefaultResponseBuilder;
import io.innospots.workflow.core.webhook.WorkflowResponseBuilder;
import io.innospots.workflow.runtime.container.*;
import io.innospots.workflow.runtime.endpoint.WebhookRuntimeEndpoint;
import io.innospots.workflow.runtime.endpoint.WebhookTestEndpoint;
import io.innospots.workflow.runtime.endpoint.WorkflowManagementEndpoint;
import io.innospots.workflow.runtime.engine.ParallelStreamFlowEngine;
import io.innospots.workflow.runtime.engine.StreamFlowEngine;
import io.innospots.workflow.runtime.flow.FlowManager;
import io.innospots.workflow.runtime.flow.FlowNodeSimpleDebugger;
import io.innospots.workflow.runtime.scheduled.NodeExecutionEventListener;
import io.innospots.workflow.runtime.server.WorkflowWebhookServer;
import io.innospots.workflow.runtime.starter.RuntimePrepareStarter;
import io.innospots.workflow.runtime.starter.WatcherStarter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * @author Smars
 * @date 2021/3/15
 */
@Configuration
@EnableConfigurationProperties(WorkflowServerProperties.class)
@Import({DatasourceConfiguration.class, BaseServiceConfiguration.class})
public class WorkflowRuntimeConfiguration {

    @Bean
    public WebhookRuntimeEndpoint webhookRuntimeEndpoint(WebhookRuntimeContainer webhookRuntimeContainer) {
        return new WebhookRuntimeEndpoint(webhookRuntimeContainer);
    }

    @Bean
    public WebhookTestEndpoint webhookTestEndpoint(FlowNodeDebugger flowNodeDebugger) {

        return new WebhookTestEndpoint(flowNodeDebugger);
    }

    @Bean
    public WorkflowManagementEndpoint managementEndpoint(FlowManager flowManager,
                                                         RunTimeContainerManager runTimeContainerManager,
                                                         QuartzScheduleManager quartzScheduleManager,
                                                         WorkflowServerProperties serverProperties
    ) {
        return new WorkflowManagementEndpoint(flowManager, runTimeContainerManager, quartzScheduleManager, serverProperties);
    }

    @Bean
    public WatcherStarter watcherStarter(ApplicationContext applicationContext) {
        return new WatcherStarter(applicationContext);
    }

    @Bean
    public FlowManager flowManager(IWorkflowLoader workflowLoader) {
        return new FlowManager(workflowLoader);
    }

    @Bean
    public NodeExecutionEventListener nodeExecutionEventListener(
            IFlowExecutionOperator flowExecutionOperator,
            INodeExecutionOperator nodeExecutionOperator,
            IScheduledNodeExecutionOperator scheduledNodeExecutionOperator
    ) {
        return new NodeExecutionEventListener(flowExecutionOperator, nodeExecutionOperator, scheduledNodeExecutionOperator);
    }

    @Bean
    public StreamFlowEngine streamFlowEngine(FlowManager flowManager, List<IFlowExecutionListener> flowExecutionListeners) {

        return new StreamFlowEngine(flowExecutionListeners, flowManager);
    }

    @Bean
    public ParallelStreamFlowEngine parallelStreamFlowEngine(List<IFlowExecutionListener> flowExecutionListeners, FlowManager flowManager) {

        return new ParallelStreamFlowEngine(flowExecutionListeners, flowManager);
    }

    @Bean
    public QueueRuntimeContainer queueRuntimeContainer(
            DataConnectionMinderManager dataConnectionMinderManager,
            WorkflowServerProperties workflowServerProperties
    ) {
        return new QueueRuntimeContainer(dataConnectionMinderManager, workflowServerProperties.getQueueThreadCapacity());
    }


    @Bean
    public ScheduleRuntimeContainer scheduleRuntimeContainer(QuartzScheduleManager quartzScheduleManager) {
        return new ScheduleRuntimeContainer(quartzScheduleManager);
    }

    @Bean
    public DefaultResponseBuilder responseBuilder() {
        return new DefaultResponseBuilder();
    }

    @Bean
    public WebhookRuntimeContainer webhookRuntimeContainer() {
        return new WebhookRuntimeContainer(new DefaultResponseBuilder());
    }

    @Bean
    public CycleTimerRuntimeContainer cycleTimerRuntimeContainer(WorkflowServerProperties workflowServerProperties) {
        return new CycleTimerRuntimeContainer(workflowServerProperties.getMaxCycleFlow());
    }

    @Bean
    @ConditionalOnMissingBean
    public QuartzScheduleManager quartzScheduleManager() {
        QuartzScheduleManager quartzScheduleManager = new QuartzScheduleManager();
        quartzScheduleManager.startup();
        return quartzScheduleManager;
    }


    @Bean
    public FlowNodeDebugger nodeDebugger(NodeExecutionReader nodeExecutionReader,
                                         IFlowExecutionOperator flowExecutionOperator,
                                         IWorkflowCacheDraftOperator workFlowBuilderOperator
    ) {
        return new FlowNodeSimpleDebugger(workFlowBuilderOperator, nodeExecutionReader, flowExecutionOperator);
    }

    @Bean
    public WorkflowWebhookServer webhookServer(WorkflowServerProperties eventProperties,
                                               ServerProperties serverProperties,
                                               WebhookRuntimeContainer webhookRuntimeContainer) {
        Integer port = eventProperties.getPort();
        if (port == null) {
            port = 10000 + serverProperties.getPort();
        }
        return new WorkflowWebhookServer(port, eventProperties.getHost(), webhookRuntimeContainer);
    }

    @Bean
    public RunTimeContainerManager runTimeContainerManager(
            WebhookRuntimeContainer webhookRuntimeContainer,
            QueueRuntimeContainer queueRuntimeContainer,
            ScheduleRuntimeContainer scheduleRuntimeContainer,
            CycleTimerRuntimeContainer cycleTimerRuntimeContainer
    ) {
        return new RunTimeContainerManager(
                webhookRuntimeContainer, cycleTimerRuntimeContainer, queueRuntimeContainer, scheduleRuntimeContainer);
    }

    @Bean
    public RuntimePrepareStarter runtimePrepareStarter(WorkflowWebhookServer workflowWebhookServer,
                                                       InnospotWorkflowProperties workflowProperties,
                                                       RunTimeContainerManager runTimeContainerManager,
                                                       ApplicationAvailability applicationAvailability
    ) {

        return new RuntimePrepareStarter(
                workflowWebhookServer, runTimeContainerManager, workflowProperties, applicationAvailability);
    }

}
