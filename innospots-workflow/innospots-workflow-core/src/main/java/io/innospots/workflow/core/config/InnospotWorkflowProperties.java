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

package io.innospots.workflow.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Raydian
 * @date 2021/1/14
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "innospots.workflow")
public class InnospotWorkflowProperties {

    /**
     * the default days for keeping workflow execution
     */
    public static final int WORKFLOW_EXECUTION_KEEP_DAYS = 30;

    /**
     * the default amount for keeping workflow execution
     */
    public static final int WORKFLOW_EXECUTION_KEEP_AMOUNT = 100;

    /**
     * the maximum amount number of versions
     */
    private int workFlowInstanceKeepVersionAmount = 5;

    /**
     * workflowInstanceCache keep time of seconds
     */
    private int workflowInstanceCacheKeepSeconds = 60 * 24 * 2;

    /**
     * workflowExecution log keep time of seconds
     */
    private int workFlowExecutionKeepDays = WORKFLOW_EXECUTION_KEEP_DAYS;

    /**
     * the amount for keeping workflow execution
     */
    private int workFlowExecutionKeepAmount = WORKFLOW_EXECUTION_KEEP_AMOUNT;

    /**
     * the path use to store execution context
     */
    private String executionStorePath;

    private String resourceSecretKey="InnosKey99!@";


}
