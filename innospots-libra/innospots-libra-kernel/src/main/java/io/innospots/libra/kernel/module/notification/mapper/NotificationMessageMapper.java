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

package io.innospots.libra.kernel.module.notification.mapper;

import io.innospots.base.mapper.BaseConvertMapper;
import io.innospots.base.utils.DateTimeUtils;
import io.innospots.libra.kernel.module.notification.entity.NotificationMessageEntity;
import io.innospots.libra.kernel.module.notification.model.NotificationMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/1/15
 */
@Mapper
public interface NotificationMessageMapper extends BaseConvertMapper {

    NotificationMessageMapper INSTANCE = Mappers.getMapper(NotificationMessageMapper.class);

    @Mapping(target = "recordTime", expression = "java(toTimeString(notificationMessageEntity.getRecordTime()))")
    @Mapping(target = "time", expression = "java(toDateString(notificationMessageEntity.getRecordTime()))")
    NotificationMessage entity2Model(NotificationMessageEntity notificationMessageEntity);

    /**
     * time to string
     *
     * @param recordTime
     * @return String
     */
    default String toTimeString(LocalDateTime recordTime) {
        return DateTimeUtils.formatLocalDateTime(recordTime, null);
    }

    default String toDateString(LocalDateTime recordTime) {
        return DateTimeUtils.formatLocalDateTime(recordTime, DateTimeUtils.DEFAULT_DATE_PATTERN);
    }
}