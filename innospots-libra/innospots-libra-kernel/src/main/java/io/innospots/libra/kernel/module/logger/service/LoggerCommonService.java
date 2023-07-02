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

package io.innospots.libra.kernel.module.logger.service;

import io.innospots.base.configuration.InnospotConfigProperties;
import io.innospots.base.registry.ServiceRegistryHolder;
import io.innospots.libra.kernel.module.logger.entity.SysOperateLogEntity;
import io.innospots.libra.kernel.module.logger.operator.SysLogOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * DataCleanService
 *
 * @author Wren
 * @date 2022/11/17-21:36
 */
@Slf4j
@Service
public class LoggerCommonService {


    private InnospotConfigProperties innospotConfigProperties;

    private SysLogOperator sysLogOperator;

    public LoggerCommonService(InnospotConfigProperties innospotConfigProperties,
                               SysLogOperator sysLogOperator) {
        this.innospotConfigProperties = innospotConfigProperties;
        this.sysLogOperator = sysLogOperator;
    }


    //sys_operation_log，同样可设置清理历史时长和最大保留记录。默认设置为不清理

    @Scheduled(cron = "0 10 4 * * ?")
    private void SysOperateLogCleanTask() {
        try {
            if (ServiceRegistryHolder.isLeader() && innospotConfigProperties.isEnableCleanSysOperateLog()) {
                int days = innospotConfigProperties.getSysOperateLogKeepDays();
                if (days < InnospotConfigProperties.SYS_OPERATE_LOG_KEEP_DAYS) {
                    log.warn("SysOperateLogCleanTask param error, days: " + days + " set default:" + InnospotConfigProperties.SYS_OPERATE_LOG_KEEP_DAYS);
                    days = InnospotConfigProperties.SYS_OPERATE_LOG_KEEP_DAYS;

                }
                int count = innospotConfigProperties.getSysLoginLogKeepAmount();
                if (count < InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_AMOUNT) {
                    log.warn("SysOperateLogCleanTask param error, count: " + count + " set default:" + InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_AMOUNT);
                    count = InnospotConfigProperties.SYS_LOGIN_LOG_KEEP_AMOUNT;
                }
                LocalDateTime deleteTime = LocalDateTime.now().plusDays(days * -1);
                //查询日志
                SysOperateLogEntity entity = sysLogOperator.getLastByIndex(count);
                int deleteCount = 0;
                if (entity != null) {
                    deleteCount = sysLogOperator.deleteLogHis(entity.getLogId(), deleteTime);
                }
                log.info("SysOperateLogCleanTask delete:{} deleteTime:{} currTime:{}", deleteCount, deleteTime, LocalDateTime.now());
            } else {
                log.info("SysOperateLogCleanTask not run!  curr service leader:{} enableCleanSysOperateLog:{} {}", ServiceRegistryHolder.isLeader(), innospotConfigProperties.isEnableCleanSysOperateLog(), ServiceRegistryHolder.getCurrentServer());
            }
        } catch (Exception e) {
            log.error("SysOperateLogCleanTask error:{}", e.getMessage(), e);
        }

    }
}
