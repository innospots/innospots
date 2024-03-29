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

import io.innospots.base.registry.ServiceRegistryHolder;
import io.innospots.base.watcher.AbstractWatcher;
import io.innospots.workflow.core.runtime.FlowRuntimeRegistry;
import io.innospots.workflow.node.app.trigger.ApiTriggerNode;
import io.innospots.workflow.node.app.trigger.CronTimerNode;
import io.innospots.workflow.node.app.trigger.QueueTriggerNode;
import io.innospots.workflow.runtime.container.RunTimeContainerManager;
import io.innospots.workflow.runtime.flow.FlowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程触发器观察器，检查有效的触发器
 * 数据库轮询监听，监听器失败，则对应的触发器失效
 *
 * @author Smars
 * @date 2021/5/30
 */
public class TriggerRegistryWatcher extends AbstractWatcher {

    private static final Logger logger = LoggerFactory.getLogger(TriggerRegistryWatcher.class);

    private FlowManager flowManager;

    private RunTimeContainerManager runTimeContainerManager;

    public TriggerRegistryWatcher(
            RunTimeContainerManager runTimeContainerManager
            , FlowManager flowManager) {
        this.runTimeContainerManager = runTimeContainerManager;
        this.flowManager = flowManager;
    }


    @Override
    public int execute() {
        List<FlowRuntimeRegistry> flowTriggers = flowManager.currentFlowTriggers();
        //event trigger
        List<FlowRuntimeRegistry> eventTriggers = new ArrayList<>();
        //event stream queue
        List<FlowRuntimeRegistry> queueTriggers = new ArrayList<>();
        //schedule trigger
        List<FlowRuntimeRegistry> scheduleTriggers = new ArrayList<>();

        for (FlowRuntimeRegistry flowTrigger : flowTriggers) {
            String nodeType = flowTrigger.getRegistryNode().nodeType();
            if (nodeType.equals(ApiTriggerNode.class.getName())) {
                eventTriggers.add(flowTrigger);
            } else if (nodeType.equals(QueueTriggerNode.class.getName())) {
                //TODO 此处需要做多节点集群识别处理，是否全部节点都能做队列触发器
                queueTriggers.add(flowTrigger);
            } else if (nodeType.equals(CronTimerNode.class.getName())) {
                //TODO
                if (ServiceRegistryHolder.isLeader()) {
                    //主节点添加
                    scheduleTriggers.add(flowTrigger);
                }
            }
        }//end for

        if (logger.isDebugEnabled()) {
            logger.debug("event trigger size:{}, queue trigger size:{}, schedule trigger size:{}",
                    eventTriggers.size(),
                    queueTriggers.size(),
                    scheduleTriggers.size());
        }

        runTimeContainerManager.registerEventTriggers(eventTriggers);
        runTimeContainerManager.registerQueueTriggers(queueTriggers);
        runTimeContainerManager.registerScheduleTriggers(scheduleTriggers);

        return 5;
    }
}
