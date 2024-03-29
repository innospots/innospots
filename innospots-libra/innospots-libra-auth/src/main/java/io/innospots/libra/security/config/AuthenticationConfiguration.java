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

package io.innospots.libra.security.config;

import io.innospots.base.configuration.InnospotDbProperties;
import io.innospots.base.crypto.BCryptPasswordEncoder;
import io.innospots.base.crypto.PasswordEncoder;
import io.innospots.libra.base.configuration.AuthProperties;
import io.innospots.libra.base.filter.IgnorePathFilter;
import io.innospots.libra.security.auth.AuthenticationProvider;
import io.innospots.libra.security.auth.basic.UserPasswordAuthenticationProvider;
import io.innospots.libra.security.filter.AuthenticationFilter;
import io.innospots.libra.security.jwt.JwtAuthManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author Smars
 * @date 2021/2/15
 */
@Configuration
@EnableConfigurationProperties({AuthProperties.class, InnospotDbProperties.class})
@MapperScan({"io.innospots.libra.security.auth.basic"})
public class AuthenticationConfiguration {

    @ConditionalOnProperty(prefix = "innospots.security", name = "mode", havingValue = "BASIC")
    @Bean
    public AuthenticationProvider userPasswordAuthenticationProvider() {
        return new UserPasswordAuthenticationProvider();
    }

    @ConditionalOnProperty(prefix = "innospots.security", name = "mode", havingValue = "EXHIBITION")
    @Bean
    public AuthenticationProvider exhibitAuthenticationProvider() {
        return new UserPasswordAuthenticationProvider();
    }

    @Bean
    public JwtAuthManager jwtAuthManager(AuthProperties authProperties) {
        return new JwtAuthManager(authProperties);
    }

    @Bean
    public FilterRegistrationBean<Filter> ignoreFilterRegistrationBean(AuthProperties authProperties) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new IgnorePathFilter(authProperties));
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> authenticationFilterRegistrationBean(JwtAuthManager authManager) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new AuthenticationFilter(authManager));
        filterRegistrationBean.setOrder(3);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}