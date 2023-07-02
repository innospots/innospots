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

package io.innospots.administration.manager;

import io.innospots.base.crypto.PasswordEncoder;
import io.innospots.base.enums.DataStatus;
import io.innospots.libra.security.LibraAuthImporter;
import io.innospots.libra.security.auth.basic.AuthUserDao;
import io.innospots.libra.security.auth.model.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.util.Assert;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/9/7
 */
@Slf4j
@SpringBootApplication(exclude = {QuartzAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        FreeMarkerAutoConfiguration.class})
@LibraAuthImporter
public class SuperAdminManager implements ApplicationRunner {

    @Autowired
    private AuthUserDao authUserDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SuperAdminManager.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("reset admin password.");
        Assert.notNull(authUserDao, "user dao is null");
        resetPassword();
    }

    private void resetPassword() {
        String password = RandomStringUtils.randomAscii(16);
        String encodePass = passwordEncoder.encode(password);
        AuthUser adminUser = authUserDao.selectById(1);
        if (adminUser == null) {
            adminUser = AuthUser.builder()
                    .userName("admin")
                    .password(encodePass)
                    .status(DataStatus.ONLINE)
                    .userId(1)
                    .build();
            authUserDao.insert(adminUser);
        } else {
            adminUser.setPassword(encodePass);
            adminUser.setUserName("admin");
            adminUser.setStatus(DataStatus.ONLINE);
            authUserDao.updateById(adminUser);
        }

        System.out.println("reset admin password: " + password);
    }
}
