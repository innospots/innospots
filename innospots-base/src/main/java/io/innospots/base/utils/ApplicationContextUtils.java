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

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author Raydian
 * @date 2020/12/15
 */
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }

    public static boolean isLoaded(){
        return applicationContext!=null;
    }

    public static ApplicationContext applicationContext() {
        return applicationContext;
    }

    /**
     * send application event
     *
     * @param applicationEvent
     */
    public static void sendAppEvent(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

    public static <T> T getBean(Class<T> clazz) {
        Assert.notNull(applicationContext, "application context is null.");
        return applicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        Assert.notNull(applicationContext, "application context is null.");
        return applicationContext().getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        Assert.notNull(applicationContext, "application context is null.");
        return applicationContext().getBeansOfType(clazz);
    }

    public static Environment environment() {
        Assert.notNull(applicationContext, "application context is null.");
        return applicationContext().getEnvironment();
    }

    public static String serverIpAddress() {
        Environment environment = environment();
        return environment.getProperty("spring.cloud.client.ip-address");
    }


    public static Integer serverPort() {
        Environment environment = environment();
        String port = environment.getProperty("server.port");
        if (port != null) {
            return Integer.valueOf(port);
        }
        return 0;
    }

    public static String applicationId() {
        return applicationContext.getId();
    }

    public static void serviceShutdown() {
        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }
}
