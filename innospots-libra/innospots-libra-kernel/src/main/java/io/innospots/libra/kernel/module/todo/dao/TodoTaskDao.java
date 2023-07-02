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

package io.innospots.libra.kernel.module.todo.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import io.innospots.libra.kernel.module.todo.entity.TodoTaskEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Smars
 * @version 1.2.0
 * @date 2023/2/7
 */
public interface TodoTaskDao extends BaseMapper<TodoTaskEntity> {

    /**
     * 使用@Select注解，将SQL的where条件部分用${ew.customSqlSegment}代替
     */
    @Select("select s.* from sys_todo_task s left join sys_todo_task_tag t on s.task_id = t.task_id ${ew.customSqlSegment}")
    IPage<TodoTaskEntity> selectTaskPage(IPage<TodoTaskEntity> page, @Param(Constants.WRAPPER) Wrapper queryWrapper);
}