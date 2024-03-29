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

package io.innospots.libra.kernel.module.system.mapper;

import io.innospots.libra.kernel.module.system.entity.RoleResourceEntity;
import io.innospots.libra.kernel.module.system.model.role.resource.RoleResourceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author chenc
 * @date 2022/2/3
 */
@Mapper
public interface RoleResourceInfoMapper {

    RoleResourceInfoMapper INSTANCE = Mappers.getMapper(RoleResourceInfoMapper.class);

    RoleResourceEntity model2Entity(RoleResourceInfo roleResourceInfo);

    RoleResourceInfo entity2Info(RoleResourceEntity roleResourceEntity);
}