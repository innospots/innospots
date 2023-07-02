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

package io.innospots.libra.kernel.module.menu.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.innospots.libra.kernel.module.menu.entity.MenuResourceEntity;
import org.apache.ibatis.annotations.Update;


/**
 * @author chenc
 * @date 2021/2/7 17:27
 */
public interface MenuResourceDao extends BaseMapper<MenuResourceEntity> {


    /**
     * update status by appKey
     *
     * @param appKey
     * @param status
     * @return
     */
    @Update("update sys_menu_resource set status=#{status}, updated_time=now() where app_key=#{appKey}")
    int updateStatusByAppKey(String appKey, boolean status);
}
