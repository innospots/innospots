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

package io.innospots.base.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Smars
 * @date 2021/3/15
 */
public class ThreadPoolBuilder {

    private static int processorNumber = Runtime.getRuntime().availableProcessors();

    public static final int DEFAULT_MAX_QUEUE_CAPACITY = 20000;

    public static ThreadPoolTaskExecutor build(int coreSize, int maxSize, int queueCapacity, String poolName) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(coreSize);
        taskExecutor.setMaxPoolSize(maxSize);
        if (queueCapacity > 0) {
            taskExecutor.setQueueCapacity(queueCapacity);
        }
        taskExecutor.setThreadGroupName(poolName);
        taskExecutor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat(poolName + "-%d").build());
        taskExecutor.setBeanName(poolName);
        taskExecutor.initialize();
        return taskExecutor;
    }

    public static ThreadPoolTaskExecutor build(int coreSize, int maxSize, String poolName) {
        return build(coreSize, maxSize, DEFAULT_MAX_QUEUE_CAPACITY, poolName);
    }

    public static ThreadPoolTaskExecutor build(int coreSize, String poolName) {
        return build(coreSize, coreSize, poolName);
    }

    public static ThreadPoolTaskExecutor build(String poolName, int queueCapacity) {
        return build(processorNumber, processorNumber, queueCapacity, poolName);
    }

}
