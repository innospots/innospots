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

package io.innospots.libra.security.logger.service;

import io.innospots.base.configuration.InnospotConfigProperties;
import io.innospots.base.registry.ServiceRegistryHolder;
import io.innospots.libra.security.logger.entity.LoginLogEntity;
import io.innospots.libra.security.logger.operator.LoginLogOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * LoginLogService
 *
 * @author Wren
 * @date 2022/11/21-10:05
 */
@Slf4j
@Service
public class LoginLogService {

    private InnospotConfigProperties innospotConfigProperties;

    private LoginLogOperator loginLogOperator;

    public LoginLogService(InnospotConfigProperties innospotConfigProperties, LoginLogOperator loginLogOperator) {
        this.innospotConfigProperties = innospotConfigProperties;
        this.loginLogOperator = loginLogOperator;
    }


    /**
     * 定时清理登录日志，保留指定数量或是最近多少多少天的登录信息
     */
    @Scheduled(cron = "0 15 4 * * ?")
    private void loginLogCleanTask() {
        try {
            if (ServiceRegistryHolder.isLeader() && innospotConfigProperties.isEnableCleanSysLoginLog()) {
                int days = innospotConfigProperties.getSysLoginLogKeepDays();
                if (days < InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_DAYS) {
                    log.warn("loginLogCleanTask param error, days: " + days + " set default:" + InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_DAYS);
                    days = InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_DAYS;

                }
                int count = innospotConfigProperties.getSysLoginLogKeepAmount();
                if (count < InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_AMOUNT) {
                    log.warn("loginLogCleanTask param error, count: " + count + " set default:" + InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_AMOUNT);
                    count = InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_AMOUNT;
                }
                LocalDateTime deleteTime = LocalDateTime.now().plusDays(days * -1);
                //查询日志
                LoginLogEntity entity = loginLogOperator.getLastByIndex(count);
                int deleteCount = 0;
                if (entity != null) {
                    deleteCount = loginLogOperator.deleteLogHis(entity.getLogId(), deleteTime);
                }
                log.info("loginLogCleanTask delete:{} deleteTime:{} currTime:{}", deleteCount, deleteTime, LocalDateTime.now());
            } else {
                log.info("loginLogCleanTask not run!  curr service leader:{} enableCleanSysLoginLog:{} {}", ServiceRegistryHolder.isLeader(), innospotConfigProperties.isEnableCleanSysLoginLog(), ServiceRegistryHolder.getCurrentServer());
            }
        } catch (Exception e) {
            log.error("loginLogCleanTask error:{}", e.getMessage(), e);
        }

    }
}
