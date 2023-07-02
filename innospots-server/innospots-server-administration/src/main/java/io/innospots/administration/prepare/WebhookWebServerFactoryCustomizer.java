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

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.embedded.UndertowWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.UndertowServletWebServerFactoryCustomizer;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.core.Ordered;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/6/5
 */
public class WebhookWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>, Ordered {

    private final ListableBeanFactory beanFactory;
    private final Class<? extends WebServerFactoryCustomizer<?>>[] customizerClasses;


    public WebhookWebServerFactoryCustomizer(
            ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.customizerClasses = new Class[]{ServletWebServerFactoryCustomizer.class, UndertowServletWebServerFactoryCustomizer.class, UndertowWebServerFactoryCustomizer.class};
    }

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        //InnospotServerProperties innospotServerProperties = BeanFactoryUtils.beanOfTypeIncludingAncestors(this.beanFactory, InnospotServerProperties.class);
        this.customizeSameAsParentContext(factory);
        factory.setErrorPages(Collections.emptySet());
        ServerProperties serverProperties = BeanFactoryUtils.beanOfTypeIncludingAncestors(this.beanFactory, ServerProperties.class);
        this.customize(factory, serverProperties);
    }

    private void customizeSameAsParentContext(ConfigurableServletWebServerFactory factory) {
        List<WebServerFactoryCustomizer<?>> customizers = Arrays.stream(this.customizerClasses).map(this::getCustomizer).filter(Objects::nonNull).collect(Collectors.toList());
        this.invokeCustomizers(factory, customizers);
    }

    private void invokeCustomizers(ConfigurableServletWebServerFactory factory, List<WebServerFactoryCustomizer<?>> customizers) {
        LambdaSafe.callbacks(WebServerFactoryCustomizer.class, customizers, factory, new Object[0]).invoke((customizer) -> {
            customizer.customize(factory);
        });
    }

    private WebServerFactoryCustomizer<?> getCustomizer(Class<? extends WebServerFactoryCustomizer<?>> customizerClass) {
        try {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(this.beanFactory, customizerClass);
        } catch (NoSuchBeanDefinitionException var3) {
            return null;
        }
    }

    protected void customize(ConfigurableServletWebServerFactory factory, ServerProperties serverProperties) {
        factory.setPort(9900);


        factory.setServerHeader(serverProperties.getServerHeader());
//        factory.setAddress();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
