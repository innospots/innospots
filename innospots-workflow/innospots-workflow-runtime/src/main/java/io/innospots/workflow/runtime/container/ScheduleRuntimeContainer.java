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

package io.innospots.workflow.runtime.container;

import io.innospots.base.quartz.QuartzScheduleManager;
import io.innospots.base.quartz.ScheduleMode;
import io.innospots.workflow.core.runtime.FlowRuntimeRegistry;
import io.innospots.workflow.node.app.trigger.CronTimerNode;
import io.innospots.workflow.runtime.scheduled.TriggerNodeQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;

/**
 * 定时执行容器，通过调度时间执行
 *
 * @author Smars
 * @date 2021/5/31
 */
@Slf4j
public class ScheduleRuntimeContainer extends BaseRuntimeContainer {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleRuntimeContainer.class);

    private QuartzScheduleManager scheduleManager;


    public ScheduleRuntimeContainer(QuartzScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }


    @Override
    protected void updateTrigger(FlowRuntimeRegistry triggerInfo) {
        super.updateTrigger(triggerInfo);
        CronTimerNode timerNode = (CronTimerNode) triggerInfo.getRegistryNode();
        String jobName = triggerInfo.key();

        Class<TriggerNodeQuartzJob> jobClass = TriggerNodeQuartzJob.class;
        Date startTime = null;
        String cronExpression = null;
        if (timerNode.scheduleMode() == ScheduleMode.ONCE) {
            // date time format yyyy-MM-dd HH:mm:ss
            startTime = timerNode.startTime();
        } else {
            cronExpression = timerNode.cronExpression();
        }
        scheduleManager.refreshJob(jobName, jobClass, cronExpression, timerNode.scheduleMode(), startTime, null);
    }

    @Override
    protected void removeTrigger(FlowRuntimeRegistry flowRuntimeRegistry) {
        super.removeTrigger(flowRuntimeRegistry);
        scheduleManager.deleteJob(flowRuntimeRegistry.key());
    }


    public void runQuartJob(JobKey jobKey) {
        FlowRuntimeRegistry runtimeRegistry = triggerNodeCache.get(jobKey.getName());
        CronTimerNode timerNode = (CronTimerNode) runtimeRegistry.getRegistryNode();
        execute(runtimeRegistry, new HashMap<>());
    }

    @Override
    public void close() {
        logger.info("close schedule runtime container.");
    }


    /*public class TriggerNodeQuartzJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            if (ServiceRegistryHolder.isLeader()) {
                JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
                logger.info("schedule task is trigger：{}", jobKey.getName());
                //FlowTriggerInfo triggerInfo = triggerNodeCache.get(jobKey.getName());
                //runTrigger(triggerInfo,triggerInfo.getTriggerNode().getEventBody());
            }
        }
    }*/
}
