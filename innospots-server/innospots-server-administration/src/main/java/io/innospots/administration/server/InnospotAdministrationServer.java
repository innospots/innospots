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

package io.innospots.administration.server;


import io.innospots.base.registry.ServiceRegistryHolder;
import io.innospots.base.registry.enums.ServiceType;
import io.innospots.base.utils.ApplicationContextUtils;
import io.innospots.base.utils.DateTimeUtils;
import io.innospots.connector.schema.AppSchemaApiImporter;
import io.innospots.libra.kernel.LibraKernelImporter;
import io.innospots.libra.security.LibraAuthImporter;
import io.innospots.workflow.console.WorkflowApiImporter;
import io.innospots.workflow.runtime.WorkflowRuntimeImporter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Raydian
 * @date 2020/12/14
 */
//@EnableDiscoveryClient
@SpringBootApplication(exclude = {QuartzAutoConfiguration.class,
//        HibernateJpaAutoConfiguration.class,
        FreeMarkerAutoConfiguration.class})
//@EnableAutoConfiguration
@LibraAuthImporter
@LibraKernelImporter
@AppSchemaApiImporter
@WorkflowRuntimeImporter
@WorkflowApiImporter
@EnableScheduling
public class InnospotAdministrationServer {

    public static void main(String[] args) {
        ServiceRegistryHolder.serverType(ServiceType.ADMINISTRATION);
        SpringApplication.run(InnospotAdministrationServer.class, args);
    }


    /**
     * /actuator/info config
     */
    @Component
    class MyInfo implements InfoContributor {
        @Override
        public void contribute(Info.Builder builder) {
            ApplicationContext context = ApplicationContextUtils.applicationContext();
            Map<String, String> runInfo = new LinkedHashMap<>();
            runInfo.put("applicationId", context.getId());
            runInfo.put("applicationName", context.getApplicationName());
            runInfo.put("upTime", DateTimeUtils.consume(context.getStartupDate()));
            runInfo.put("activeProFiles", StringUtils.join(context.getEnvironment().getActiveProfiles(), "|"));
            builder.withDetail("info", runInfo);
        }
    }
}
