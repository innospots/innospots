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

package io.innospots.administration.prepare;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/6/4
 */
@Slf4j
//@Configuration
public class Configurations {

    /*
    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowListenerCustomizer(InnospotServerProperties serverProperties) {
        log.info("webhook web server factory:{}",serverProperties);
        return (factory) -> {
            factory.addBuilderCustomizers(builder -> addHttpListener(builder,serverProperties));
            factory.setContextPath(serverProperties.getWebhook().getContextPath());
        };
    }

    private Undertow.Builder addHttpListener(Undertow.Builder builder,InnospotServerProperties serverProperties) {
        String hostAddress = "0.0.0.0";
        if(serverProperties.getWebhook().getAddress()!=null){
            hostAddress = serverProperties.getWebhook().getAddress().getHostAddress();
        }
        return builder.addHttpListener(serverProperties.getWebhook().getPort(),hostAddress);
    }

     */
}
