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

package io.innospots.workflow.runtime.watcher;

import io.innospots.base.enums.DataStatus;
import io.innospots.base.watcher.AbstractWatcher;
import io.innospots.workflow.console.entity.instance.WorkflowInstanceEntity;
import io.innospots.workflow.console.operator.instance.WorkflowInstanceOperator;
import io.innospots.workflow.core.loader.IWorkflowLoader;
import io.innospots.workflow.runtime.flow.Flow;
import io.innospots.workflow.runtime.flow.FlowManager;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Smars
 * @date 2021/6/10
 */
public class WorkflowStatusWatcher extends AbstractWatcher {


    private static final Logger logger = LoggerFactory.getLogger(WorkflowStatusWatcher.class);

    private WorkflowInstanceOperator workflowInstanceOperator;

    private FlowManager flowManager;

    private int recentMinutes = 3;

    public WorkflowStatusWatcher(WorkflowInstanceOperator workflowInstanceOperator,
                                 FlowManager flowManager) {
        this.workflowInstanceOperator = workflowInstanceOperator;
        this.flowManager = flowManager;
    }

    @Override
    public int execute() {
        //recent update flowInstance
        List<WorkflowInstanceEntity> flowInstances = workflowInstanceOperator.selectRecentlyUpdateOrOnLine(recentMinutes);
        if (CollectionUtils.isEmpty(flowInstances)) {
            logger.debug("not exit available online or updated flow.");
            return 0;
        }

        Set<String> onlineSet = new HashSet<>(flowManager.cacheFlowKeys());

        for (int i = 0; i < flowInstances.size(); i++) {
            WorkflowInstanceEntity instance = flowInstances.get(i);
            if (instance.getStatus() == DataStatus.OFFLINE ||
                    instance.getStatus() == DataStatus.REMOVED) {
                String key = IWorkflowLoader.key(instance.getWorkflowInstanceId(), instance.getRevision());
                logger.info("remove offline flow from manager:{}", key);
                flowManager.clear(key);
            } else if (instance.getStatus() == DataStatus.ONLINE) {
                String key = IWorkflowLoader.key(instance.getWorkflowInstanceId(), instance.getRevision());
                onlineSet.remove(key);
                Flow flow = flowManager.findFlow(instance.getWorkflowInstanceId(), instance.getRevision());
                if (flow == null) {
                    //flow not exist
                    logger.info("the flow is online:{}", key);
                    flowManager.loadFlow(instance.getWorkflowInstanceId(), instance.getRevision());
                } else if (flow.hasUpdate(instance.getUpdatedTime())) {
                    //flow update
                    logger.info("the flow has bean updated: {}, refresh flow.", key);
                    flowManager.loadFlow(instance.getWorkflowInstanceId(), instance.getRevision(), true, true);
                }
            }
        }//end for

        // offline flow
        if (!onlineSet.isEmpty()) {
            for (String s : onlineSet) {
                logger.info("remove offline flow from manager:{}", s);
                flowManager.clear(s);
            }
            //TODO 增加本地的生成的json配置文件的清理工作
        }

        return 0;
    }
}
