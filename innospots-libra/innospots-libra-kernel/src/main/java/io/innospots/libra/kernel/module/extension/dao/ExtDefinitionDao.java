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

package io.innospots.libra.kernel.module.extension.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.innospots.libra.kernel.module.extension.entity.ExtDefinitionEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Smars
 * @date 2021/12/6
 */
public interface ExtDefinitionDao extends BaseMapper<ExtDefinitionEntity> {


    /**
     * get AppDefinitionEntity by appKey and last version
     *
     * @param appKey
     * @return
     */
    @Select("select * from " + ExtDefinitionEntity.TABLE_NAME + " where app_key=#{appKey} order by app_version desc limit 0,1")
    ExtDefinitionEntity getLastVersion(String appKey);


    @Select({
            "<script>",
            "select * from " + ExtDefinitionEntity.TABLE_NAME + " where app_key in",
            "<foreach collection='appKeys' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<ExtDefinitionEntity> selectByAppKeys(List<String> appKeys);
}
