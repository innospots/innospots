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

package io.innospots.libra.kernel.module.todo.operator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.innospots.libra.kernel.module.todo.dao.TodoTaskTagDao;
import io.innospots.libra.kernel.module.todo.entity.TodoTaskTagEntity;
import io.innospots.libra.kernel.module.todo.model.TodoTask;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenc
 * @version 1.2.0
 * @date 2023/2/12
 */
@Service
public class TodoTaskTagOperator extends ServiceImpl<TodoTaskTagDao, TodoTaskTagEntity> {

    @Transactional(rollbackFor = Exception.class)
    public boolean createTodoTaskTag(TodoTask task) {
        boolean up = true;
        Set<String> tags = new HashSet<>(task.getTags());
        Set<String> newTags = new HashSet<>(tags);
        Set<Integer> removeTagIds = new HashSet<>();
        List<TodoTaskTagEntity> entities = this.listByTaskId(task.getTaskId());
        if (CollectionUtils.isNotEmpty(entities)) {
            for (TodoTaskTagEntity entity : entities) {
                newTags.remove(entity.getTagName());
                if (!tags.contains(entity.getTagName())) {
                    removeTagIds.add(entity.getTagId());
                }
            }
        }
        if (!newTags.isEmpty()) {
            List<TodoTaskTagEntity> newTodoTaskTags = new ArrayList<>();
            for (String newTag : newTags) {
                TodoTaskTagEntity todoTaskTagEntity = new TodoTaskTagEntity();
                todoTaskTagEntity.setTaskId(task.getTaskId());
                todoTaskTagEntity.setTagName(newTag);
                newTodoTaskTags.add(todoTaskTagEntity);
            }
            up = this.saveBatch(newTodoTaskTags);
        }

        if (!removeTagIds.isEmpty()) {
            up = this.removeByIds(removeTagIds) && up;
        }
        return up;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTodoTaskTag(Integer taskId) {
        QueryWrapper<TodoTaskTagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TodoTaskTagEntity::getTaskId, taskId);
        return this.remove(queryWrapper);
    }

    public List<TodoTaskTagEntity> listByTaskId(Integer taskId) {
        QueryWrapper<TodoTaskTagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TodoTaskTagEntity::getTaskId, taskId);
        return super.list(queryWrapper);
    }

    public List<TodoTaskTagEntity> listByTaskIds(List<Integer> taskIds) {
        QueryWrapper<TodoTaskTagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(TodoTaskTagEntity::getTaskId, taskIds);
        return super.list(queryWrapper);
    }

    public List<String> getTag() {
        List<TodoTaskTagEntity> groupList = super.list(
                new QueryWrapper<TodoTaskTagEntity>()
                        .select("TAG_NAME")
                        .groupBy("TAG_NAME"));
        return groupList.stream().map(TodoTaskTagEntity::getTagName).collect(Collectors.toList());
    }
}