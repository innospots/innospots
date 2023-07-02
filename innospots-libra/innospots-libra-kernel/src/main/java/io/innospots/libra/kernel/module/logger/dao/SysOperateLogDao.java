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

package io.innospots.libra.kernel.module.logger.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.innospots.libra.kernel.module.logger.entity.SysOperateLogEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;


/**
 * @author chenc
 * @date 2021/2/18 10:58
 */
public interface SysOperateLogDao extends BaseMapper<SysOperateLogEntity> {


    /**
     * delete log lt logId max 1000
     *
     * @param logId
     * @return
     */
    @Delete("delete from " + SysOperateLogEntity.TABLE_NAME + " where log_id < #{logId} and operate_time < #{operateTime} limit 1000")
    int deleteByLtLogId(@Param("logId") Long logId, @Param("operateTime") LocalDateTime operateTime);
}
